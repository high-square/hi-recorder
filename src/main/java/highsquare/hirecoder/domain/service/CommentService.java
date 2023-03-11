package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.CommentRepository;
import highsquare.hirecoder.domain.repository.StudyMemberRepository;
import highsquare.hirecoder.entity.Comment;
import highsquare.hirecoder.page.PageRequestDto;
import highsquare.hirecoder.page.PageResultDto;
import highsquare.hirecoder.web.form.CommentSelectedForm;
import highsquare.hirecoder.web.form.CommentSelectedRecruitForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static highsquare.hirecoder.constant.LikeCheckConstant.Like_Checked_Comment;
import static highsquare.hirecoder.constant.LikeCheckConstant.Like_Unchecked_Comment;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;

    private final LikeOnCommentService likeOnCommentService;

    private final StudyMemberRepository studyMemberRepository;


    public Long addComment(Comment comment) {
        Comment newComment = commentRepository.save(comment);
        return newComment.getId();
    }


    /**
     * 전체 댓글 페이징(content)
     */
    public PageResultDto<CommentSelectedForm, Comment> pagingAllComments(Long boardId,Long memberId, PageRequestDto requestDto) {
        Pageable pageable = requestDto.getPageable(Sort.by("createdTime").descending());
        Page<Comment> result = commentRepository.findAllComments(boardId,pageable);

        //해당 멤버가 클릭한 좋아요 확인 작업
        Map<Long, Integer> commentLikeList = likeOnCommentService.commentWithLikeByMember(boardId,memberId);

        Function<Comment, CommentSelectedForm> fn = (entity -> entityToDto(entity,commentLikeList));

        return new PageResultDto<>(result, fn);
    }

    /**
     * 전체 댓글 페이징(recruit)
     */
    public PageResultDto<CommentSelectedRecruitForm, CommentSelectedRecruitForm> pagingAllCommentsRecruit(Long boardId,Long memberId, PageRequestDto requestDto) {
        Pageable pageable = requestDto.getPageable(Sort.by("createdTime").descending());
        Page<CommentSelectedRecruitForm> result = commentRepository.findAllCommentsRecruit(boardId, pageable);


        //해당 멤버가 클릭한 좋아요 확인 작업
        Map<Long, Integer> commentLikeList = likeOnCommentService.commentWithLikeByMember(boardId,memberId);

        Function<CommentSelectedRecruitForm, CommentSelectedRecruitForm> fn = (form -> setLikeCheckWithMember(form,commentLikeList));

        return new PageResultDto<>(result, fn);
    }


    /**
     * Best 댓글 페이징(content)
     */
    public PageResultDto<CommentSelectedForm, Comment> pagingBestComments(Long boardId,Long memberId,PageRequestDto requestDto) {
        Pageable pageable = requestDto.getPageable(Sort.by("likeCnt").descending());
        Page<Comment> result = commentRepository.findBestComments(boardId, pageable);

        //해당 멤버가 클릭한 좋아요 확인 작업
        Map<Long, Integer> commentLikeList = likeOnCommentService.commentWithLikeByMember(boardId,memberId);
        Function<Comment, CommentSelectedForm> fn = (entity -> entityToDto(entity,commentLikeList));


        return new PageResultDto<>(result, fn);
    }

    /**
     * Best 댓글 페이징(recruit)
     */
    public PageResultDto<CommentSelectedRecruitForm, CommentSelectedRecruitForm> pagingBestCommentsRecruit(Long boardId,Long memberId,PageRequestDto requestDto) {
        Pageable pageable = requestDto.getPageable(Sort.by("likeCnt").descending());
        Page<CommentSelectedRecruitForm> result = commentRepository.findBestCommentsRecruit(boardId, pageable);

        //해당 멤버가 클릭한 좋아요 확인 작업
        Map<Long, Integer> commentLikeList = likeOnCommentService.commentWithLikeByMember(boardId,memberId);
        Function<CommentSelectedRecruitForm, CommentSelectedRecruitForm> fn = (form -> setLikeCheckWithMember(form,commentLikeList));


        return new PageResultDto<>(result, fn);
    }


    /**
     * Comment 엔티티를 Form 객체로 변환하는 작업(content일때 CommentSelectedForm으로 전환)
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
        if (commentLikeList.get(entity.getId())==null || commentLikeList.get(entity.getId())==Like_Unchecked_Comment) {
            form.setLikeCheckWithMember(Like_Unchecked_Comment);
        } else {
            form.setLikeCheckWithMember(Like_Checked_Comment);
        }



        return form;
    }

    /**
     * CommentSelectedRecruitForm에 likeCheckWithMember를 setting 해주는 작업
     * (recruit일때)
     */
    private CommentSelectedRecruitForm setLikeCheckWithMember(CommentSelectedRecruitForm form,Map<Long, Integer> commentLikeList) {
        //좋아요 체크 로직
        if (commentLikeList.get(form.getId())==null || commentLikeList.get(form.getId())==Like_Unchecked_Comment) {
            form.setLikeCheckWithMember(Like_Unchecked_Comment);
        } else {
            form.setLikeCheckWithMember(Like_Checked_Comment);
        }

        return form;
    }



    /**
     * 게시물에 해당하는 댓글의 총 갯수 카운트
     */
    public Integer countComments(Long boardId) {
        return commentRepository.countTotalComments(boardId);
    }


    /**
     * 댓글의 내용 수정하기 작업
     */
    public void updateCommentContent(Long commentId, String commentContent) {
        commentRepository.updateCommentContent(commentId, commentContent);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteComment(commentId);
    }

    public boolean isExistComment(Long commentId) {
        return commentId!=null && commentRepository.findById(commentId).orElse(null)!=null;
    }

    /**
     * 해당 댓글의 작성자인지 확인하는 로직
     */
    public boolean isCommentWriter(Long commentId, Long memberId) {
        return commentRepository.isCommentWriter(commentId, memberId);
    }
}
