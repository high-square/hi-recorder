package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.Member;
import highsquare.hirecoder.entity.Study;
import highsquare.hirecoder.entity.StudyMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StudyMemberRepositoryTest {
    @Autowired StudyMemberRepository studyMemberRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired StudyRepository studyRepository;

    @Test
    @DisplayName("DB에 study에 속하는 member가 있는지 확인하는 메소드 테스트")
    public void existsByStudy_idAndMember_idTest() {

        // given
        Member member1 = new Member();
        Member member2 = new Member();
        Member member3 = new Member();
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        Study study1 = new Study();
        Study study2 = new Study();
        studyRepository.save(study1);
        studyRepository.save(study2);

        // when

        StudyMember studyMember1 = new StudyMember();
        studyMember1.setStudy(study1);
        studyMember1.setMember(member1);

        StudyMember studyMember2 = new StudyMember();
        studyMember2.setStudy(study1);
        studyMember2.setMember(member2);

        StudyMember studyMember3 = new StudyMember();
        studyMember3.setStudy(study2);
        studyMember3.setMember(member3);

        StudyMember studyMember4 = new StudyMember();
        studyMember4.setStudy(study2);
        studyMember4.setMember(member1);

        studyMemberRepository.save(studyMember1);
        studyMemberRepository.save(studyMember2);
        studyMemberRepository.save(studyMember3);
        studyMemberRepository.save(studyMember4);

        // then

        assertThat(studyMemberRepository.doesMemberBelongToStudy(study1.getId(), member1.getId()))
                .isTrue();
        assertThat(studyMemberRepository.doesMemberBelongToStudy(study1.getId(), member2.getId()))
                .isTrue();
        assertThat(studyMemberRepository.doesMemberBelongToStudy(study1.getId(), member3.getId()))
                .isFalse();
        assertThat(studyMemberRepository.doesMemberBelongToStudy(study2.getId(), member1.getId()))
                .isTrue();
        assertThat(studyMemberRepository.doesMemberBelongToStudy(study2.getId(), member2.getId()))
                .isFalse();
        assertThat(studyMemberRepository.doesMemberBelongToStudy(study2.getId(), member3.getId()))
                .isTrue();
    }

}