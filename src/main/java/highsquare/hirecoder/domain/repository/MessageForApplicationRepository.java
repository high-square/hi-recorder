package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.MessageForApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageForApplicationRepository extends JpaRepository<MessageForApplication, Long> {

    @Modifying
    @Query(value="insert into messageforapplication (message_id, appealMessage) values(:applyForStudyId,:appealMessage)", nativeQuery = true)
        // Native 쿼리라서 insert 구문에 apply_id에 해당하는 값을 넣어줘야함
    void addMessage(@Param("applyForStudyId")Long applyForStudyId, @Param("appealMessage") String appealMessage);

}
