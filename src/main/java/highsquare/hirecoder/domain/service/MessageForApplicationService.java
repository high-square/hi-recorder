package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.constant.SessionConstant;
import highsquare.hirecoder.domain.repository.ApplyForStudyRepository;
import highsquare.hirecoder.domain.repository.MessageForApplicationRepository;
import highsquare.hirecoder.entity.ApplyForStudy;
import highsquare.hirecoder.entity.MessageForApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageForApplicationService {

    private final ApplyForStudyRepository applyForStudyRepository;

    private final MessageForApplicationRepository messageForApplicationRepository;


    /**
     * 메시지를 읽을 수 있는 자격조건 확인 기능
     */
    public boolean checkEligibility(Long applyForStudyId, Long memberId) {
        ApplyForStudy findApplyForStudy = applyForStudyRepository.getForCheckMemberStudy(applyForStudyId).orElse(null);

        if (findApplyForStudy != null) {

            if (findApplyForStudy.getMember().getId() == memberId
                    || findApplyForStudy.getStudy().getManager().getId() == memberId) {
                return true;
            }
        }
        return false;

    }

    public void addMessage(Long applyForStudyId, String appealMessage) {
        messageForApplicationRepository.addMessage(applyForStudyId,appealMessage);
    }
}
