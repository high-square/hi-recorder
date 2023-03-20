package highsquare.hirecoder.domain.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManager;


import static highsquare.hirecoder.entity.QComment.comment;

@Slf4j
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CommentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }




    @Override
    public boolean isCommentWriter(Long commentId, Long memberId) {

        Long foundComment = queryFactory.select(comment.id).from(comment)
                .where(comment.id.eq(commentId), comment.member.id.eq(memberId)).fetchOne();

        return foundComment != null;
    }





}
