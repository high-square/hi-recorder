package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.BoardRepository;
import highsquare.hirecoder.domain.repository.MemberRepository;
import highsquare.hirecoder.domain.repository.StudyRepository;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Kind;
import highsquare.hirecoder.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static highsquare.hirecoder.entity.Kind.CONTENT;
import static highsquare.hirecoder.entity.Kind.RECRUIT;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardListService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    public Page<Board> boardList(Pageable pageable){
        // 스터디 구인글 리스트
        return boardRepository.findStudyAll(RECRUIT, pageable);
    }

    // 검색 기능
    public Page<Board> boardSearchList(String searchKeyword, Pageable pageable){

        return boardRepository.findBySearch(searchKeyword, RECRUIT, pageable);
    }

    //로그인한 회원 중 스터디에 참여하고 있는 멤버가, 해당 스터디를 눌렀을 때 해당 스터디 글들의 리스트가 뜨는 로직
    public Page<Board> findAllByStudyId(Long studyId, Long memberId, Pageable pageable) {
        // 스터디 내부 게시글 리스트
        List<Member> members = memberRepository.findByStudyId(studyId);
        List<Long> memberIds = members.stream().map(Member::getId).collect(Collectors.toList());
        return boardRepository.findByMemberIdInAndStudyIdAndKind(memberIds, studyId, Kind.CONTENT, pageable);
    }

    // 검색 기능
    public Page<Board> studyBoardSearchList(Long studyId, Long memberId, String searchKeyword, Pageable pageable){

        // 스터디 내부 게시글 리스트
        List<Member> members = memberRepository.findByStudyId(studyId);
        List<Long> memberIds = members.stream().map(Member::getId).collect(Collectors.toList());

        return boardRepository.findByStudyBoardSearch(memberIds, studyId, CONTENT, searchKeyword, pageable);
    }


}
