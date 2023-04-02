package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.MemberRepository;
import highsquare.hirecoder.domain.repository.StudyMemberRepository;
import highsquare.hirecoder.domain.repository.StudyRepository;
import highsquare.hirecoder.dto.MemberInfo;
import highsquare.hirecoder.entity.AttendState;
import highsquare.hirecoder.entity.Member;
import highsquare.hirecoder.entity.Study;
import highsquare.hirecoder.entity.StudyMember;
import highsquare.hirecoder.page.PageResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyMemberService {
    private final StudyMemberRepository studyMemberRepository;
    private final StudyRepository studyRepository;
    private final MemberRepository memberRepository;

    public boolean doesMemberBelongToStudy(Long studyId, Long memberId) {
        return isIdNotNull(studyId, memberId) &&
                studyMemberRepository.existsMemberAndStudy(studyId, memberId)&&
                (studyMemberRepository.findAuditState(studyId, memberId).equals("참여"));
    }

    public List<Study> getAllMembersStudy(Long memberId) {

        List<StudyMember> allMembersStudy = new ArrayList<>();

        if (memberId != null) {
            allMembersStudy = studyMemberRepository.getAllMembersStudy(memberId);
        }
        return allMembersStudy.stream().map(StudyMember::getStudy).collect(Collectors.toList());
    }


    private boolean isIdNotNull(Long studyId, Long memberId) {
        return (studyId != null) && (memberId != null);
    }

    public void saveStudyMember(Long studyId, Long memberId) {
        Study study = studyRepository.findById(studyId).get();
        Member member = memberRepository.findById(memberId).get();

        StudyMember studyMember = new StudyMember();
        studyMember.setStudy(study);
        studyMember.setMember(member);
        studyMember.setAttendState(AttendState.참여);

        studyMemberRepository.save(studyMember);
    }

    public boolean checkMemberInStudy(Long studyMemberId) {
        if (studyMemberRepository.existsById(studyMemberId)) {
            StudyMember findStudyMember = studyMemberRepository.findById(studyMemberId).get();

            if(findStudyMember.getAttendState().name().equals("참여")) {
                return true;
            }
        }
        return false;
    }

    public void changeAttendState(Long studyMemberId, AttendState attendState) {
        StudyMember findStudyMember = studyMemberRepository.findById(studyMemberId).get();
        findStudyMember.setAttendState(attendState);
    }

    public AttendState getAttendState(Long studyId, Long memberId) {
        
        StudyMember studyMember = studyMemberRepository.findStudyMemberByStudyIdAndMemberId(studyId, memberId);
        
        if (studyMember != null) return studyMember.getAttendState();
        else return null;
    }

    public int getBelongedStudyCount(Long memberId) {
        return studyMemberRepository.checkMembersStudyCount(memberId);
    }

    public PageResultDto<MemberInfo, ?> ManageStudyMember(Long studyId, Pageable pageable) {
        return new PageResultDto<>(studyMemberRepository.searchStudyMemberInfo(studyId, pageable));
    }
}
