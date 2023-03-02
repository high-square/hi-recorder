package highsquare.hirecoder.domain.repository.custom;

import highsquare.hirecoder.entity.Tag;

import java.util.List;

public interface TagRepositoryCustom {
    public List<Tag> getAllTagsByBoardId(Long boardId);
}
