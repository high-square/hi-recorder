package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.MessageForApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageForApplicationRepository extends JpaRepository<MessageForApplication, Long> {
}
