package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Kind;
import highsquare.hirecoder.entity.Member;
import highsquare.hirecoder.entity.Study;
import net.bytebuddy.asm.Advice;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static highsquare.hirecoder.entity.Kind.CONTENT;
import static highsquare.hirecoder.entity.Kind.RECRUIT;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BoardRepositoryTest {
    @Autowired BoardRepository boardRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    @DisplayName("게시글 글쓴이 조회 로직 테스트")
    public void findByIdAndMember_IdTest() {

        // given

        Member member1 = new Member();
        member1.setName("member1");
        memberRepository.save(member1);

        Member member2 = new Member();
        member2.setName("member2");
        memberRepository.save(member2);

        Board board1 = new Board();
        board1.setMember(member1);
        board1.setTitle("board1");
        boardRepository.save(board1);

        Board board2 = new Board();
        board2.setMember(member2);
        board2.setTitle("board2");
        boardRepository.save(board2);

        System.out.println("member1 : " + member1.getId() + ", board1 : " + board1.getId());
        System.out.println("member2 : " + member2.getId() + ", board2 : " + board2.getId());


        em.flush();
        em.clear();

        // when

        boolean board1Member1 = boardRepository.existsBoardWriter(board1.getId(), member1.getId());
        boolean board2Member1 = boardRepository.existsBoardWriter(board2.getId(), member1.getId());
        boolean board2Member2 = boardRepository.existsBoardWriter(board2.getId(), member2.getId());

        // then

        assertThat(board1Member1).isTrue();
        assertThat(board2Member1).isFalse();
        assertThat(board2Member2).isTrue();

    }

    @Test
    public void findTop5ByCreateDateBetweenOrderByCntDescAndKind() throws Exception {
        //given
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "viewCnt");
        Pageable pageable = PageRequest.of(0, 5, sort);

        //when
        List<Board> top5 = boardRepository.findTop5ByBoardList(start, end, RECRUIT, pageable);

        //then

        assertThat(top5.size()).isEqualTo(5);
        assertThat(top5.get(0).getViewCnt()).isEqualTo(400);
        assertThat(top5.get(1).getViewCnt()).isEqualTo(300);
        assertThat(top5.get(2).getViewCnt()).isEqualTo(150);
        assertThat(top5.get(3).getViewCnt()).isEqualTo(70);
        assertThat(top5.get(4).getViewCnt()).isEqualTo(60);
    }

}