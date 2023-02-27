package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.BoardRepository;
import highsquare.hirecoder.domain.repository.MemberRepository;
import highsquare.hirecoder.domain.repository.StudyRepository;
import highsquare.hirecoder.entity.Board;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class BoardListServiceTest {
    @Autowired BoardListService boardListService;
    @Test
    public void getStudyBoardList() {
        //given
        Long studyId = 5L;
        Long memberId = 1L;

        //when
        List<Board> boardList = boardListService.findAllByStudyId(studyId, memberId);

        //then
        assertThat(boardList);
    }

}