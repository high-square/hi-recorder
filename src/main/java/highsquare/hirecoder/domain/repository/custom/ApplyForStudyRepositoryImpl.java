package highsquare.hirecoder.domain.repository.custom;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import highsquare.hirecoder.dto.ApplyInfo;
import highsquare.hirecoder.dto.ApplySort;
import highsquare.hirecoder.entity.ApplyForStudy;
import highsquare.hirecoder.entity.AuditState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.Projections.constructor;
import static highsquare.hirecoder.entity.QApplyForStudy.applyForStudy;
import static highsquare.hirecoder.entity.QMember.member;
import static highsquare.hirecoder.entity.QMessageForApplication.messageForApplication;

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

    @Override
    public Page<ApplyInfo> searchApplyInfo(Long studyId, Pageable page) {
        List<ApplyInfo> content = queryFactory.select(
                        constructor(ApplyInfo.class, applyForStudy.id, applyForStudy.member.name, messageForApplication.appealMessage, applyForStudy.member.email, applyForStudy.createDate)
                )
                .from(applyForStudy)
                .join(applyForStudy.member, member)
                .where(applyForStudy.study.id.eq(studyId), applyForStudy.auditstate.eq(AuditState.대기))
                .join(messageForApplication).on(messageForApplication.applyForStudy.eq(applyForStudy))
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .orderBy(applyInfoSort(page))
                .fetch();

        Long count = queryFactory.select(applyForStudy.count()).from(applyForStudy)
                .where(applyForStudy.study.id.eq(studyId), applyForStudy.auditstate.eq(AuditState.대기))
                .fetchOne();

        return new PageImpl<>(content, page, count);
    }

    private OrderSpecifier<?> applyInfoSort(Pageable page) {

        Sort sort = page.getSort();

        if (!sort.isEmpty()) {
            for (Sort.Order order : sort) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                switch (ApplySort.valueOf(order.getProperty())) {
                    case ID:
                        return new OrderSpecifier<>(direction, applyForStudy.id);
                    case MEMBER_NAME:
                        return new OrderSpecifier<>(direction, member.name);
                    case REASON:
                        return new OrderSpecifier<>(direction, messageForApplication.appealMessage.length());
                    case EMAIL:
                        return new OrderSpecifier<>(direction, member.email);
                    case DATE:
                        return new OrderSpecifier<>(direction, applyForStudy.createDate);
                }
            }
        }

        return null;
    }
}
