package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.BoardTagRepository;
import highsquare.hirecoder.domain.repository.TagRepository;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Tag;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TagServiceUnitTest {
    @InjectMocks
    private TagService tagService;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private BoardTagRepository boardTagRepository;

    private Tag tag1;
    private Tag tag2;
    private Board board;

    @BeforeEach
    public void setUp() {
        board = new Board(null, null, "boardTitle", null, null, null);
        tag1 = new Tag("tag1");
        tag2 = new Tag("tag2");

        given(tagRepository.findByContent(tag1.getContent())).willReturn(Optional.of(tag1));
        given(tagRepository.findByContent("tag2")).willReturn(Optional.empty());
    }

    // 정상일 때
    @Test
    @DisplayName("태그 등록 테스트 : 정상인 경우")
    public void registerTagTest1() {

        // given

        List<String> tags = List.of("tag1", "tag2");

        // when

        List<Tag> tagList = tagService.registerTags(board, tags);

        // then

        Assertions.assertThat(tagList.stream().map(Tag::getContent).collect(Collectors.toList()))
                .containsExactly("tag1", "tag2");
    }

    // board가 널일때
    @Test
    @DisplayName("태그 등록 테스트 : board가 null인 경우")
    public void registerTagTest2() {

        // given

        List<String> tags = List.of("tag1", "tag2");

        // when

        List<Tag> tagList = tagService.registerTags(null, tags);

        // then

        Assertions.assertThat(tagList.stream().map(Tag::getContent).collect(Collectors.toList()))
                .containsExactly("tag1", "tag2");
    }

}