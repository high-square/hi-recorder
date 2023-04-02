package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Member;
import highsquare.hirecoder.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select sm.member from StudyMember sm where sm.study.id = :studyId")
    List<Member> findByStudyId(@Param("studyId")Long studyId);

    Optional<Member> findByName(String name);

    @Query("select m.name from Member m where m.id = :memberId")
    String findNameById(@Param("memberId") Long memberId);
}
