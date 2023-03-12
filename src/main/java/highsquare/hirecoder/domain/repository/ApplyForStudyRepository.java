package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.ApplyForStudy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface ApplyForStudyRepository extends JpaRepository<ApplyForStudy, Long> {

    @Modifying
    @Query(value="insert into ApplyForStudy (member_id, study_id,auditState) values(:memberId,:studyId,:auditstate)", nativeQuery = true)
    // Native 쿼리라서 insert 구문에 apply_id에 해당하는 값을 넣어줘야함
    // 선택지 1. natvie 쿼리   2. repository.save(entity)
    void enrollApplyForStudy(@Param("studyId")Long studyId, @Param("memberId") Long memberId,@Param("auditstate") String auditState);


}
