package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardImageRepository extends JpaRepository<BoardImage, String> {
}
