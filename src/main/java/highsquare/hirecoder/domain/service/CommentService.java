package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.CommentRepository;
import highsquare.hirecoder.entity.Comment;
import highsquare.hirecoder.page.PageRequestDto;
import highsquare.hirecoder.page.PageResultDto;
import highsquare.hirecoder.web.form.CommentSelectedForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;


    public Long addComment(Comment comment) {
        Comment newComment = commentRepository.save(comment);
        return newComment.getId();
    }

    public PageResultDto<CommentSelectedForm, Comment> pagingAllComments(Long boardId,PageRequestDto requestDto) {
        Pageable pageable = requestDto.getPageable(Sort.by("createdTime").ascending());
        Page<Comment> result = commentRepository.findAllComments(boardId, pageable);

        Function<Comment, CommentSelectedForm> fn = (entity -> entityToDto(entity));

        return new PageResultDto<>(result, fn);
    }

    public PageResultDto<CommentSelectedForm, Comment> pagingBestComments(Long boardId,PageRequestDto requestDto) {
        Pageable pageable = requestDto.getPageable(Sort.by("likeCount").descending());
        Page<Comment> result = commentRepository.findBestComments(boardId, pageable);

        Function<Comment, CommentSelectedForm> fn = (entity -> entityToDto(entity));

        return new PageResultDto<>(result, fn);
    }


    /**
     * Comment 엔티티를 Form 객체로 변환하는 작업
     */
    private CommentSelectedForm entityToDto(Comment entity) {
        CommentSelectedForm form = new CommentSelectedForm();
        form.setId(entity.getId());
        form.setMemberName(entity.getMember().getName());
        form.setContent(entity.getContent());
        form.setBoardId(entity.getBoard().getId());
        form.setMemberId(entity.getMember().getId());
        form.setLikeCount(entity.getLikeCnt());
        return form;
    }


}
