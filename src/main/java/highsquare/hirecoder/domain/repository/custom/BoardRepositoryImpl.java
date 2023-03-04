package highsquare.hirecoder.domain.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import static highsquare.hirecoder.entity.QBoard.board;

public class BoardRepositoryImpl implements BoardRepositoryCustom {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public BoardRepositoryImpl(EntityManager em) {
        this.em = em;
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public boolean existsBoardWriter(Long boardId, Long writerId) {

        Long foundBoard = queryFactory.select(board.id).from(board)
                .where(board.member.id.eq(writerId)
                        .and(board.id.eq(boardId)))
                .fetchFirst();

        return foundBoard != null;
    }
}
