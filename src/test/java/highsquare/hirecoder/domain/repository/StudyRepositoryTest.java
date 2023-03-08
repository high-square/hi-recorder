package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.Member;
import highsquare.hirecoder.entity.Study;
import org.hibernate.query.criteria.internal.expression.MapEntryExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class StudyRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    MemberRepository memberRepository;

    List<Member> members = new ArrayList<>();

    List<Study> studies = new ArrayList<>();

    @BeforeEach
    public void initData() {
        Member member1 = new Member();
        member1.setName("김강욱");
        member1.setEmail("naver.com");
        member1.setPassword("123");

        Member member2 = new Member();
        member2.setName("정강욱");
        member2.setEmail("naver.com");
        member2.setPassword("123");

        Member member3 = new Member();
        member3.setName("심강욱");
        member3.setEmail("naver.com");
        member3.setPassword("123");

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        members.add(member1);
        members.add(member2);
        members.add(member3);

        Study study1 = new Study();
        study1.setManager(member1);

        Study study2 = new Study();
        study2.setManager(member2);

        studyRepository.save(study1);
        studyRepository.save(study2);

        studies.add(study1);
        studies.add(study2);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("스터디의 매니저 id 찾는 테스트")
    public void findStudyManagerId() {

        assertThat(studyRepository.findStudyManagerId(studies.get(0).getId())).isEqualTo(members.get(0).getId());
        assertThat(studyRepository.findStudyManagerId(studies.get(1).getId())).isEqualTo(members.get(1).getId());
    }

}