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
    @Query("update ApplyForStudy ap set ap.auditstate=:auditState where ap.id=:applyForStudyId")
    void changeAuditState(@Param("applyForStudyId") Long applyForStudyId,@Param("auditState") AuditState auditState);

    @EntityGraph(attributePaths = {"study","member"})
    @Query("select ap from ApplyForStudy ap where ap.id=:applyForStudyId ")
    Optional<ApplyForStudy> getForCheckMemberStudy(@Param("applyForStudyId")Long applyForStudyId);

    Optional<ApplyForStudy> findFirstByMember_IdAndStudy_Id(Long memberId, Long studyId);
}
