package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.MemberRepository;
import highsquare.hirecoder.domain.repository.StudyRepository;
import highsquare.hirecoder.dto.StudyCreationInfo;
import highsquare.hirecoder.entity.Member;
import highsquare.hirecoder.entity.Study;
import highsquare.hirecoder.entity.StudyMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StudyService {
    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;

    private final StudyMemberService studyMemberService;

    public boolean isTooManyToManage(Long memberId, int maxStudyCount) {
        return studyRepository.findAllByManager_Id(memberId).size() >= maxStudyCount;
    }


    @Transactional
    public Study createStudy(StudyCreationInfo info) {
        Optional<Member> optionalManager = memberRepository.findById(info.getManagerId());

        Study study = new Study(
              info.getStudyName(), info.getActivityState(),
              info.getRecruitState(), info.getStartDate(),
              info.getEndDate(), info.getCrewNumber(),
              info.getMeetingType(), optionalManager.orElse(null)
        );

        Study savedStudy = studyRepository.save(study);
        studyMemberService.saveStudyMember(savedStudy.getId(),info.getManagerId());

        return savedStudy;

    }

    public boolean isExistingStudy(Long studyId) {
        return studyId != null && studyRepository.findById(studyId).orElse(null)!=null;
    }

    public Long getStudyManagerId(Long studyId) {
        return studyRepository.findStudyManagerId(studyId);
    }

    public String getStudyNameById(Long studyId) {
        Study study = studyRepository.findById(studyId).get();
        return study.getName();
    }

    @Transactional
    public void deleteStudy(Study study) {
        studyRepository.delete(study);
    }
}

