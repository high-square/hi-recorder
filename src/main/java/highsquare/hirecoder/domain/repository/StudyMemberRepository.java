package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.domain.repository.custom.StudyMemberRepositoryCustom;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Comment;
import highsquare.hirecoder.entity.Study;
import highsquare.hirecoder.entity.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long>, StudyMemberRepositoryCustom {

    @Query("select sm.study from StudyMember sm where sm.member.id =:memberId and sm.attendState='참여'")
    List<Study> findAllStudyByMemberId(@Param("memberId") Long memberId);

    @Query("select b from Board b where b.study.id =:studyId and b.member.id =:memberId")
    List<Board> findAllBoardByStudyIdAndMemberId(@Param("studyId") Long studyId, @Param("memberId") Long memberId);

    @Query("select sm.study from StudyMember sm where sm.member.id =:memberId and sm.study.id =:studyId and sm.attendState='참여'")
    StudyMember findStudyMemberByStudyIdAndMemberId(@Param("studyId") Long studyId, @Param("memberId") Long memberId);

    @Query("select sm.attendState from StudyMember sm where sm.study.id=:studyId and sm.member.id=:memberId")
    String findAuditState(@Param("studyId")Long studyId, @Param("memberId") Long memberId);

    @Query("select count(sm) from StudyMember sm where sm.member.id=:memberId and sm.attendState='참여'")
    int checkMembersStudyCount(@Param("memberId")Long memberId);

    @Query("select count(sm) from StudyMember sm where sm.study.id=:studyId and sm.attendState='참여'")
    int checkStudysMemberCount(@Param("studyId")Long studyId);
}
