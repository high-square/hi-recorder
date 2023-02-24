package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.Member;
import highsquare.hirecoder.entity.Study;
import highsquare.hirecoder.entity.StudyMember;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StudyMemberRepositoryTest {
    @Autowired
    EntityManager em;
    @Autowired StudyMemberRepository studyMemberRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired StudyRepository studyRepository;

    List<Member> members;
    List<Study> studies;
    List<StudyMember> studyMembers;

    @BeforeEach
    public void setUp() {
        members = initMember();
        studies = initStudy();
        studyMembers = initStudyMember();

        em.flush();
        em.clear();

        System.out.println("<<<<<< 설정 종료 >>>>>>>>");
    }

    @Test
    @DisplayName("DB에 study에 속하는 member가 있는지 확인하는 메소드 테스트")
    public void existsByStudy_idAndMember_idTest() {

        // given

        // when

        // then

        assertThat(studyMemberRepository.existsMemberAndStudy(studies.get(0).getId(), members.get(0).getId()))
                .isTrue();
        assertThat(studyMemberRepository.existsMemberAndStudy(studies.get(0).getId(), members.get(1).getId()))
                .isTrue();
        assertThat(studyMemberRepository.existsMemberAndStudy(studies.get(0).getId(), members.get(2).getId()))
                .isFalse();
        assertThat(studyMemberRepository.existsMemberAndStudy(studies.get(1).getId(), members.get(0).getId()))
                .isTrue();
        assertThat(studyMemberRepository.existsMemberAndStudy(studies.get(1).getId(), members.get(1).getId()))
                .isFalse();
        assertThat(studyMemberRepository.existsMemberAndStudy(studies.get(1).getId(), members.get(2).getId()))
                .isTrue();
    }

    @Test
    @DisplayName("member가 속한 모든 스터디를 가져오는 테스트")
    public void getAllMembersStudy() throws Exception {

        // given


        // when
        List<StudyMember> allMembersStudy = studyMemberRepository.getAllMembersStudy(members.get(0).getId());

        // then
        Assertions.assertThat(allMembersStudy.size()).isEqualTo(2);
        Assertions.assertThat(allMembersStudy.stream().map(StudyMember::getStudy).map(Study::getId))
                .containsExactly(studies.get(0).getId(), studies.get(1).getId());

        em.flush();
        em.clear();


        allMembersStudy.get(0).getMember();
    }

    private List<Member> initMember() {

        List<Member> members = new ArrayList<>();

        members.add(new Member());
        members.add(new Member());
        members.add(new Member());

        members.stream().forEach(memberRepository::save);

        return members;
    }

    private List<Study> initStudy() {

        List<Study> studies = new ArrayList<>();
        studies.add(new Study());
        studies.add(new Study());

        studies.stream().forEach(studyRepository::save);

        return studies;
    }

    private List<StudyMember> initStudyMember() {

        List<StudyMember> studyMembers = new ArrayList<>();

        studyMembers.add(new StudyMember());
        studyMembers.add(new StudyMember());
        studyMembers.add(new StudyMember());
        studyMembers.add(new StudyMember());

        studyMembers.get(0).setStudy(studies.get(0));
        studyMembers.get(0).setMember(members.get(0));

        studyMembers.get(1).setStudy(studies.get(0));
        studyMembers.get(1).setMember(members.get(1));

        studyMembers.get(2).setStudy(studies.get(1));
        studyMembers.get(2).setMember(members.get(2));

        studyMembers.get(3).setStudy(studies.get(1));
        studyMembers.get(3).setMember(members.get(0));

        studyMembers.stream().forEach(studyMemberRepository::save);

        return studyMembers;
    }
}