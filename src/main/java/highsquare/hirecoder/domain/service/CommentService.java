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
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;

    private final LikeOnCommentService likeOnCommentService;


    public Long addComment(Comment comment) {
        Comment newComment = commentRepository.save(comment);
        return newComment.getId();
    }

    //전체댓글 페이징 처리
    public PageResultDto<CommentSelectedForm, Comment> pagingAllComments(Long boardId,Long memberId, PageRequestDto requestDto) {
        Pageable pageable = requestDto.getPageable(Sort.by("createdTime").descending());
        Page<Comment> result = commentRepository.findAllComments(boardId,pageable);

        //해당 멤버가 클릭한 좋아요 확인 작업
        Map<Long, Integer> commentLikeList = likeOnCommentService.commentWithLikeByMember(boardId,memberId);

        Function<Comment, CommentSelectedForm> fn = (entity -> entityToDto(entity,commentLikeList));

        return new PageResultDto<>(result, fn);
    }

    //BEST댓글 페이징 처리
//    public PageResultDto<CommentSelectedForm, Comment> pagingBestComments(Long boardId,PageRequestDto requestDto) {
//        Pageable pageable = requestDto.getPageable(Sort.by("likeCount").descending());
//        Page<Comment> result = commentRepository.findBestComments(boardId, pageable);
//
//        Function<Comment, CommentSelectedForm> fn = (entity -> entityToDto(entity));
//
//        return new PageResultDto<>(result, fn);
//    }


    /**
     * Comment 엔티티를 Form 객체로 변환하는 작업
     */
    private CommentSelectedForm entityToDto(Comment entity,Map<Long, Integer> commentLikeList) {
        CommentSelectedForm form = new CommentSelectedForm();
        form.setId(entity.getId());
        form.setMemberName(entity.getMember().getName());
        form.setContent(entity.getContent());
        form.setBoardId(entity.getBoard().getId());
        form.setMemberId(entity.getMember().getId());
        form.setLikeCount(entity.getLikeCnt());

        //좋아요 체크 로직
        if (commentLikeList.get(entity.getId())==null || commentLikeList.get(entity.getId())==0) {
            form.setLikeCheckWithMember(0);
        } else {
            form.setLikeCheckWithMember(1);
        }



        return form;
    }

    /**
     * 게시물에 해당하는 댓글의 총 갯수 카운트
     */
    public Integer countComments(Long boardId) {
        return commentRepository.countTotalComments(boardId);
    }


}
