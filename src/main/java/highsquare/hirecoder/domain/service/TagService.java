package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.BoardTagRepository;
import highsquare.hirecoder.domain.repository.TagRepository;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.BoardTag;
import highsquare.hirecoder.entity.Kind;
import highsquare.hirecoder.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.*;

import static highsquare.hirecoder.entity.Kind.RECRUIT;

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


    @Transactional
    public List<Tag> getTags(Long boardId) {

        List<Tag> tagsUsedInBoard = boardTagRepository.findTagByBoardId(boardId);

        if (tagsUsedInBoard==null) {
            tagsUsedInBoard = new ArrayList<>();
        }

        return tagsUsedInBoard;
    } 

   

    public void updateTags(Board board, List<String> tags) {
        removeTags(board);
        registerTags(board, tags);
    }

    public void removeTags(Board board) {
        boardTagRepository.deleteByBoard(board);

    }

    //
    public List<Map<String, Object>> getTagList(Kind kind) {
        // RECRUIT, CONTENT 별 Tags
        List<Object[]> tagCountList = boardTagRepository.findTagCountByBoardTag(kind);
        List<Map<String, Object>> popularTags = new ArrayList<>();
        for (Object[] tagCount : tagCountList){
            String tag = ((Tag) tagCount[0]).getContent().toString(); // tagname
            int count = ((Number) tagCount[1]).intValue(); // tag count
            Map<String, Object> tagMap = new HashMap<>();
            tagMap.put("tag", tag);
            tagMap.put("count", count);
            popularTags.add(tagMap);
        }
        return popularTags;
    }

}
