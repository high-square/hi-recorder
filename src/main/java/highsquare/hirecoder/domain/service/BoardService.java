package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.BoardRepository;
import highsquare.hirecoder.domain.repository.MemberRepository;
import highsquare.hirecoder.domain.repository.StudyRepository;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Kind;
import highsquare.hirecoder.entity.Member;
import highsquare.hirecoder.entity.Study;
import highsquare.hirecoder.web.form.BoardForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

import static highsquare.hirecoder.constant.CookieConstant.COOKIE_VALID_TIME;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;

    public Board createBoard(Long memberId, Long studyId, Kind kind, BoardForm boardForm) {

        Member member = memberRepository.findById(memberId).get();

        Study study = studyRepository.findById(studyId).get();

        Board board = new Board(member, study, boardForm.getTitle(), boardForm.getContent(), boardForm.getHeadImageUrl(),
                boardForm.isOpen(), kind);

        Board savedBoard = boardRepository.save(board);

        return savedBoard;
    }

    public Optional<Board> getBoard(Long boardId) {

        return boardRepository.findById(boardId);
    }

    public Board getBoard(Long boardId, HttpServletRequest request,HttpServletResponse response) {
        Board board = boardRepository.findById(boardId).get();


        // 받아온 board의 id가 Cookie에 저장되어 있는지 확인하는 로직
        if(!viewInspection(request,response,board.getId())) {
            board.viewPlus();
        }
        return board;
    }

    public void deleteBoard(Long boardId) {
        boardRepository.deleteById(boardId);
    }

    public boolean isMemberWriter(Long memberId, Long boardId) {
        return boardRepository.existsBoardWriter(boardId, memberId);
    }

    /**
     * 조회수 증가 로직
     * true를 반환 시 조회수 증가 로직이 필요없고 false이면 조회수 증가 로직이 필요함
     */
    private boolean viewInspection(HttpServletRequest request, HttpServletResponse response, Long boardId) {
        Cookie oldCookie=null; // 쿠키 변수 선언
        Cookie[] cookies = request.getCookies();
        log.info("cookies null? {}", cookies == null);

        // 세션이 없어서 기본적으로 제공되는 쿠키가 없음. 아마 앞단에서 로그인 세션 적용하면 쿠키생김
        if (cookies==null) {
            Cookie cookie = new Cookie(UUID.randomUUID().toString(), "아무거나쿠키");
            response.addCookie(cookie);
        }

        for (Cookie cookie : cookies) {
            if(cookie.getName().equals("postView")) {
                oldCookie=cookie;
            }
        }

        if (oldCookie!=null) {
            // 쿠키의 값을 확인해서 게시글 번호가 포함되지 않을 시 값에 게시글 번호를 붙여주고 저장함
            log.info("postView1 cookie의 value = {}", oldCookie.getValue());
            if(!oldCookie.getValue().contains("["+String.valueOf(boardId)+"]")) {
                oldCookie.setValue(oldCookie.getValue() + "[" + boardId+"]");
                log.info("postView2 cookie의 value = {}", oldCookie.getValue());
                oldCookie.setMaxAge(COOKIE_VALID_TIME);
                response.addCookie(oldCookie);
                return false;
            }
            return true;
        } else { // Cookie 새로 만들어서 저장
            Cookie newCookie = new Cookie("postView", "[" + boardId+"]");
            newCookie.setMaxAge(COOKIE_VALID_TIME);
            response.addCookie(newCookie);
            return false;
        }


    }

    @Transactional
    public Board updateBoard(Long boardId, String title, boolean open, String content) {
        Board board = boardRepository.findById(boardId).get();

        board.setTitle(title);
        board.setContent(content);
        board.setPublicYn(open ? "y" : "n");

        return board;
    }

    public boolean isExistingBoard(Long boardId,Long studyId) {
        return boardId != null && boardRepository.existsBoardInStudy(boardId,studyId);
    }

    public boolean isPublic(Long boardId) {
        Board findBoard = boardRepository.findById(boardId).get();

        return findBoard.getPublicYn().equals("y");
    }
}
