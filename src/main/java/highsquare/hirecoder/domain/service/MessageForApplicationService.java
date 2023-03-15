package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.constant.SessionConstant;
import highsquare.hirecoder.domain.repository.ApplyForStudyRepository;
import highsquare.hirecoder.entity.ApplyForStudy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageForApplicationService {

    private final ApplyForStudyRepository applyForStudyRepository;


    /**
     * 메시지를 읽을 수 있는 자격조건 확인 기능
     */
    public boolean checkEligibility(Long applyForStudyId, HttpSession session) {
        ApplyForStudy findApplyForStudy = applyForStudyRepository.getForCheckMemberStudy(applyForStudyId).orElse(null);

        if (findApplyForStudy != null) {

            if (findApplyForStudy.getMember().getId() == session.getAttribute(SessionConstant.MEMBER_ID)
                    || findApplyForStudy.getStudy().getManager().getId() == session.getAttribute(SessionConstant.MEMBER_ID)) {
                return true;
            }
        }
        return false;

    }
}
