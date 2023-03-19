package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.BoardRepository;
import highsquare.hirecoder.domain.repository.LikeOnBoardRepository;
import highsquare.hirecoder.domain.repository.MemberRepository;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.LikeOnBoard;
import highsquare.hirecoder.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static highsquare.hirecoder.constant.LikeCheckConstant.Like_Checked_Board;
import static highsquare.hirecoder.constant.LikeCheckConstant.Like_Unchecked_Board;


@Service
@RequiredArgsConstructor
@Transactional
public class LikeOnBoardService {

    private final LikeOnBoardRepository likeOnBoardRepository;
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;


    public LikeOnBoard updateLike(Long boardId, Long memberId) {

        LikeOnBoard findLike = getLikeOnBoard(boardId, memberId);

        // 해당 LikeOnBoard의 LikeCheck가 0이면 좋아요 선택 X, 1이면 좋아요 선택 O이니 값을 변경해줌
        // 변경감지를 이용해서 update해줌
        if (findLike.getLikeCheck()==Like_Unchecked_Board) {
            findLike.setLikeCheck(Like_Checked_Board);
        } else {
            findLike.setLikeCheck(Like_Unchecked_Board);
        }

        return findLike;
    }

    // 조회수를 클릭 or 취소할 때만 이 메소드가 호출되므로 여기 메소드에서 board의 likeCnt도 업데이트 하는 것이 좋아보임(팀원들의 의견 들어보기)
    public Integer countLikeCnt(Long boardId) {
        Integer likeCnt = likeOnBoardRepository.countLikeCnt(boardId);
        Board findBoard = boardRepository.findById(boardId).get();
        findBoard.setLikeCnt(likeCnt);
        return likeCnt;
    }

    public LikeOnBoard getLikeOnBoard(Long boardId, Long memberId) {

        // 해당 게시글과 해당 멤버의 좋아요 엔티티가 존재하지 않을 시 새로 만드는 작업
        if (!likeOnBoardRepository.isExistingLikeOnBoard(boardId,memberId)) {
            return likeOnBoardRepository.save(makeNewLike(boardId, memberId));
        }

        LikeOnBoard findLike = likeOnBoardRepository.findLikeOnBoard(boardId, memberId);
        return findLike;
    }

    public boolean isExistingLikeOnBoard(Long boardId, Long memberId) {
        return likeOnBoardRepository.isExistingLikeOnBoard(boardId, memberId);
    }

    private LikeOnBoard makeNewLike(Long boardId, Long memberId) {
        LikeOnBoard likeOnBoard = new LikeOnBoard();
        Member member = memberRepository.findById(memberId).get();
        Board board = boardRepository.findById(boardId).get();
        likeOnBoard.setMember(member);
        likeOnBoard.setBoard(board);
        return likeOnBoard;
    }
}
