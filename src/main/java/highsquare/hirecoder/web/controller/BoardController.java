package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.constant.SessionConstant;
import highsquare.hirecoder.domain.repository.BoardRepository;
import highsquare.hirecoder.domain.repository.StudyMemberRepository;
import highsquare.hirecoder.domain.service.*;
import highsquare.hirecoder.entity.*;
import highsquare.hirecoder.page.PageRequestDto;
import highsquare.hirecoder.page.PageResultDto;
import highsquare.hirecoder.utils.ScriptUtils;
import highsquare.hirecoder.web.form.BoardSelectedForm;
import highsquare.hirecoder.web.form.CommentSelectedForm;
import highsquare.hirecoder.web.form.CommentSelectedRecruitForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static highsquare.hirecoder.constant.PageConstant.*;

@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;

    private final LikeOnBoardService likeOnBoardService;

    private final StudyService studyService;

    private final StudyMemberRepository studyMemberRepository;

    private final BoardRepository boardRepository;

    private final TagService tagService;


    @GetMapping("/{kind}/{study_id}/{board_id}")
    public String getBoard(@PathVariable("kind") Kind kind,
                           @PathVariable("study_id") Long studyId,
                           @PathVariable("board_id") Long boardId, Model model,
                           HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 원래라면 로그인 작업에서 session을 생성할텐데 지금 페이지가 없으므로 true를 넣어서 하나 생성해주겠음
        HttpSession session = request.getSession(true);
        // 현재 로그인에 관한 페이지가 없으므로 session 값에 강욱 멤버의 memberId를 임의로 넣어두겠음
        // 현재 로그인한 멤버의 name도 넣어두겠음
        session.setAttribute(SessionConstant.MEMBER_ID,1L);
        session.setAttribute("memberName", "강욱");


        //스터디 존재 여부
        if (!studyService.isExistingStudy(studyId)) {
            ScriptUtils.alertAndBackPage(response,"해당 스터디가 존재하지 않습니다.");
        }

        //게시글 존재 여부(스터디에 해당되는지도 체크)
        if (!boardService.isExistingBoard(boardId,studyId)) {
            ScriptUtils.alertAndBackPage(response,"해당 스터디에 게시글이 존재하지 않습니다.");
        }


        //전체 공개 여부에 따라 멤버가 읽을 수 있는지 없는지 여부
        if (!boardService.isPublic(boardId)) {
            Long memberId = (Long) session.getAttribute(SessionConstant.MEMBER_ID);

            if(!studyMemberRepository.existsMemberAndStudy(studyId, memberId)) {
                ScriptUtils.alertAndBackPage(response,"해당 스터디에 해당되지 않습니다.");
            }
        }

        // DB에서 id에 해당하는 board를 꺼내옴
        Board board = boardService.getBoard(boardId,request,response);
        // 해당 스터디의 매니저 id를 꺼내옴
        if (kind.name().equals("RECRUIT")) {
            Long studyManagerId = studyService.getStudyManagerId(studyId);
            model.addAttribute("studyManagerId", studyManagerId);
        }


        // 세션에 저장된 member와 해당 게시글을 이용해서 LikeOnBoard 엔티티 가져오기
        LikeOnBoard likeOnBoard = likeOnBoardService.getLikeOnBoard(board.getId(), (Long)session.getAttribute(SessionConstant.MEMBER_ID));

        // board 엔티티를 boardForm으로 변환
        BoardSelectedForm boardForm = turnBoardEntityToForm(board);

        // boardId를 이용해서 게시글에 해당되는 태그 가져오기
        // 간단해서 DTO를 생략하고 엔티티를 바로 반환함
        List<Tag> tags = tagService.getTags(boardId);


        // 댓글 가져오기 작업
        // Paging에 필요한 데이터를 가지는 PageRequest 생성(page, size)
        PageRequestDto pageRequestDto = new PageRequestDto(DEFAULT_PAGE, DEFAULT_SIZE);

        // DB에서 board.id에 해당하는 PageResultDto<CommentSelectedForm>를 꺼내옴
        // DB에서 board.id에 해당하는 Best 댓글순으로 꺼내옴
        if (kind.name().equals("CONTENT")) {
            PageResultDto<CommentSelectedForm, Comment> allComments =
                    commentService.pagingAllComments(boardId,
                            (Long)session.getAttribute(SessionConstant.MEMBER_ID),pageRequestDto);

            PageResultDto<CommentSelectedForm, Comment> bestComments =
                    commentService.pagingBestComments(boardId,
                            (Long)session.getAttribute(SessionConstant.MEMBER_ID),pageRequestDto);

            model.addAttribute("comments", allComments);
            model.addAttribute("bestComments", bestComments);
        } else if (kind.name().equals("RECRUIT")) {
            PageResultDto<CommentSelectedRecruitForm, CommentSelectedRecruitForm> allComments =
                    commentService.pagingAllCommentsRecruit(boardId,
                            (Long)session.getAttribute(SessionConstant.MEMBER_ID),pageRequestDto);

            PageResultDto<CommentSelectedForm, Comment> bestComments =
                    commentService.pagingBestComments(boardId,
                            (Long)session.getAttribute(SessionConstant.MEMBER_ID),pageRequestDto);
            model.addAttribute("comments", allComments);
            model.addAttribute("bestComments", bestComments);
        }


        //게시글에 해당하는 총 댓글수 체크
        Integer commentsTotalCounts = commentService.countComments(boardId);



        // view로 전달
        model.addAttribute("board", boardForm);
        model.addAttribute("boardId", boardForm.getId());
        model.addAttribute("likeCheckBoard", likeOnBoard.getLikeCheck());
        model.addAttribute("studyId", studyId);
        model.addAttribute("tags", tags);
        model.addAttribute("commentsTotalCounts", commentsTotalCounts);
        model.addAttribute("kind", kind);

        return "boards/board";
    }

    // 삭제하기를 누르면 해당 게시글을 삭제하고 그 게시글이 속했던 스터디 게시글 페이지로 이동하게 했음
    // 삭제 시 foreignKey 제약조건으로 인해 삭제가 안됨 -- 설정해야함

    @GetMapping("/{kind}/{study_id}/{board_id}/delete")
    public String deleteBoard(@PathVariable("study_id") Long studyId,
                              @PathVariable("board_id") Long boardId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes,
                              HttpServletResponse response) throws IOException{

        if(!boardService.isMemberWriter((Long)session.getAttribute(SessionConstant.MEMBER_ID), boardId)) {
           ScriptUtils.alertAndBackPage(response,"게시글 작성자가 아니라 게시글 삭제가 불가능합니다.");
        } else {
            boardService.deleteBoard(boardId);
        }


        redirectAttributes.addAttribute("studyId", studyId);
        return "redirect:/study/{studyId}";
    }

    // 게시글의 좋아요 클릭 시 작업
    @PostMapping("/like")
    @ResponseBody
    public Map<String,String> boardLikeProcess(@RequestParam(name="board_id") Long boardId,
                                               @RequestParam(name="member_id") Long memberId,
                                               HttpSession session) {

        Map<String, String> map = new HashMap<>();

        // 로그인 여부 확인 로직
        if (memberId == null || !memberId.equals((Long)session.getAttribute(SessionConstant.MEMBER_ID))) {
            map.put("notLoginMember", "로그인이 필요한 작업입니다. 로그인 페이지로 이동하시겠습니까?");
        } else if ((boardRepository.findById(boardId).orElse(null) == null)) { // 게시글 존재 여부 확인 로직
            map.put("notExistBoard","해당 게시글이 존재하지 않습니다.");
        } else {
            if (!likeOnBoardService.isExistingLikeOnBoard(boardId, memberId)) {
                // LikeOnBoard 새로 생성
                likeOnBoardService.getLikeOnBoard(boardId, memberId);
            }


            LikeOnBoard likeOnBoard = likeOnBoardService.updateLike(boardId, memberId);
            Integer likeCnt = likeOnBoardService.countLikeCnt(boardId, memberId);
            map.put("likeCheck",String.valueOf(likeOnBoard.getLikeCheck()));
            map.put("likeCnt", String.valueOf(likeCnt));
        }

        return map;
    }




    /**
     * Board 엔티티를 BoardSelectedForm으로 변환하는 작업
     */
    private static BoardSelectedForm turnBoardEntityToForm(Board board) {
        BoardSelectedForm boardForm =
                new BoardSelectedForm(board.getId(), board.getMember().getId(), board.getMember().getName(),
                        board.getStudy().getId(), board.getStudy().getName(), board.getTitle(),
                        board.getContent(), board.getFile(), board.getPublicYn(), board.getViewCnt(), board.getLikeCnt(), board.getKind().name()
                        ,LocalDateTime.now(),LocalDateTime.now());
        return boardForm;
    }
}
