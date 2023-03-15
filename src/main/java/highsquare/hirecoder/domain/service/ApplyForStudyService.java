package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.ApplyForStudyRepository;
import highsquare.hirecoder.entity.ApplyForStudy;
import highsquare.hirecoder.entity.AuditState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class ApplyForStudyService {

    private final ApplyForStudyRepository applyForStudyRepository;


    public void enrollApplyForStudy(Long studyId, Long memberId, String name) {
        applyForStudyRepository.enrollApplyForStudy(studyId,memberId,name);
    }

    public boolean isValidApplication(Long applyForStudyId) {
        if (applyForStudyId!=null) {
            ApplyForStudy applyForStudy = applyForStudyRepository.findById(applyForStudyId).orElse(null);

            if (applyForStudy==null) {
                return false;
            }

            if (applyForStudy.getAuditstate().equals(AuditState.대기)) {
                return true;
            }
        }

        return false;
    }

    public void approval(Long applyForStudyId) {
        applyForStudyRepository.changeAuditState(applyForStudyId,AuditState.승인);
    }

    public void reject(Long applyForStudyId) {
        applyForStudyRepository.changeAuditState(applyForStudyId,AuditState.거절);
    }
}

