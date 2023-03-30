package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.ApplyForStudyRepository;
import highsquare.hirecoder.domain.repository.MemberRepository;
import highsquare.hirecoder.domain.repository.StudyMemberRepository;
import highsquare.hirecoder.entity.ApplyForStudy;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Study;
import highsquare.hirecoder.entity.StudyMember;
import highsquare.hirecoder.page.PageRequestDto;
import highsquare.hirecoder.page.PageResultDto;
import highsquare.hirecoder.web.form.MyApplyStudyForm;
import highsquare.hirecoder.web.form.MyStudyForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final StudyMemberRepository studyMemberRepository;
    private final MemberRepository memberRepository;
    private final ApplyForStudyRepository applyForStudyRepository;

    /**
     * memberId -> studyMemberId -> study List 조회
     */
    public PageResultDto<MyStudyForm, Study> pagingMyStudy(Long memberId, PageRequestDto requestDto) {
        Pageable pageable = requestDto.getPageable(Sort.by("id").ascending());
        Page<Study> result = studyMemberRepository.findAllStudyByMemberId(memberId, pageable);

        Function<Study, MyStudyForm> fn = (entity -> studyToDto(entity));

        return new PageResultDto<>(result, fn);
    }

    private MyStudyForm studyToDto(Study entity) {
        MyStudyForm form = new MyStudyForm();
        form.setId(entity.getId());
        form.setName(entity.getName());
        form.setActivityState(entity.getActivityState());
        form.setStudyStartDate(entity.getStudyStartDate());
        form.setStudyFinishDate(entity.getStudyFinishDate());
        form.setCrewNumber(entity.getCrewNumber());
        form.setMeetingType(entity.getMeetingType());

        return form;
    }

    /**
     * memberId -> applyforstudy
     */
    public PageResultDto<MyApplyStudyForm, ApplyForStudy> pagingMyApplyingStudy(Long memberId, PageRequestDto requestDto) {
        Pageable pageable = requestDto.getPageable(Sort.by("id").ascending());
        Page<ApplyForStudy> result = applyForStudyRepository.findAllByMemberId(memberId, pageable);

        Function<ApplyForStudy, MyApplyStudyForm> fn = (entity -> applyStudyToDto(entity));

        return new PageResultDto<>(result, fn);
    }

    private MyApplyStudyForm applyStudyToDto(ApplyForStudy entity) {
        MyApplyStudyForm form = new MyApplyStudyForm();
        form.setId(entity.getId());
        form.setStudyId(entity.getStudy().getId());
        form.setStudyName(entity.getStudy().getName());
        form.setAuditstate(entity.getAuditstate());

        return form;
    }

    /**
     * 특정 study로 들어가면
     * 거기서 내가 쓴 글 조회
     * studyMember.study.id == board.study.id
     */
    public List<Board> findMyPosts(Long studyId, Long memberId) {
        return studyMemberRepository.findAllBoardByStudyIdAndMemberId(studyId, memberId);
    }

    /**
     * 스터디 탈퇴
     */
    @Transactional
    public void leaveStudy(Long studyId, Long memberId) {
        StudyMember studyMember = studyMemberRepository.findStudyMemberByStudyIdAndMemberId(studyId, memberId);
        studyMemberRepository.delete(studyMember);
    }

    /**
     * memberId -> memberName 찾기
     */
    public String findNameById(Long memberId) {
        return memberRepository.findNameById(memberId);
    }

}
