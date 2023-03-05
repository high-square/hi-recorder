package highsquare.hirecoder.domain.repository.custom;

public interface BoardRepositoryCustom {
    public boolean existsBoardWriter(Long boardId, Long writerId);

    public boolean existsBoardInStudy(Long boardId, Long studyId);
}
