package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.StudyMemberRepository;
import highsquare.hirecoder.entity.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StudyMemberService {
    private final StudyMemberRepository studyMemberRepository;

    public boolean doesMemberBelongToStudy(Long studyId, Long memberId) {
        return isIdNotNull(studyId, memberId) &&
                studyMemberRepository.existsMemberAndStudy(studyId, memberId);
    }

    public List<Study> getAllMembersStudy(Long memberId) {
        return null;
    }

    private boolean isIdNotNull(Long studyId, Long memberId) {
        return (studyId != null) && (memberId != null);
    }
}
