package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.domain.repository.custom.StudyRepositoryCustom;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long>, StudyRepositoryCustom {

    public List<Study> findAllByManager_Id(Long managerId);

}
