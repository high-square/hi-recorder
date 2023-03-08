package highsquare.hirecoder.domain.repository.custom;

import com.querydsl.core.QueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import highsquare.hirecoder.entity.QStudy;
import highsquare.hirecoder.entity.StudyMember;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

import java.util.List;

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

}
