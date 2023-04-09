package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardImageRepository extends JpaRepository<BoardImage, String> {


    @Query("select  b from BoardImage b where b.board.id=:boardId")
    Optional<BoardImage> findByBoardId(@Param("boardId") Long boardId);
}
