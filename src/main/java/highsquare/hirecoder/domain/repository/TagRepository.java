package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.domain.repository.custom.TagRepositoryCustom;
import highsquare.hirecoder.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long>, TagRepositoryCustom {

    public Optional<Tag> findByContent(String content);
}
