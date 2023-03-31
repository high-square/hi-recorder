package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.domain.repository.custom.MessageForApplicationRepositoryCustom;
import highsquare.hirecoder.entity.MessageForApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageForApplicationRepository extends JpaRepository<MessageForApplication, Long>, MessageForApplicationRepositoryCustom {
}
