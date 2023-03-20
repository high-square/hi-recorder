package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.ApplyForStudy;
import highsquare.hirecoder.entity.AuditState;
import highsquare.hirecoder.entity.Member;
import highsquare.hirecoder.entity.Study;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
class ApplyForStudyRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    ApplyForStudyRepository applyForStudyRepository;

//    @Test
//    void enrollApplyForStudyTest() {
//        Member manager = new Member();
//        manager.setName("효진님");
//
//
//        Study study = new Study();
//        study.setName("SPRINGBOOT 스터디");
//        study.setManager(manager);
//
//        Member member = new Member();
//        member.setName("욱강욱");
//
//        em.persist(manager);
//        em.persist(study);
//        em.persist(member);
//
//        em.flush();
//        em.clear();
//
//        applyForStudyRepository.enrollApplyForStudy(study.getId(),member.getId(), AuditState.대기.name());
//
//        assertThat(!applyForStudyRepository.findAll().isEmpty());
//    }

    @Test
    void getForCheckMemberAndStudyTest() {

        Member member = new Member();
        member.setName("강욱님");

        Member manager = new Member();
        member.setName("매니저님");

        Study study = new Study();
        study.setName("JSP 스터디");
        study.setManager(manager);


        ApplyForStudy applyForStudy = new ApplyForStudy();
        applyForStudy.setStudy(study);
        applyForStudy.setMember(member);
        applyForStudy.setAuditstate(AuditState.대기);

        em.persist(member);
        em.persist(manager);
        em.persist(study);
        em.persist(applyForStudy);

        em.flush();
        em.clear();

        ApplyForStudy findApplyForStudy = applyForStudyRepository.getForCheckMemberStudy(applyForStudy.getId()).orElse(null);
        assertThat(findApplyForStudy.getStudy().getId()).isEqualTo(study.getId());
        assertThat(findApplyForStudy.getMember().getId()).isEqualTo(member.getId());
    }

}