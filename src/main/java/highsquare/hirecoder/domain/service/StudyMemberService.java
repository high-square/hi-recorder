package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.StudyMemberRepository;
import highsquare.hirecoder.entity.Member;
import highsquare.hirecoder.entity.Study;
import highsquare.hirecoder.entity.StudyMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyMemberService {
    private final StudyMemberRepository studyMemberRepository;

    public boolean doesMemberBelongToStudy(Long studyId, Long memberId) {
        return isIdNotNull(studyId, memberId) &&
                studyMemberRepository.existsMemberAndStudy(studyId, memberId);
    }

    public List<Study> getAllMembersStudy(Long memberId) {

        List<StudyMember> allMembersStudy = new ArrayList<>();

        if (memberId != null) {
            allMembersStudy = studyMemberRepository.getAllMembersStudy(memberId);
        }
        return allMembersStudy.stream().map(StudyMember::getStudy).collect(Collectors.toList());
    }

    public void registerMemberToStudy(Long studyId, Long memberId) {
        Study study = new Study();
        study.setId(studyId);

        Member member = new Member();
        member.setId(memberId);

        StudyMember studyMember = new StudyMember();
        studyMember.setStudy(study);
        studyMember.setMember(member);

        studyMemberRepository.save(studyMember);
    }

    private boolean isIdNotNull(Long studyId, Long memberId) {
        return (studyId != null) && (memberId != null);
    }
}
