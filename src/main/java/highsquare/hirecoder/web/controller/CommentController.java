package highsquare.hirecoder.web.controller;

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
import java.util.ArrayList;
import java.util.List;

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

        // 페이지 검증로직이 필요함(페이지 수, 사이즈가 음수인지 아닌지, boardId가 들어왔는지)

        // Paging에 필요한 데이터를 가지는 PageRequest 생성(page, size)
        PageRequestDto pageRequestDto = new PageRequestDto(page, size);


        // DB에서 board.id에 해당하는 PageResultDto<CommentSelectedForm>를 꺼내옴
        PageResultDto<CommentSelectedForm, Comment> allComments =
                commentService.pagingAllComments(boardId,(Long)session.getAttribute("memberId"), pageRequestDto);

        model.addAttribute("comments", allComments);
        model.addAttribute("boardId", boardId);

        return "boards/board :: #commentTable";
    }

    @GetMapping("/BestComments")
    public String getBestComments(@RequestParam("boardId") Long boardId, HttpSession session, Model model) {

        // Paging에 필요한 데이터를 가지는 PageRequest 생성(page, size)
        PageRequestDto pageRequestDto = new PageRequestDto(DEFAULT_PAGE, BEST_DEFAULT_SIZE);

        // DB에서 board.id에 해당하는 PageResultDto<CommentSelectedForm>를 꺼내옴
        PageResultDto<CommentSelectedForm, Comment> bestComments =
                commentService.pagingBestComments(boardId,(Long)session.getAttribute("memberId"), pageRequestDto);

        model.addAttribute("bestComments", bestComments);
        model.addAttribute("boardId", boardId);

        return "boards/board :: #bestCommentTable";
    }

    // 댓글 수정 클릭 시 작업
    @PatchMapping("/update/{commentId}")
    @ResponseBody
    public Long updateComment(@PathVariable("commentId") Long commentId,
                              @RequestParam String commentContent) {
        Long result = commentService.updateCommentContent(commentId, commentContent);
        if(result==0) {
            throw new IllegalArgumentException("해당 댓글이 존재하지 않습니다.");
        } else {
            return result;
        }

    }

    // 댓글 삭제 클릭 시 작업
    @DeleteMapping("/delete/{commentId}")
    @ResponseBody
    public void deleteComment(@PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);
    }

    // 댓글의 좋아요 클릭 시 작업
    @PostMapping("/like")
    @ResponseBody
    public List<Object> commentLikeProcess(@RequestParam(name="comment_id") Long commentId,
                                         @RequestParam(name="member_id") Long memberId) {
        LikeOnComment likeOnComment = likeOnCommentService.updateLike(commentId, memberId);
        Integer likeCnt = likeOnCommentService.countLikeCnt(commentId, memberId);
        List<Object> data = new ArrayList<>();
        data.add(String.valueOf(likeOnComment.getLikeCheck()));
        data.add(likeCnt);
        return data;
    }

    @GetMapping("/count")
    @ResponseBody
    public Integer commentsCountProcess(@RequestParam(name="board_id") Long boardId) {
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
