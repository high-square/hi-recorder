package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.LikeOnBoard;
import highsquare.hirecoder.entity.LikeOnComment;
import highsquare.hirecoder.web.form.LikeCheckDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface LikeOnCommentRepository extends JpaRepository<LikeOnComment, Long> {

    @Query("select l from LikeOnComment l where l.comment.id =:commentId and l.member.id =:memberId")
    LikeOnComment findLikeOnComment(@Param("commentId") Long commentId, @Param("memberId") Long memberId);

    @Query("select count(l) from LikeOnComment l where l.comment.id =:commentId and l.likeCheck=1")
    Integer countLikeCnt(@Param("commentId") Long commentId);


    @Query("select  new highsquare.hirecoder.web.form.LikeCheckDto(l.comment.id, l.likeCheck) from LikeOnComment l " +
            "where l.comment.id IN (select c.id from Comment c where c.board.id=:boardId) and" +
            " l.member.id=:memberId")
    List<LikeCheckDto> commentWithLikeByMember(@Param("boardId") Long boardId, @Param("memberId") Long memberId);
}
