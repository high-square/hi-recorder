package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.ApplyForStudyRepository;
import highsquare.hirecoder.domain.repository.MemberRepository;
import highsquare.hirecoder.domain.repository.MessageForApplicationRepository;
import highsquare.hirecoder.domain.repository.StudyRepository;
import highsquare.hirecoder.dto.ApplyInfo;
import highsquare.hirecoder.entity.*;
import highsquare.hirecoder.page.PageResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class ApplyForStudyService {

    private final ApplyForStudyRepository applyForStudyRepository;
    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;
    private final MessageForApplicationRepository messageForApplicationRepository;


    public ApplyForStudy enrollApplyForStudy(Long studyId, Long memberId, String name) {

        Study study = studyRepository.findById(studyId).get();
        Member member = memberRepository.findById(memberId).get();

        ApplyForStudy applyForStudy = new ApplyForStudy();
        applyForStudy.setMember(member);
        applyForStudy.setStudy(study);
        applyForStudy.setAuditstate(AuditState.대기);

        ApplyForStudy savedApplyForStudy = applyForStudyRepository.save(applyForStudy);
        return savedApplyForStudy;
    }

    public boolean isValidApplication(Long applyForStudyId) {

        if (applyForStudyId != null) {
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

    public void reject(Long applyForStudyId, String rejectMessage) {

        applyForStudyRepository.changeAuditState(applyForStudyId, AuditState.거절);
        MessageForApplication messageForApplication =
                messageForApplicationRepository.findByApplyForStudyIdWithApplyForStudy(applyForStudyId);

        messageForApplication.setRejectMessage(rejectMessage);
    }

    public Optional<ApplyForStudy> findApplyForStudyByMemberAndStudy(Long studyId, Long memberId) {
        return applyForStudyRepository.findByStudyIdAndMemberId(studyId, memberId);
    }

    public PageResultDto<ApplyInfo,?> ManageApplyForStudy(Long studyId, Pageable pageable) {
        Page<ApplyInfo> applyInfoPage = applyForStudyRepository.searchApplyInfo(studyId, pageable);

        return new PageResultDto<>(applyInfoPage);
    }
}

