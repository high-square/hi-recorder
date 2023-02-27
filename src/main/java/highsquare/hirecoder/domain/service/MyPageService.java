package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.BoardRepository;
import highsquare.hirecoder.domain.repository.MemberRepository;
import highsquare.hirecoder.domain.repository.StudyMemberRepository;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Member;
import highsquare.hirecoder.entity.Study;
import highsquare.hirecoder.entity.StudyMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final StudyMemberRepository studyMemberRepository;

    /**
     * memberId -> studyMemberId -> study List 조회
     * entity 수정?
     * queryDsl? repository query?
     * 한 번에 조인?
     */
    public List<Study> findMyStudy(Long memberId) {
        return studyMemberRepository.findAllStudyByMemberId(memberId);
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
    public void deleteMember(Member member) {
        // 이름만 변경? or delete?
    }
}
