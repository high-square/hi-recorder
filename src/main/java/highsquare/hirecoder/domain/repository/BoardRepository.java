package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BoardRepository extends JpaRepository<Board, Long> {


}
