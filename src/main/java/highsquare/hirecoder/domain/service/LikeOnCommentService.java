package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.*;
import highsquare.hirecoder.entity.*;
import highsquare.hirecoder.web.form.LikeCheckDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static highsquare.hirecoder.constant.LikeCheckConstant.Like_Checked_Comment;
import static highsquare.hirecoder.constant.LikeCheckConstant.Like_Unchecked_Comment;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeOnCommentService {

    private final LikeOnCommentRepository likeOnCommentRepository;
    private final MemberRepository memberRepository;

    private final CommentRepository commentRepository;


    public LikeOnComment updateLike(Long commentId, Long memberId) {

        LikeOnComment findLike = getLikeOnComment(commentId, memberId);

        // 해당 LikeOnBoard의 LikeCheck가 0이면 좋아요 선택 X, 1이면 좋아요 선택 O이니 값을 변경해줌
        // 변경감지를 이용해서 update해줌
        if (findLike.getLikeCheck()==Like_Unchecked_Comment) {
            findLike.setLikeCheck(Like_Checked_Comment);
        } else {
            findLike.setLikeCheck(Like_Unchecked_Comment);
        }

        return findLike;
    }

    public Integer countLikeCnt(Long commentId, Long memberId) {
        Integer likeCnt = likeOnCommentRepository.countLikeCnt(commentId, memberId);
        Comment findComment = commentRepository.findById(commentId).get();
        findComment.setLikeCnt(likeCnt);
        return likeCnt;
    }

    public LikeOnComment getLikeOnComment(Long commentId, Long memberId) {
        LikeOnComment findLike = likeOnCommentRepository.findLikeOnComment(commentId, memberId);

        // 해당 게시글과 해당 멤버의 좋아요 엔티티가 존재하지 않을 시 새로 만드는 작업
        if (findLike==null) {
            return likeOnCommentRepository.save(makeNewLike(commentId, memberId));
        }

        return findLike;
    }

    private LikeOnComment makeNewLike(Long commentId, Long memberId) {
        LikeOnComment likeOnComment = new LikeOnComment();
        Member member = memberRepository.findById(memberId).get();
        Comment comment = commentRepository.findById(commentId).get();
        likeOnComment.setMember(member);
        likeOnComment.setComment(comment);
        return likeOnComment;
    }

    //로그인한 멤버가 해당 게시글의 댓글 중 좋아요를 누른 댓글 목록을 Map 형식으로 반환하는 작업
    public Map<Long,Integer> commentWithLikeByMember(Long boardId, Long memberId) {
        List<LikeCheckDto> likeCheck = likeOnCommentRepository.commentWithLikeByMember(boardId, memberId);
        Map<Long, Integer> likeCheckMap = new HashMap<>();
        for (LikeCheckDto likeCheckDto : likeCheck) {
            likeCheckMap.put(likeCheckDto.getCommentId(), likeCheckDto.getLikeCheck());
        }
        return likeCheckMap;
    }
}