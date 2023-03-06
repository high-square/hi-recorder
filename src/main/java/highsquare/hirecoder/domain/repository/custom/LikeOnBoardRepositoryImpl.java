package highsquare.hirecoder.domain.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import static highsquare.hirecoder.entity.QLikeOnBoard.likeOnBoard;

public class LikeOnBoardRepositoryImpl implements LikeOnBoardRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public LikeOnBoardRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public boolean isExistingLikeOnBoard(Long boardId, Long memberId) {
        Long foundLikeOnBoard=queryFactory.select(likeOnBoard.id).from(likeOnBoard)
                .where(likeOnBoard.board.id.eq(boardId), likeOnBoard.member.id.eq(memberId)).fetchOne();

        return foundLikeOnBoard != null;
    }
}
