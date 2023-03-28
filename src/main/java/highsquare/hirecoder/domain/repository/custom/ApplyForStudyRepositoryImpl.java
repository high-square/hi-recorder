package highsquare.hirecoder.domain.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import highsquare.hirecoder.entity.ApplyForStudy;

import javax.persistence.EntityManager;
import java.util.Optional;

import static highsquare.hirecoder.entity.QApplyForStudy.applyForStudy;

public class ApplyForStudyRepositoryImpl implements ApplyForStudyRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public ApplyForStudyRepositoryImpl(EntityManager em) {
        this.em = em;
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<ApplyForStudy> findByStudyIdAndMemberId(Long studyId, Long memberId) {
        return queryFactory.selectFrom(applyForStudy)
                .where(applyForStudy.study.id.eq(studyId), applyForStudy.member.id.eq(memberId))
                .stream().findFirst();
    }
}
