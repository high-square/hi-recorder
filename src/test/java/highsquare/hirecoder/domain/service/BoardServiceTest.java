package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.BoardRepository;
import highsquare.hirecoder.domain.repository.MemberRepository;
import highsquare.hirecoder.domain.repository.StudyRepository;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Kind;
import highsquare.hirecoder.entity.Member;
import highsquare.hirecoder.entity.Study;
import highsquare.hirecoder.web.form.BoardForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BoardServiceTest {
    @Autowired BoardRepository boardRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired StudyRepository studyRepository;

    @Autowired BoardService boardService;

    @Test
    @DisplayName("보드 생성 서비스 테스트")
    @Transactional
    @Rollback(value = false)
    public void createBoardTest() {

        // given

        Member member = new Member();
        member.setName("JeaDoo");
        Member savedMember = memberRepository.save(member);

        Study study = new Study();
        study.setName("ABC");
        Study savedStudy = studyRepository.save(study);

        BoardForm BoardForm = new BoardForm("Hello", null, true, "# hello JaeDoo", null, null);

        // when

        Board board = boardService.createBoard(member.getId(), study.getId(), Kind.CONTENT, BoardForm);

        // then

        assertThat(board.getMember().getId()).isEqualTo(savedMember.getId());
        assertThat(board.getMember().getName()).isEqualTo(savedMember.getName());

        assertThat(board.getStudy().getId()).isEqualTo(savedStudy.getId());
        assertThat(board.getStudy().getName()).isEqualTo(savedStudy.getName());

        assertThat(board.getTitle()).isEqualTo(BoardForm.getTitle());
        assertThat(board.getContent()).isEqualTo(BoardForm.getContent());

    }
}