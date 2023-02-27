package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.BoardRepository;
import highsquare.hirecoder.domain.repository.MemberRepository;
import highsquare.hirecoder.domain.repository.StudyRepository;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Kind;
import highsquare.hirecoder.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardListService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    //로그인한 회원 중 스터디에 참여하고 있는 멤버가, 해당 스터디를 눌렀을 때 해당 스터디 글들의 리스트가 뜨는 로직
    public List<Board> findAllByStudyId(Long studyId, Long memberId) {
        List<Member> members = memberRepository.findByStudyId(studyId);
        List<Long> memberIds = members.stream().map(Member::getId).collect(Collectors.toList());
        return boardRepository.findByMemberIdInAndStudyIdAndKind(memberIds, studyId, Kind.CONTENT);
    }
}
