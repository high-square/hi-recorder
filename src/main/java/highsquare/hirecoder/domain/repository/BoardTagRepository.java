package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.BoardTag;
import highsquare.hirecoder.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardTagRepository extends JpaRepository<BoardTag, Long> {

    @Query("select b.tag from BoardTag b where b.board.id=:boardId")
    List<Tag> findTagByBoardId(@Param("boardId") Long boardId);

    public int deleteByBoard(Board board);

    @Query("SELECT bt.tag, COUNT(bt.tag) AS cnt FROM BoardTag bt GROUP BY bt.tag ORDER BY cnt DESC")
    List<Object[]> findTagCountByBoardTag();
}
