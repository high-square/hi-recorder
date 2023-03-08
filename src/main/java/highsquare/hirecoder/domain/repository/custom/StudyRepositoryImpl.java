package highsquare.hirecoder.domain.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;

import static highsquare.hirecoder.entity.QMember.member;
import static highsquare.hirecoder.entity.QStudy.study;

public class StudyRepositoryImpl implements StudyRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public StudyRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Long findStudyManagerId(Long studyId) {
        return queryFactory.select(study.manager.id).from(study)
                .join(study.manager, member)
                .where(study.id.eq(studyId)).fetchOne();
    }
}
