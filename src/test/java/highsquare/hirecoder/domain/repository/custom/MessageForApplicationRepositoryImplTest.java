package highsquare.hirecoder.domain.repository.custom;

import highsquare.hirecoder.domain.repository.ApplyForStudyRepository;
import highsquare.hirecoder.domain.repository.MessageForApplicationRepository;
import highsquare.hirecoder.entity.ApplyForStudy;
import highsquare.hirecoder.entity.MessageForApplication;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MessageForApplicationRepositoryImplTest {
    @Autowired
    ApplyForStudyRepository applyForStudyRepository;
    @Autowired
    MessageForApplicationRepository messageForApplicationRepository;

    @Test
    public void findByApplyForStudyIdWithApplyForStudyTest() {
        // given
        ApplyForStudy applyForStudy = new ApplyForStudy();
        applyForStudyRepository.save(applyForStudy);

        MessageForApplication messageForApplication = new MessageForApplication();
        messageForApplication.setAppealMessage("신청");
        messageForApplication.setRejectMessage("거절");
        messageForApplication.setApplyForStudy(applyForStudy);
        messageForApplicationRepository.save(messageForApplication);

        // when


        // then
        MessageForApplication foundMessageForApplication =
                messageForApplicationRepository.findByApplyForStudyIdWithApplyForStudy(applyForStudy.getId());

        Assertions.assertThat(foundMessageForApplication).isEqualTo(messageForApplication);
    }

}