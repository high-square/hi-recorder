package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {

    public List<Study> findAllByManager_Id(Long managerId);
}
