package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.ApplyForStudyRepository;
import highsquare.hirecoder.domain.repository.MemberRepository;
import highsquare.hirecoder.domain.repository.StudyRepository;
import highsquare.hirecoder.entity.ApplyForStudy;
import highsquare.hirecoder.entity.AuditState;
import highsquare.hirecoder.entity.Member;
import highsquare.hirecoder.entity.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class ApplyForStudyService {

    private final ApplyForStudyRepository applyForStudyRepository;
    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;


    public Long enrollApplyForStudy(Long studyId, Long memberId, String name) {

        Study study = studyRepository.findById(studyId).get();
        Member member = memberRepository.findById(memberId).get();

        ApplyForStudy applyForStudy = new ApplyForStudy();
        applyForStudy.setMember(member);
        applyForStudy.setStudy(study);
        applyForStudy.setAuditstate(AuditState.대기);

        ApplyForStudy savedApplyForStudy = applyForStudyRepository.save(applyForStudy);
        return savedApplyForStudy.getId();
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

