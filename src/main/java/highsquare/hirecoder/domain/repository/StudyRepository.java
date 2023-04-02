package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.domain.repository.custom.StudyRepositoryCustom;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long>, StudyRepositoryCustom {

    @Query("select s from Study s where s.manager.id=:managerId")
    List<Study> findAllByManager_Id(@Param("managerId")Long managerId);

}
