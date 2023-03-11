package highsquare.hirecoder.domain.repository.custom;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import highsquare.hirecoder.entity.StudyMember;
import highsquare.hirecoder.utils.QueryDslUtils;
import highsquare.hirecoder.web.form.CommentSelectedRecruitForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import static highsquare.hirecoder.entity.QBoard.board;
import static highsquare.hirecoder.entity.QComment.comment;
import static highsquare.hirecoder.entity.QMember.member;
import static highsquare.hirecoder.entity.QStudy.study;
import static highsquare.hirecoder.entity.QStudyMember.studyMember;
import static org.springframework.util.ObjectUtils.isEmpty;

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

    @Override
    @Transactional
    public Page<CommentSelectedRecruitForm> findAllCommentsRecruit(Long boardId, Pageable pageable) {

        List<OrderSpecifier> ORDERS = getAllOrderSpecifiers(pageable);

        // boardId에 해당하는 comment들을 CommentSelectedRecruitForm 형태로 빼옴
        List<CommentSelectedRecruitForm> commentForm = getCommentSelectedRecruitForms(boardId, pageable, ORDERS);


        // 각각의 comment를 작성한 멤버의 studyList를 빼와서 CommentSelectedRecruitForm.StudyList에 넣어주는 작업
        commentForm.forEach(form -> {
            injectStudyList(form);
        });

        Long total = queryFactory.select(comment.count()).from(comment)
                .where(comment.board.id.eq(boardId)).fetchOne();
        return new PageImpl<>(commentForm, pageable, total);
    }

    private void injectStudyList(CommentSelectedRecruitForm form) {
        List<StudyMember> studyMemberList = queryFactory.selectFrom(studyMember)
                .where(studyMember.member.id.eq(form.getMemberId()))
                .join(studyMember.study, study)
                .fetchJoin().fetch();
        log.info("studyMemberList {}", studyMemberList.toString());
        studyMemberList.forEach(studyMember -> form.getStudyList().add(studyMember.getStudy().getId()));
    }

    private List<CommentSelectedRecruitForm> getCommentSelectedRecruitForms(Long boardId, Pageable pageable, List<OrderSpecifier> ORDERS) {
        List<CommentSelectedRecruitForm> commentForm = queryFactory.select(Projections.constructor(CommentSelectedRecruitForm.class,
                        comment.id, comment.content, comment.likeCnt, comment.member.id, member.name, board.id
                )).from(comment).join(comment.board, board).join(comment.member, member)
                .where(comment.board.id.eq(boardId))
                .orderBy(ORDERS.stream().toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
        return commentForm;
    }

    @Override
    @Transactional
    public Page<CommentSelectedRecruitForm> findBestCommentsRecruit(Long boardId, Pageable pageable) {
        List<OrderSpecifier> ORDERS = getAllOrderSpecifiers(pageable);

        // boardId에 해당하는 comment들을 CommentSelectedRecruitForm 형태로 빼옴
        List<CommentSelectedRecruitForm> commentForm = getCommentSelectedRecruitForms(boardId, pageable, ORDERS);


        // 각각의 comment를 작성한 멤버의 studyList를 빼와서 CommentSelectedRecruitForm.StudyList에 넣어주는 작업
        commentForm.forEach(form -> {
            injectStudyList(form);
        });

        Long total = queryFactory.select(comment.count()).from(comment)
                .where(comment.board.id.eq(boardId)).fetchOne();
        return new PageImpl<>(commentForm, pageable, total);
    }

    private List<OrderSpecifier> getAllOrderSpecifiers(Pageable pageable) {

        List<OrderSpecifier> ORDERS = new ArrayList<>();

        if (!isEmpty(pageable.getSort())) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                OrderSpecifier<?> orderId;
                switch (order.getProperty()) {
                    case "createdTime":
                        orderId = QueryDslUtils.getSortedColumn(direction, comment, "createdTime");
                        ORDERS.add(orderId);
                        break;
                    case "likeCnt":
                        orderId = QueryDslUtils.getSortedColumn(direction, comment, "likeCnt");
                        ORDERS.add(orderId);
                        break;
                    default:
                        break;
                }
            }
        }

        return ORDERS;
    }
}
