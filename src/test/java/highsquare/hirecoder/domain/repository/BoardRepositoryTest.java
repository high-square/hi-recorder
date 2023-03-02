package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Optional;

@DataJpaTest
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

        Assertions.assertThat(board1Member1).isTrue();
        Assertions.assertThat(board2Member1).isFalse();
        Assertions.assertThat(board2Member2).isTrue();

    }

}