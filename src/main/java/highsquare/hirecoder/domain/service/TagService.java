package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.BoardTagRepository;
import highsquare.hirecoder.domain.repository.TagRepository;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.BoardTag;
import highsquare.hirecoder.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TagService {
    private final TagRepository tagRepository;
    private final BoardTagRepository boardTagRepository;

    public List<Tag> registerTags(Board board, List<String> tags) {

        List<Tag> tagList = new ArrayList<>();

        for (String tagContent : tags) {
            Optional<Tag> registeredTagOptional = tagRepository.findByContent(tagContent);

            Tag registeredTag = registeredTagOptional.orElseGet(()->{
                Tag tag = new Tag(tagContent);
                tagRepository.save(tag);
                return tag;
            });

            boardTagRepository.save(new BoardTag(registeredTag, board));

            tagList.add(registeredTag);
        }

        return tagList;
    }

    public List<Tag> getTags(Long boardId) {
        return tagRepository.getAllTagsByBoardId(boardId);
    }

    public void updateTags(Board board, List<String> tags) {
        removeTags(board);
        registerTags(board, tags);
    }

    public void removeTags(Board board) {
        boardTagRepository.deleteByBoard(board);
    }
}
