package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.ApplyForStudy;
import highsquare.hirecoder.entity.AuditState;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ApplyForStudyRepository extends JpaRepository<ApplyForStudy, Long> {

    @Modifying
    @Query(value="insert into ApplyForStudy (member_id, study_id,auditState) values(:memberId,:studyId,:auditstate)", nativeQuery = true)
    // Native 쿼리라서 insert 구문에 apply_id에 해당하는 값을 넣어줘야함
    // 선택지 1. natvie 쿼리   2. repository.save(entity)
    void enrollApplyForStudy(@Param("studyId")Long studyId, @Param("memberId") Long memberId,@Param("auditstate") String auditState);


    @Modifying
    @Query("update ApplyForStudy ap set ap.auditstate=:auditState where ap.id=:applyForStudyId")
    void changeAuditState(@Param("applyForStudyId") Long applyForStudyId,@Param("auditState") AuditState auditState);


    @EntityGraph(attributePaths = {"study","member"})
    @Query("select ap from ApplyForStudy ap where ap.id=:applyForStudyId ")
    Optional<ApplyForStudy> getForCheckMemberStudy(@Param("applyForStudyId")Long applyForStudyId);

}
