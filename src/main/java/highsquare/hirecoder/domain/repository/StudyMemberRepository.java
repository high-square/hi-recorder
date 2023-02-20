package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.domain.repository.custom.StudyMemberRepositoryCustom;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Member;
import highsquare.hirecoder.entity.Study;
import highsquare.hirecoder.entity.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long>, StudyMemberRepositoryCustom {
}
