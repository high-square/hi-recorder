package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.AuditState;
import highsquare.hirecoder.entity.Member;
import highsquare.hirecoder.entity.Study;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;


@DataJpaTest
class ApplyForStudyRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    ApplyForStudyRepository applyForStudyRepository;

    @Test
    void enrollApplyForStudyTest() {
        Member manager = new Member();
        manager.setName("효진님");


        Study study = new Study();
        study.setName("SPRINGBOOT 스터디");
        study.setManager(manager);

        Member member = new Member();
        member.setName("욱강욱");

        em.persist(manager);
        em.persist(study);
        em.persist(member);

        em.flush();
        em.clear();

        applyForStudyRepository.enrollApplyForStudy(study.getId(),member.getId(), AuditState.대기.name());

        Assertions.assertThat(!applyForStudyRepository.findAll().isEmpty());
    }

}