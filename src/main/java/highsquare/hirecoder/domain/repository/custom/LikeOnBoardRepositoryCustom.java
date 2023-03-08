package highsquare.hirecoder.domain.repository.custom;

public interface LikeOnBoardRepositoryCustom {

    public boolean isExistingLikeOnBoard(Long boardId, Long memberId);
}
