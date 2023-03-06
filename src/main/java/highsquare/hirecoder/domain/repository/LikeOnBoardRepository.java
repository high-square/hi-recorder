package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.domain.repository.custom.LikeOnBoardRepositoryCustom;
import highsquare.hirecoder.entity.Comment;
import highsquare.hirecoder.entity.LikeOnBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LikeOnBoardRepository extends JpaRepository<LikeOnBoard, Long>, LikeOnBoardRepositoryCustom {

    @Query("select l from LikeOnBoard l where l.board.id =:boardId and l.member.id =:memberId")
    LikeOnBoard findLikeOnBoard(@Param("boardId") Long boardId,@Param("memberId") Long memberId);

    @Query("select count(l) from LikeOnBoard l where l.board.id =:boardId and l.member.id =:memberId and l.likeCheck=1")
    Integer countLikeCnt(@Param("boardId") Long boardId, @Param("memberId") Long memberId);
}
