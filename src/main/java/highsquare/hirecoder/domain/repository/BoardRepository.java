package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Kind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface BoardRepository extends JpaRepository<Board, Long> {
    // 게시글 분류가
    @Query("select b from Board b join fetch b.study s where b.kind = :kind")
    List<Board> findStudyAll(@Param("kind") Kind kind);

}
