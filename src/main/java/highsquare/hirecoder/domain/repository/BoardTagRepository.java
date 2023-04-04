package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.BoardTag;
import highsquare.hirecoder.entity.Kind;
import highsquare.hirecoder.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardTagRepository extends JpaRepository<BoardTag, Long> {

    @Query("select b.tag from BoardTag b where b.board.id=:boardId")
    List<Tag> findTagByBoardId(@Param("boardId") Long boardId);

    public int deleteByBoard(Board board);

    @Query("SELECT bt.tag, COUNT(bt.tag) AS cnt FROM BoardTag bt join fetch Board b on bt.board.id = b.id WHERE b.kind = :kind GROUP BY bt.tag ORDER BY cnt DESC")
    List<Object[]> findTagCountByBoardTag(@Param("kind") Kind kind);
}
