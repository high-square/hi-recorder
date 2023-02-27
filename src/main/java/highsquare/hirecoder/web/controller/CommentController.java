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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public String addComment(@RequestBody CommentSelectedForm commentForm, RedirectAttributes redirectAttributes, Model model) {
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


        // Paging에 필요한 데이터를 가지는 PageRequest 생성(page, size)
        PageRequestDto pageRequestDto = new PageRequestDto(page, size);


        // DB에서 board.id에 해당하는 PageResultDto<CommentSelectedForm>를 꺼내옴
        PageResultDto<CommentSelectedForm, Comment> allComments =
                commentService.pagingAllComments(boardId,(Long)session.getAttribute("memberId"), pageRequestDto);

        model.addAttribute("comments", allComments);
        model.addAttribute("boardId", boardId);

        return "boards/board :: #commentTable";
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
