package highsquare.hirecoder.domain.repository.custom;

public interface BoardRepositoryCustom {
    public boolean existsBoardWriter(Long boardId, Long writerId);
}
