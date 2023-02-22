package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.repository.BoardRepository;
import highsquare.hirecoder.domain.repository.MemberRepository;
import highsquare.hirecoder.domain.service.CommentService;
import highsquare.hirecoder.entity.Comment;
import highsquare.hirecoder.web.form.CommentSelectedForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentService commentService;

    @PostMapping
    public String addComment(@RequestBody CommentSelectedForm commentForm, Model model) {
        // Comment 엔티티 생성 과정(Form -> Entity)
        Comment comment = transformToEntity(commentForm);

        // Comment 엔티티 DB에 저장
        commentService.addComment(comment);

        // DB에서 board.id에 해당하는 Page<Comment>를 꺼내옴
        List<Comment> allComments = commentService.findAllByBoardId(commentForm.getBoardId());

        List<CommentSelectedForm> comments = transformToFormList(allComments);

//        PageResultDto<CommentSelectedForm, Comment> allComments =
//                commentService.pagingAllComments(commentForm.getBoardId(), new PageRequestDto(0, 10));
//
        model.addAttribute("comments", comments);

        return "boards/board :: #commentTable";

    }

    /**
     * DB에서 가져온 엔티티 List를 뷰에 전달하기위해 Form List 객체로 변환하는 작업
     */
    private static List<CommentSelectedForm> transformToFormList(List<Comment> comments) {
        List<CommentSelectedForm> commentsForm = comments.stream().map(o -> new CommentSelectedForm(o.getId(),
                o.getContent(), o.getLikeCount(), o.getMember().getId(), o.getBoard().getId())).collect(Collectors.toList());
        return commentsForm;
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
