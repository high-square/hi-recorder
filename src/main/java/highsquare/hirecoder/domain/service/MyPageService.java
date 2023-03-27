package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.BoardRepository;
import highsquare.hirecoder.domain.repository.MemberRepository;
import highsquare.hirecoder.domain.repository.StudyMemberRepository;
import highsquare.hirecoder.entity.*;
import highsquare.hirecoder.page.PageRequestDto;
import highsquare.hirecoder.page.PageResultDto;
import highsquare.hirecoder.web.form.CommentSelectedForm;
import highsquare.hirecoder.web.form.MyStudyForm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static highsquare.hirecoder.constant.LikeCheckConstant.Like_Checked_Comment;
import static highsquare.hirecoder.constant.LikeCheckConstant.Like_Unchecked_Comment;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final StudyMemberRepository studyMemberRepository;
    private final MemberRepository memberRepository;

    /**
     * memberId -> studyMemberId -> study List 조회
     */
//    public List<Study> findMyStudy(Long memberId) {
//        return studyMemberRepository.findAllStudyByMemberId(memberId);
//    }

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
     * 회원 탈퇴
     */

    /**
     * memberId -> memberName 찾기
     */
    public String findNameById(Long memberId) {
        System.out.println("memberId = " + memberId);
        System.out.println("memberRepository.findNameById(memberId) = " + memberRepository.findNameById(memberId));
        return memberRepository.findNameById(memberId);
    }
}
