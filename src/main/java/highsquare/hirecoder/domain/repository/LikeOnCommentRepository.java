package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.LikeOnBoard;
import highsquare.hirecoder.entity.LikeOnComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeOnCommentRepository extends JpaRepository<LikeOnComment, Long> {

    @Query("select l from LikeOnComment l where l.comment.id =:commentId and l.member.id =:memberId")
    LikeOnComment findLikeOnComment(@Param("commentId") Long commentId, @Param("memberId") Long memberId);

    @Query("select count(l) from LikeOnComment l where l.comment.id =:commentId and l.member.id =:memberId and l.likeCheck=1")
    Integer countLikeCnt(@Param("commentId") Long commentId, @Param("memberId") Long memberId);
}
