package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.*;
import highsquare.hirecoder.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeOnCommentService {

    private final LikeOnCommentRepository likeOnCommentRepository;
    private final MemberRepository memberRepository;

    private final CommentRepository commentRepository;


    public LikeOnComment updateLike(Long commentId, Long memberId) {

        LikeOnComment findLike = likeOnCommentRepository.findLikeOnComment(commentId, memberId);

        // 해당 LikeOnBoard의 LikeCheck가 0이면 좋아요 선택 X, 1이면 좋아요 선택 O이니 값을 변경해줌
        // 변경감지를 이용해서 update해줌
        if (findLike.getLikeCheck()==0) {
            findLike.setLikeCheck(1);
        } else {
            findLike.setLikeCheck(0);
        }

        return findLike;
    }

    // 조회수를 클릭 or 취소할 때만 이 메소드가 호출되므로 여기 메소드에서 board의 likeCnt도 업데이트 하는 것이 좋아보임(팀원들의 의견 들어보기)
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
}