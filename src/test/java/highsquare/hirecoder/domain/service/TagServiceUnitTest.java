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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

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
        board.setId(0L);
        tag1 = new Tag("tag1");
        tag2 = new Tag("tag2");
    }

    // 정상일 때
    @Test
    @DisplayName("태그 등록 테스트 : 정상인 경우")
    public void registerTagTest1() {

        // given

        List<String> tags = List.of("tag1", "tag2");

        given(tagRepository.findByContent(tag1.getContent())).willReturn(Optional.of(tag1));
        given(tagRepository.findByContent("tag2")).willReturn(Optional.empty());

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

        given(tagRepository.findByContent(tag1.getContent())).willReturn(Optional.of(tag1));
        given(tagRepository.findByContent("tag2")).willReturn(Optional.empty());

        // when

        List<Tag> tagList = tagService.registerTags(null, tags);

        // then

        Assertions.assertThat(tagList.stream().map(Tag::getContent).collect(Collectors.toList()))
                .containsExactly("tag1", "tag2");
    }

    @Test
    @DisplayName("태그 수정 테스트")
    public void updateTagsTest() {

        // given
        List<String> removedTags = new ArrayList<>(List.of("tag1", "tag2", "tag3"));
        List<String> willUpdate = List.of("tag1", "tag3");
        List<String> updatedTags = new ArrayList<>(List.of());

        given(boardTagRepository.deleteByBoard(board)).will((invocation->{
          int r = removedTags.size();
          removedTags.clear();
          return r;
        }));

        given(tagRepository.findByContent(anyString())).will((invocation -> {
            updatedTags.add(invocation.getArgument(0, String.class));
            return Optional.of(new Tag(invocation.getArgument(0, String.class)));
        }));

        // when
        tagService.updateTags(board, willUpdate);

        //
        Assertions.assertThat(removedTags.size()).isEqualTo(0);
        Assertions.assertThat(updatedTags).containsExactly("tag1", "tag3");

    }

}