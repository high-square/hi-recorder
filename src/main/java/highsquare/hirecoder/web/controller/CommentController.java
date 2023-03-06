package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.constant.SessionConstant;
import highsquare.hirecoder.domain.repository.BoardRepository;
import highsquare.hirecoder.domain.repository.MemberRepository;
import highsquare.hirecoder.domain.service.CommentService;
import highsquare.hirecoder.domain.service.LikeOnCommentService;
import highsquare.hirecoder.entity.Comment;
import highsquare.hirecoder.entity.LikeOnComment;
import highsquare.hirecoder.page.PageRequestDto;
import highsquare.hirecoder.page.PageResultDto;
import highsquare.hirecoder.web.form.CommentSelectedForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static highsquare.hirecoder.constant.PageConstant.*;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentService commentService;

    private final LikeOnCommentService likeOnCommentService;

    @PostMapping
    public String addComment(@ModelAttribute("commentForm") CommentSelectedForm commentForm, BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             HttpSession session,Model model) {
        // 로그인 여부 확인 로직

        // <----- 검증 로직 시작
        // 댓글 길이 검증
        if(!commentForm.isContentTooShort(bindingResult)) {
            commentForm.isContentTooLong(bindingResult);
        }

        if (bindingResult.hasErrors()) {
            String errorCode = bindingResult.getFieldErrors("content").get(0).getCode();
            String content = (String)bindingResult.getFieldErrors("content").get(0).getRejectedValue();

            if (errorCode.startsWith("max")) {
                model.addAttribute("contentMaxLength", true);
            } else if (errorCode.startsWith("min")) {
                model.addAttribute("contentMinLength", true);
            }

            model.addAttribute("rejectedContent", content);
            getAllComments(DEFAULT_PAGE, DEFAULT_SIZE, commentForm.getBoardId(), session, model);
            return "boards/board ::#commentTable";
        }


        // <----- 검증 로직 종료

        // Comment 엔티티 생성 과정(Form -> Entity)
        Comment comment = transformToEntity(commentForm);

        // Comment 엔티티 DB에 저장
        commentService.addComment(comment);

        // CommentSelectedForm에서 boardId를 getAllComments에 넘겨줌
        redirectAttributes.addAttribute("boardId", commentForm.getBoardId());
        redirectAttributes.addAttribute("memberId", commentForm.getMemberId());


        return "redirect:/comments";
    }

    @GetMapping
    public String getAllComments(@RequestParam(defaultValue = ""+ DEFAULT_PAGE) int page,
                                 @RequestParam(defaultValue = ""+ DEFAULT_SIZE) int size,
                                 @RequestParam("boardId") Long boardId,
                                 HttpSession session,
                                 Model model) {
        // 로그인 여부 확인 로직

        // 해당 게시글이 존재하는지 확인 로직
        if (boardRepository.findById(boardId).orElse(null)==null) {
            model.addAttribute("notExistBoard", true);
        } else if (page<1 && size<1){ // 페이지 검증로직이 필요함(페이지 수, 사이즈가 음수인지 아닌지)
            model.addAttribute("notValidPageAndSize", true);
        } else {
            // Paging에 필요한 데이터를 가지는 PageRequest 생성(page, size)
            PageRequestDto pageRequestDto = new PageRequestDto(page, size);

            // DB에서 board.id에 해당하는 PageResultDto<CommentSelectedForm>를 꺼내옴
            PageResultDto<CommentSelectedForm, Comment> allComments =
                    commentService.pagingAllComments(boardId,(Long)session.getAttribute(SessionConstant.MEMBER_ID), pageRequestDto);

            model.addAttribute("comments", allComments);
            model.addAttribute("boardId", boardId);
        }


        return "boards/board :: #commentTable";
    }

    @GetMapping("/BestComments")
    public String getBestComments(@RequestParam("boardId") Long boardId, HttpSession session, Model model) {

        // 로그인 여부 확인 로직

        // 해당 게시글이 존재하는지 확인 로직
        if (boardRepository.findById(boardId).orElse(null)==null) {
            model.addAttribute("notExistBoard", true);
        } else {
            // Paging에 필요한 데이터를 가지는 PageRequest 생성(page, size)
            PageRequestDto pageRequestDto = new PageRequestDto(DEFAULT_PAGE, BEST_DEFAULT_SIZE);

            // DB에서 board.id에 해당하는 PageResultDto<CommentSelectedForm>를 꺼내옴
            PageResultDto<CommentSelectedForm, Comment> bestComments =
                    commentService.pagingBestComments(boardId, (Long) session.getAttribute(SessionConstant.MEMBER_ID), pageRequestDto);

            model.addAttribute("bestComments", bestComments);
            model.addAttribute("boardId", boardId);
        }

        return "boards/board :: #bestCommentTable";
    }

    // 댓글 수정 클릭 시 작업
    @PatchMapping("/update/{commentId}")
    @ResponseBody
    public Map<String,String> updateComment(@PathVariable("commentId") Long commentId,
                                            @RequestParam String commentContent,
                                            HttpSession session, Model model) {

        // 로그인 여부

        // <----- 검증 로직 시작
        Map<String, String> map = new HashMap<>();
        // 해당 댓글이 존재하는지 확인 로직
        if (!commentService.isExistComment(commentId)) {
            map.put("notExistComment", "해당 댓글이 존재하지 않습니다.");
        }

        // 댓글 작성자 본인인지 체크
        if (!commentService.isCommentWriter(commentId,(Long)session.getAttribute(SessionConstant.MEMBER_ID))) {
            map.put("notWriter", "해당 댓글의 작성자가 아닙니다.");
        }

        // 댓글 길이 검증
        if (commentContent.trim().length()==0) {
            map.put("blankContent", "댓글 내용이 빈칸입니다. 재입력이 필요합니다.");
        }

        if (commentContent.length()>200) {
            map.put("tooLongContent", "댓글의 최대 길이는 200자입니다.");
        }
        // <----- 검증 로직 종료



        if (map.isEmpty()) {
            commentService.updateCommentContent(commentId, commentContent);
        }


        return map;

    }

    // 댓글 삭제 클릭 시 작업
    @DeleteMapping("/delete/{commentId}")
    @ResponseBody
    public Map<String,String> deleteComment(@PathVariable("commentId") Long commentId,HttpSession session) {

        // 로그인 여부 확인 로직

        // <----- 검증 로직 시작
        Map<String, String> map = new HashMap<>();
        // 댓글이 존재하는지 확인 로직
        if (!commentService.isExistComment(commentId)) {
            map.put("notExistComment","해당 댓글이 존재하지 않습니다.");
        }

        // 댓글 작성자 본인인지 체크
        if (!commentService.isCommentWriter(commentId,(Long)session.getAttribute(SessionConstant.MEMBER_ID))) {
            map.put("notWriter", "해당 댓글의 작성자가 아닙니다.");
        }

        if (map.isEmpty()) {
            commentService.deleteComment(commentId);
        }

        return map;
    }

    // 댓글의 좋아요 클릭 시 작업
    @PostMapping("/like")
    @ResponseBody
    public Map<String,String> commentLikeProcess(@RequestParam(name="comment_id") Long commentId,
                                         @RequestParam(name="member_id") Long memberId,
                                           HttpSession session) {
        Map<String, String> map = new HashMap<>();

        // 댓글이 존재하는지 여부 확인 로직
        if(!commentService.isExistComment(commentId)) {
            map.put("notExistComment", "해당 댓글이 존재하지 않습니다.");
            return map;
        }

        // 로그인 여부 확인 로직

        if (memberId==null||!memberId.equals((Long)session.getAttribute(SessionConstant.MEMBER_ID))) {
            map.put("notLoginMember", "로그인이 필요한 작업입니다. 로그인 페이지로 이동하시겠습니까?");
        } else {
            LikeOnComment likeOnComment = likeOnCommentService.updateLike(commentId, memberId);
            Integer likeCnt = likeOnCommentService.countLikeCnt(commentId, memberId);
            map.put("likeCheck", String.valueOf(likeOnComment.getLikeCheck()));
            map.put("likeCnt", String.valueOf(likeCnt));
        }
        return map;

    }

    @GetMapping("/count")
    @ResponseBody
    public Integer commentsCountProcess(@RequestParam(name="board_id") Long boardId) {
        // 게시글 존재 여부 확인 로직 - 음수이면 존재하지 않음
        if(boardRepository.findById(boardId).orElse(null)==null) {
            return -1;
        }

         return commentService.countComments(boardId);
    }



    /**
     * 뷰에서 받아온 Form 객체를 Entity로 등록하기 전에 데이터 옮기는 작업
     */
    private Comment transformToEntity(CommentSelectedForm commentForm) {
        Comment comment = new Comment();
        comment.setMember(memberRepository.findById(commentForm.getMemberId()).get());
        comment.setBoard(boardRepository.findById(commentForm.getBoardId()).get());
        comment.setContent(commentForm.getContent());
        return comment;
    }



}
