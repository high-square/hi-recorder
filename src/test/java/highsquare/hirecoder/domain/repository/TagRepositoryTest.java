package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.BoardTag;
import highsquare.hirecoder.entity.Tag;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest
class TagRepositoryTest {

    @Autowired TagRepository tagRepository;
    @Autowired BoardRepository boardRepository;
    @Autowired BoardTagRepository boardTagRepository;

    @Test
    void getAllTagsByBoardIdTest() throws Exception
    {
        // given
        List<Tag> tags = List.of(
                new Tag("tag1"),
                new Tag("tag2"),
                new Tag("tag3")
        );

        List<Board> boards = List.of(
                new Board(),
                new Board(),
                new Board()
        );

        List<BoardTag> boardTags = List.of(
                new BoardTag(tags.get(0), boards.get(0)),
                new BoardTag(tags.get(1), boards.get(0)),
                new BoardTag(tags.get(2), boards.get(0)),
                new BoardTag(tags.get(2), boards.get(1))
        );

        tags.stream().forEach(tagRepository::save);
        boards.stream().forEach(boardRepository::save);
        boardTags.stream().forEach(boardTagRepository::save);

        // when

        List<Tag> foundTags1 = tagRepository.getAllTagsByBoardId(boards.get(0).getId());
        List<Tag> foundTags2 = tagRepository.getAllTagsByBoardId(boards.get(1).getId());
        List<Tag> foundTags3 = tagRepository.getAllTagsByBoardId(boards.get(2).getId());

        // then

        Assertions.assertThat(foundTags1).containsExactly(tags.get(0), tags.get(1),tags.get(2));
        Assertions.assertThat(foundTags2).containsExactly(tags.get(2));
        Assertions.assertThat(foundTags3.size()).isEqualTo(0);

    }
}