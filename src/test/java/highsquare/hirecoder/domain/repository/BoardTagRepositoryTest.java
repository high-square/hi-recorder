package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.BoardTag;
import highsquare.hirecoder.entity.Tag;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
class BoardTagRepositoryTest {

    @Autowired
    TagRepository tagRepository;
    @Autowired BoardRepository boardRepository;
    @Autowired BoardTagRepository boardTagRepository;

    List<Tag> tags;
    List<Board> boards;
    List<BoardTag> boardTags;

    @BeforeEach
    public void setUp() {
        tags = List.of(
                new Tag("tag1"),
                new Tag("tag2"),
                new Tag("tag3")
        );

        boards = List.of(
                new Board(),
                new Board(),
                new Board()
        );

        boardTags = List.of(
                new BoardTag(tags.get(0), boards.get(0)),
                new BoardTag(tags.get(1), boards.get(0)),
                new BoardTag(tags.get(2), boards.get(0)),
                new BoardTag(tags.get(2), boards.get(1))
        );

        tags.stream().forEach(tagRepository::save);
        boards.stream().forEach(boardRepository::save);
        boardTags.stream().forEach(boardTagRepository::save);
    }

    @Test
    @DisplayName("보드의 태그를 모두 지우는 로직 테스트")
    public void removeTagsTest() {

        // given


        // when

        boardTagRepository.deleteByBoard(boards.get(0));

        List<Tag> all = tagRepository.findAll();
        List<Tag> allTagsByBoardId1 = tagRepository.getAllTagsByBoardId(boards.get(0).getId());
        List<Tag> allTagsByBoardId2 = tagRepository.getAllTagsByBoardId(boards.get(1).getId());

        // then

        Assertions.assertThat(all.size()).isEqualTo(3);
        Assertions.assertThat(allTagsByBoardId1.size()).isEqualTo(0);
        Assertions.assertThat(allTagsByBoardId2.size()).isEqualTo(1);

    }

}