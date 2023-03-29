package highsquare.hirecoder.domain.repository.custom;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import highsquare.hirecoder.dto.MemberInfo;
import highsquare.hirecoder.dto.MemberSort;
import highsquare.hirecoder.entity.QMember;
import highsquare.hirecoder.entity.StudyMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import java.util.List;

import static com.querydsl.core.types.Projections.constructor;
import static highsquare.hirecoder.entity.QBoard.board;
import static highsquare.hirecoder.entity.QComment.comment;
import static highsquare.hirecoder.entity.QMember.member;
import static highsquare.hirecoder.entity.QStudy.study;
import static highsquare.hirecoder.entity.QStudyMember.studyMember;

public class StudyMemberRepositoryImpl implements StudyMemberRepositoryCustom {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public StudyMemberRepositoryImpl(EntityManager em) {
        this.em = em;
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public boolean existsMemberAndStudy(Long study_id, Long member_id) {
        Long result = queryFactory.select(studyMember.id).from(studyMember)
                .where(studyMember.study.id.eq(study_id),
                        studyMember.member.id.eq(member_id)).fetchFirst();

        return result != null;
    }

    @Override
    public List<StudyMember> getAllMembersStudy(Long member_id) {

        return queryFactory.selectFrom(studyMember)
                .where(studyMember.member.id.eq(member_id))
                .join(studyMember.study, study)
                .fetchJoin().fetch();
    }

    @Override
    public Page<MemberInfo> searchStudyMemberInfo(Long studyId, Pageable pageable) {

        List<MemberInfo> content = queryFactory.select(constructor(MemberInfo.class,
                        studyMember.member.id, studyMember.member.name, board.count(), comment.count(), studyMember.attendState.stringValue()))
                .from(studyMember)
                .join(study).on(studyMember.study.id.eq(study.id), study.id.eq(studyId))
                .join(member).on(studyMember.member.id.eq(member.id))
                .leftJoin(board).on(study.id.eq(board.study.id), member.id.eq(board.member.id)).fetchJoin()
                .leftJoin(comment).on(comment.board.id.eq(board.id)).fetchJoin()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .groupBy(studyMember)
                .orderBy(memberInfoSort(pageable))
                .fetch();

        Long count = queryFactory.select(studyMember.count())
                .from(studyMember)
                .join(study).on(studyMember.study.id.eq(studyId))
                .fetchOne();

        return new PageImpl<>(content, pageable, count);
    }

    private OrderSpecifier<?> memberInfoSort(Pageable pageable) {
        Sort sort = pageable.getSort();

        if (!sort.isEmpty()) {
            for (Sort.Order order : sort) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                switch (MemberSort.valueOf(order.getProperty())) {
                    case ID:
                        return new OrderSpecifier<>(direction, member.id);
                    case NAME:
                        return new OrderSpecifier<>(direction, member.name);
                    case WRITE_COUNT:
                        return new OrderSpecifier<>(direction, board.count());
                    case COMMENT_COUNT:
                        return new OrderSpecifier<>(direction, comment.count());
                    case ATTEND:
                        return new OrderSpecifier<>(direction, studyMember.attendState);
                    case MANAGER:
                        return new OrderSpecifier<>(direction, study.manager.id.eq(member.id));
                }
            }
        }

        return null;
    }

}
