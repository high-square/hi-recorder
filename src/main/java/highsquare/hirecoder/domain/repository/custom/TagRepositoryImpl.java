package highsquare.hirecoder.domain.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import highsquare.hirecoder.entity.Tag;

import javax.persistence.EntityManager;
import java.util.List;

import static highsquare.hirecoder.entity.QBoardTag.boardTag;
import static highsquare.hirecoder.entity.QTag.tag;

public class TagRepositoryImpl implements TagRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public TagRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Tag> getAllTagsByBoardId(Long boardId) {

        return queryFactory.select(tag).from(boardTag)
                .join(boardTag.tag, tag)
                .where(boardTag.board.id.eq(boardId))
                .fetch();
    }
}
