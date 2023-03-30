package highsquare.hirecoder.domain.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import highsquare.hirecoder.entity.MessageForApplication;

import javax.persistence.EntityManager;

import static highsquare.hirecoder.entity.QApplyForStudy.applyForStudy;
import static highsquare.hirecoder.entity.QMessageForApplication.messageForApplication;


public class MessageForApplicationRepositoryImpl implements MessageForApplicationRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public MessageForApplicationRepositoryImpl(EntityManager em) {
        this.em = em;
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public MessageForApplication findByApplyForStudyIdWithApplyForStudy(Long applyForStudyId) {
        return queryFactory.selectFrom(messageForApplication)
                .join(applyForStudy).on(messageForApplication.applyForStudy.id.eq(applyForStudyId))
                .fetchJoin().fetchFirst();
    }
}
