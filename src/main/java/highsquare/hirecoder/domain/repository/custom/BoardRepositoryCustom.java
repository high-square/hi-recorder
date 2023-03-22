package highsquare.hirecoder.domain.repository.custom;

import highsquare.hirecoder.entity.Board;

import java.util.List;

public interface BoardRepositoryCustom {
    public boolean existsBoardWriter(Long boardId, Long writerId);

    public boolean existsBoardInStudy(Long boardId, Long studyId);

}
