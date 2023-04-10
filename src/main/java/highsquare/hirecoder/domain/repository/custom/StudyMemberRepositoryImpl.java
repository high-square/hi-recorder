package highsquare.hirecoder.domain.repository.custom;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import highsquare.hirecoder.dto.MemberInfo;
import highsquare.hirecoder.dto.MemberSort;
import highsquare.hirecoder.entity.QBoard;
import highsquare.hirecoder.entity.StudyMember;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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

        QBoard board2 = new QBoard("board2");

        List<MemberInfo> content = queryFactory.select(constructor(MemberInfo.class,
                        member.id, member.name, board.countDistinct(), comment.countDistinct(), studyMember.member.eq(studyMember.study.manager), studyMember.attendState.stringValue()))
                .from(studyMember)
                .join(studyMember.study, study).on(study.id.eq(studyId))
                .join(studyMember.member, member)
                .leftJoin(board).on(board.study.eq(study), board.member.eq(member)).fetchJoin()
                .leftJoin(board2).on(board2.study.eq(study)).fetchJoin()
                .leftJoin(comment).on(comment.board.eq(board2), comment.member.eq(member))
                .groupBy(member.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(memberInfoSort(pageable))
                .fetch();

        Long count = queryFactory.select(studyMember.count())
                .from(studyMember)
                .join(studyMember.study, study)
                .where(studyMember.study.id.eq(studyId))
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
                        return new OrderSpecifier<>(direction, study.manager.id);
                }
            }
        }

        return null;
    }

    @Override
    public List<StudyMember> getMyStudies(Long memberId) {
        return queryFactory.selectFrom(studyMember)
                .where(studyMember.member.id.eq(memberId))
                .join(studyMember.study, study).fetchJoin()
                .fetch();
    }
}
