package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Kind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface BoardRepository extends JpaRepository<Board, Long> {
    // kind가 RECRUIT일 때 OR CONTENT 일 때
    @Query("select b from Board b join fetch b.study s where b.kind = :kind")
    List<Board> findStudyAll(@Param("kind") Kind kind);

    List<Board> findByMemberIdInAndStudyIdAndKind(List<Long> memberIds, Long studyId, Kind kind);

}
