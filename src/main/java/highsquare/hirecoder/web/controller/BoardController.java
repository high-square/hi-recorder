package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.repository.BoardImageRepository;
import highsquare.hirecoder.domain.repository.BoardRepository;
import highsquare.hirecoder.domain.repository.StudyMemberRepository;
import highsquare.hirecoder.domain.repository.StudyRepository;
import highsquare.hirecoder.domain.service.*;
import highsquare.hirecoder.entity.*;
import highsquare.hirecoder.page.PageRequestDto;
import highsquare.hirecoder.page.PageResultDto;
import highsquare.hirecoder.utils.ScriptUtils;
import highsquare.hirecoder.web.form.BoardSelectedForm;
import highsquare.hirecoder.web.form.CommentSelectedForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static highsquare.hirecoder.constant.MessageConstant.MAX_APPEALMESSAGE_LENGTH;
import static highsquare.hirecoder.constant.MessageConstant.MIN_APPEALMESSAGE_LENGTH;
import static highsquare.hirecoder.constant.PageConstant.*;
import static highsquare.hirecoder.constant.StudyCountConstant.STUDY_COUNT_MAX;

@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;

    private final LikeOnBoardService likeOnBoardService;

    private final StudyService studyService;

    private final BoardImageRepository boardImageRepository;


    private final BoardRepository boardRepository;

    private final TagService tagService;
    private final StudyMemberService studyMemberService;
    private final MessageForApplicationService messageForApplicationService;
    private final ApplyForStudyService applyForStudyService;

    private final StudyRepository studyRepository;


    @GetMapping("/{kind}/{study_id}/{board_id}")
    public String getBoard(@PathVariable("kind") Kind kind, @PathVariable("study_id") Long studyId,
                           @PathVariable("board_id") Long boardId, Model model,
                           Principal principal,
                           HttpServletRequest request, HttpServletResponse response) throws IOException {

        Long loginMemberId = Long.parseLong(principal.getName());


        //스터디 존재 여부
        if (!studyService.isExistingStudy(studyId)) {
            ScriptUtils.alertAndBackPage(response, "해당 스터디가 존재하지 않습니다.");
        }

        //게시글 존재 여부(스터디에 해당되는지도 체크)
        if (!boardService.isExistingBoard(boardId, studyId)) {
            ScriptUtils.alertAndBackPage(response, "해당 스터디에 게시글이 존재하지 않습니다.");
        }




        //전체 공개 여부에 따라 멤버가 읽을 수 있는지 없는지 여부
        if (!boardService.isPublic(boardId)) {

            if (!studyMemberService.doesMemberBelongToStudy(studyId, loginMemberId)) {
                ScriptUtils.alertAndBackPage(response, "해당 스터디에 해당되지 않습니다.");
            }
        }

        // DB에서 id에 해당하는 board를 꺼내옴
        Board board = boardService.getBoard(boardId, request, response);

        //Kind와 board의 kind가 같은지 비교
        if(!kind.equals(board.getKind())) {
            return "redirect:/boards/" + board.getKind() + "/{study_id}/{board_id}";
        }


        // 해당 스터디의 매니저 id를 꺼내옴
        if (kind.name().equals("RECRUIT")) {
            Long studyManagerId = studyService.getStudyManagerId(studyId);
            model.addAttribute("studyManagerId", studyManagerId);
        }


        // 세션에 저장된 member와 해당 게시글을 이용해서 LikeOnBoard 엔티티 가져오기
        LikeOnBoard likeOnBoard = likeOnBoardService.getLikeOnBoard(board.getId(), loginMemberId);

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

        PageResultDto<CommentSelectedForm, Comment> allComments =
                commentService.pagingAllComments(boardId, loginMemberId, pageRequestDto);

        PageResultDto<CommentSelectedForm, Comment> bestComments =
                commentService.pagingBestComments(boardId, loginMemberId, pageRequestDto);

        //게시글에 해당하는 총 댓글수 체크
        Integer commentsTotalCounts = commentService.countComments(boardId);

        // 스터디 모집 상태 체크
        Study findStudy = studyRepository.findById(studyId).get();
        if (findStudy.getRecruitState().name().equals("모집중")) {
            model.addAttribute("isRecruiting", true);
        } else {
            model.addAttribute("isRecruiting", false);
        }

        // view로 전달
        model.addAttribute("comments", allComments);
        model.addAttribute("bestComments", bestComments);
        model.addAttribute("board", boardForm);
        model.addAttribute("boardId", boardForm.getId());
        model.addAttribute("likeCheckBoard", likeOnBoard.getLikeCheck());
        model.addAttribute("studyId", studyId);
        model.addAttribute("tags", tags);
        model.addAttribute("commentsTotalCounts", commentsTotalCounts);
        model.addAttribute("memberId", loginMemberId);
        return "boards/board";
    }

    // 삭제하기를 누르면 해당 게시글을 삭제하고 그 게시글이 속했던 스터디 게시글 페이지로 이동하게 했음
    // 삭제 시 foreignKey 제약조건으로 인해 삭제가 안됨 -- 설정해야함

    @GetMapping("/{kind}/{study_id}/{board_id}/delete")
    public String deleteBoard(@PathVariable("study_id") Long studyId, @PathVariable("board_id") Long boardId, Principal principal
            , RedirectAttributes redirectAttributes, HttpServletResponse response) throws IOException {

        Long loginMemberId = Long.parseLong(principal.getName());


        if (!boardService.isMemberWriter(loginMemberId, boardId)) {
            ScriptUtils.alertAndBackPage(response, "게시글 작성자가 아니라 게시글 삭제가 불가능합니다.");
        } else {
            boardService.deleteBoard(boardId);
        }


        redirectAttributes.addAttribute("studyId", studyId);
        return "redirect:/study/{studyId}";
    }

    // 게시글의 좋아요 클릭 시 작업
    @PostMapping("/like")
    @ResponseBody
    public Map<String, String> boardLikeProcess(@RequestParam(name = "board_id") Long boardId, Principal principal) {

        Map<String, String> map = new HashMap<>();

        Long loginMemberId = Long.parseLong(principal.getName());


        if ((boardRepository.findById(boardId).orElse(null) == null)) { // 게시글 존재 여부 확인 로직
            map.put("notExistBoard", "해당 게시글이 존재하지 않습니다.");
        } else {
            LikeOnBoard likeOnBoard = likeOnBoardService.updateLike(boardId, loginMemberId);
            Integer likeCnt = likeOnBoardService.countLikeCnt(boardId);
            boardRepository.updateLikeCnt(boardId,likeCnt);
            map.put("likeCheck", String.valueOf(likeOnBoard.getLikeCheck()));
            map.put("likeCnt", String.valueOf(likeCnt));
        }

        return map;
    }


    /**
     * Board 엔티티를 BoardSelectedForm으로 변환하는 작업
     */
    private static BoardSelectedForm turnBoardEntityToForm(Board board) {
        BoardSelectedForm boardForm = new BoardSelectedForm(board.getId(), board.getMember().getId(), board.getMember().getName(),
                board.getStudy().getId(), board.getStudy().getName(), board.getTitle(), board.getContent(),
                board.getPublicYn(), board.getViewCnt(), board.getLikeCnt(), board.getKind().name(), board.getCreateDate(),
                board.getUpdateDate(), board.getHeadImageUrl());
        return boardForm;
    }

    /**
     * 일반 멤버가 스터디 신청하기 버튼 클릭시 신청테이블에 등록되는 로직
     * 스터디에 신청할 때 사유테이블에 신청사유를 작성
     * 우선 ScriptUtils를 이용하여 간단하게 로직 작성함(map에 오류를 넣어서 페이지로 반환시키든 리펙토링 필요)
     */
    @GetMapping("/RECRUIT/{studyId}/{boardId}/enroll")
    public String checkStudyMember(@PathVariable("studyId") Long studyId,
                                   Principal principal,
                                   HttpServletResponse response,
                                   Model model) throws IOException {

        Long loginMemberId = Long.parseLong(principal.getName());

        // 해당 스터디 존재 확인 로직
        if (!studyService.isExistingStudy(studyId)) {
            ScriptUtils.alertAndBackPage(response, " 해당 스터디가 존재하지 않습니다.");
        }

        if (!validateBelongedStudyCount(loginMemberId)) {
            ScriptUtils.alert(response,"가입할 수 있는 스터디 최대 갯수를 초과하셨습니다.");
        }

        validateMemberAttendState(studyId, loginMemberId, response);

        if (!validateNotEnrolled(studyId, loginMemberId)) {
            ScriptUtils.alertAndBackPage(response, "이미 신청한 스터디입니다.");
        }

        model.addAttribute("studyId", studyId);

        String studyName = studyService.getStudyNameById(studyId);
        model.addAttribute("studyName", studyName);
        return "form/messageForApply";
    }

    /**
     * 신청 테이블에 대기상태로 저장됨
     */
    @PostMapping("/RECRUIT/{studyId}/{boardId}/enroll")
    public String enrollApplyForStudy(@RequestParam("appealMessage") String appealMessage,
                                      @PathVariable("studyId") Long studyId, Principal principal,
                                      HttpServletResponse response,
                                      Model model) throws IOException {

        Long loginMemberId = Long.parseLong(principal.getName());

        // 해당 스터디와 멤버가 존재하는지 확인(로그인 검증에서 멤버 존재 확인)
        if (!studyService.isExistingStudy(studyId)) {
            ScriptUtils.alertAndBackPage(response, " 해당 스터디가 존재하지 않습니다.");
        }

        // 스터디 멤버 테이블에서 studyId와 memberId를 검색조건으로 AttendState 상태 들고 올텐데
        // 레코드가 존재하면 AttendState 상태에 따라 메시지를 보냄
        // 존재하지 않을 시 신청 테이블을 생성함
        if (validateMemberAttendState(studyId, loginMemberId, response)) {
            // 해당 멤버의 스터디 갯수 제한 검증 로직
            if (!validateBelongedStudyCount(loginMemberId)) {
                ScriptUtils.alert(response,"가입할 수 있는 스터디 최대 갯수를 초과하셨습니다.");
            }
        }

        // appealMessage 검증로직
        if (!validMessage(appealMessage, model)) {
            model.addAttribute("studyId", studyId);

            String studyName = studyService.getStudyNameById(studyId);
            model.addAttribute("studyName", studyName);
            return "form/messageForApply";
        }

        // 검증 로직 종료

        ApplyForStudy applyForStudy = applyForStudyService.enrollApplyForStudy(studyId, loginMemberId, AuditState.대기.name());
        messageForApplicationService.addMessage(applyForStudy, appealMessage);

        return "redirect:/study/myStudy";
    }

    private boolean validMessage(String appealMessage,Model model) {
        if (appealMessage.length()< MIN_APPEALMESSAGE_LENGTH) {
            model.addAttribute("invalidMessageLength", true);
            return false;
        }

        if (appealMessage.length()> MAX_APPEALMESSAGE_LENGTH) {
            model.addAttribute("invalidMessageLength", true);
            model.addAttribute("rejectedMessage", appealMessage.substring(0,MAX_APPEALMESSAGE_LENGTH-1));
            return false;
        }
        return true;
    }

    private boolean validateMemberAttendState(Long studyId, Long loginMemberId, HttpServletResponse response) throws IOException {

        if (!studyMemberService.doesMemberBelongToStudy(studyId, loginMemberId)) {

            return true;

        } else {
            AttendState attendState = studyMemberService.getAttendState(studyId, loginMemberId);

            switch (attendState) {
                case 참여 : {ScriptUtils.alertAndBackPage(response,"이미 가입하신 스터디입니다."); break; }
                case 탈퇴 : {ScriptUtils.alertAndBackPage(response,"탈퇴한 스터디는 가입이 불가능합니다."); break; }
                case 강퇴 : {ScriptUtils.alertAndBackPage(response,"강퇴당한 스터디는 가입이 불가능합니다."); break; }
            }

            return false;
        }
    }

    private boolean validateBelongedStudyCount(Long loginMemberId) throws IOException {
        int studyCount = studyMemberService.getBelongedStudyCount(loginMemberId);

        if(studyCount>=STUDY_COUNT_MAX) {
            return false;
        }

        return true;
    }

    /**
     * 이미 신청한 상태인지 확인하는 로직
     * @param studyId
     * @param loginMemberId
     */
    private boolean validateNotEnrolled(Long studyId, Long loginMemberId) {
        return applyForStudyService.findApplyForStudyByMemberAndStudy(studyId, loginMemberId).isEmpty();
    }
}
