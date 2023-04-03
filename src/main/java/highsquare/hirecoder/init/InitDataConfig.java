package highsquare.hirecoder.init;

import highsquare.hirecoder.domain.repository.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;

@Configuration
public class InitDataConfig {

    @Component
    @Profile("h2")
    static class InitDataForH2 extends InitData {
        private final EntityManager em;
        public InitDataForH2(PlatformTransactionManager platformTransactionManager, BoardRepository boardRepository, CommentRepository commentRepository, MemberRepository memberRepository, StudyRepository studyRepository, StudyMemberRepository studyMemberRepository, ApplyForStudyRepository applyForStudyRepository, MessageForApplicationRepository messageForApplicationRepository, EntityManager em) {
            super(platformTransactionManager, boardRepository, commentRepository, memberRepository, studyRepository, studyMemberRepository, applyForStudyRepository, messageForApplicationRepository);
            this.em = em;
        }

        @Override
        public void editConstraints() {
            setCommentConstraint();
            setBoardConstraint();
            setMemberConstraint();
            setStudyConstraint();
            setApplyForStudyConstraint();
        }

        private void setCommentConstraint() {
            String dropLikeOnCommentFKQuery = "ALTER TABLE PUBLIC.LIKE_ON_COMMENT CONSTRAINT AINT FK_COMMENT__LIKE_ON_COMMENT;";
            String addLikeOnCommentFKQuery = "ALTER TABLE PUBLIC.LIKE_ON_COMMENT ADD CONSTRAINT FK_COMMENT__LIKE_ON_COMMENT FOREIGN KEY (comment_id) REFERENCES PUBLIC.COMMENT(COMMENT_ID) ON DELETE CASCADE;";

            em.createNativeQuery(dropLikeOnCommentFKQuery + addLikeOnCommentFKQuery).executeUpdate();
        }

        private void setBoardConstraint() {
            String dropCommentFKQuery = "ALTER TABLE PUBLIC.COMMENT CONSTRAINT AINT FK_BOARD__COMMENT;";
            String addCommentFKQuery = "ALTER TABLE PUBLIC.COMMENT ADD CONSTRAINT FK_BOARD__COMMENT FOREIGN KEY (board_id) REFERENCES PUBLIC.BOARD(BOARD_ID) ON DELETE CASCADE;";

            String dropBoardTagFKQuery = "ALTER TABLE PUBLIC.BOARD_TAG CONSTRAINT AINT FK_BOARD__BOARD_TAG;";
            String addBoardTagFKQuery = "ALTER TABLE PUBLIC.BOARD_TAG ADD CONSTRAINT FK_BOARD__BOARD_TAG FOREIGN KEY (board_id) REFERENCES PUBLIC.BOARD(BOARD_ID) ON DELETE CASCADE;";

            String dropLikeOnBoardFKQuery = "ALTER TABLE PUBLIC.LIKE_ON_BOARD CONSTRAINT AINT FK_BOARD__LIKE_ON_BOARD;";
            String addLikeOnBoardFKQuery = "ALTER TABLE PUBLIC.LIKE_ON_BOARD ADD CONSTRAINT FK_BOARD__LIKE_ON_BOARD FOREIGN KEY (board_id) REFERENCES PUBLIC.BOARD(BOARD_ID) ON DELETE CASCADE;";

            String dropBoardImageFKQuery = "ALTER TABLE PUBLIC.BOARD_IMAGE CONSTRAINT AINT FK_BOARD__BOARD_IMAGE;";
            String addBoardImageFKQuery = "ALTER TABLE PUBLIC.BOARD_IMAGE ADD CONSTRAINT FK_BOARD__BOARD_IMAGE FOREIGN KEY (board_id) REFERENCES PUBLIC.BOARD(BOARD_ID) ON DELETE SET NULL;";

            em.createNativeQuery(dropCommentFKQuery + addCommentFKQuery +
                    dropBoardTagFKQuery + addBoardTagFKQuery +
                    dropLikeOnBoardFKQuery + addLikeOnBoardFKQuery +
                    dropBoardImageFKQuery + addBoardImageFKQuery).executeUpdate();
        }

        private void setStudyConstraint() {
            String dropBoardFKQuery = "ALTER TABLE PUBLIC.BOARD CONSTRAINT AINT FK_STUDY__BOARD;";
            String dropStudyMemberFKQuery = "ALTER TABLE PUBLIC.STUDY_MEMBER CONSTRAINT AINT FK_STUDY__STUDY_MEMBER;";

            String addBoardFKQuery = "ALTER TABLE PUBLIC.BOARD ADD CONSTRAINT FK_STUDY__BOARD FOREIGN KEY (study_id) REFERENCES PUBLIC.STUDY(ID) ON DELETE CASCADE;";
            String addStudyMemberFKQuery = "ALTER TABLE PUBLIC.STUDY_MEMBER ADD CONSTRAINT FK_STUDY__STUDY_MEMBER FOREIGN KEY (study_id) REFERENCES PUBLIC.STUDY(ID) ON DELETE CASCADE;";

            String dropApplyForStudyFKQuery = "ALTER TABLE PUBLIC.APPLY_FOR_STUDY CONSTRAINT AINT FK_STUDY__APPLY_FOR_STUDY;";
            String addApplyForStudyFKQuery = "ALTER TABLE PUBLIC.APPLY_FOR_STUDY ADD CONSTRAINT FK_STUDY__APPLY_FOR_STUDY FOREIGN KEY (STUDY_ID) REFERENCES PUBLIC.STUDY(ID) ON DELETE CASCADE;";

            em.createNativeQuery(dropBoardFKQuery + dropStudyMemberFKQuery + addBoardFKQuery + addStudyMemberFKQuery
                    + dropApplyForStudyFKQuery + addApplyForStudyFKQuery).executeUpdate();
        }

        private void setMemberConstraint() {
            String dropApplyForStudyFKQuery = "ALTER TABLE PUBLIC.APPLY_FOR_STUDY CONSTRAINT AINT FK_MEMBER__APPLY_FOR_STUDY;";
            String addApplyForStudyFKQuery = "ALTER TABLE PUBLIC.APPLY_FOR_STUDY ADD CONSTRAINT FK_MEMBER__APPLY_FOR_STUDY FOREIGN KEY (MEMBER_ID) REFERENCES PUBLIC.MEMBER(MEMBER_ID) ON DELETE CASCADE;";

            String dropStudyMemberFKQuery = "ALTER TABLE PUBLIC.STUDY_MEMBER CONSTRAINT AINT FK_MEMBER__STUDY_MEMBER;";
            String addStudyMemberFKQuery = "ALTER TABLE PUBLIC.STUDY_MEMBER ADD CONSTRAINT FK_MEMBER__STUDY_MEMBER FOREIGN KEY (MEMBER_ID) REFERENCES PUBLIC.MEMBER(MEMBER_ID) ON DELETE CASCADE;";

            String dropLikeOnBoardFKQuery = "ALTER TABLE PUBLIC.LIKE_ON_BOARD CONSTRAINT AINT FK_MEMBER__LIKE_ON_BOARD;";
            String addLikeOnBoardFKQuery = "ALTER TABLE PUBLIC.LIKE_ON_BOARD ADD CONSTRAINT FK_MEMBER__LIKE_ON_BOARD FOREIGN KEY (MEMBER_ID) REFERENCES PUBLIC.MEMBER(MEMBER_ID) ON DELETE CASCADE;";

            String dropBoardFKQuery = "ALTER TABLE PUBLIC.BOARD CONSTRAINT AINT FK_MEMBER__BOARD;";
            String addBoardFKQuery = "ALTER TABLE PUBLIC.BOARD ADD CONSTRAINT FK_MEMBER__BOARD FOREIGN KEY (MEMBER_ID) REFERENCES PUBLIC.MEMBER(MEMBER_ID) ON DELETE CASCADE;";

            String dropCommentFKQuery = "ALTER TABLE PUBLIC.COMMENT CONSTRAINT AINT FK_MEMBER__COMMENT;";
            String addCommentFKQuery = "ALTER TABLE PUBLIC.COMMENT ADD CONSTRAINT FK_MEMBER__COMMENT FOREIGN KEY (MEMBER_ID) REFERENCES PUBLIC.MEMBER(MEMBER_ID) ON DELETE CASCADE;";

            String dropLikeOnCommentFKQuery = "ALTER TABLE PUBLIC.LIKE_ON_COMMENT CONSTRAINT AINT FK_MEMBER__LIKE_ON_COMMENT;";
            String addLikeOnCommentFKQuery = "ALTER TABLE PUBLIC.LIKE_ON_COMMENT ADD CONSTRAINT FK_MEMBER__LIKE_ON_COMMENT FOREIGN KEY (MEMBER_ID) REFERENCES PUBLIC.MEMBER(MEMBER_ID) ON DELETE CASCADE;";

            String dropManagerFKQuery = "ALTER TABLE PUBLIC.STUDY CONSTRAINT AINT FK_MANAGER__STUDY;";
            String addManagerFKQuery = "ALTER TABLE PUBLIC.STUDY ADD CONSTRAINT FK_MANAGER__STUDY FOREIGN KEY (MANAGER_ID) REFERENCES PUBLIC.MEMBER(MEMBER_ID) ON DELETE CASCADE;";

            em.createNativeQuery(dropApplyForStudyFKQuery + addApplyForStudyFKQuery
                    + dropStudyMemberFKQuery + addStudyMemberFKQuery
                    + dropLikeOnBoardFKQuery + addLikeOnBoardFKQuery
                    + dropBoardFKQuery + addBoardFKQuery
                    + dropCommentFKQuery + addCommentFKQuery
                    + dropLikeOnCommentFKQuery + addLikeOnCommentFKQuery
                    + dropManagerFKQuery + addManagerFKQuery
            ).executeUpdate();
        }

        private void setApplyForStudyConstraint() {
            String dropApplyForStudyFKQuery = "ALTER TABLE PUBLIC.MESSAGE_FOR_APPLICATION CONSTRAINT AINT FK_APPLY_FOR_STUDY__MESSAGE_FOR_APPLICATION;";
            String addApplyForStudyFKQuery = "ALTER TABLE PUBLIC.MESSAGE_FOR_APPLICATION ADD CONSTRAINT FK_APPLY_FOR_STUDY__MESSAGE_FOR_APPLICATION FOREIGN KEY (APPLY_ID) REFERENCES PUBLIC.APPLY_FOR_STUDY(APPLY_ID) ON DELETE CASCADE;";

            em.createNativeQuery(dropApplyForStudyFKQuery + addApplyForStudyFKQuery).executeUpdate();
        }
    }

    @Component
    @Profile("mysql")
    static class InitDataForMySql extends InitData {
        private final EntityManager em;
        public InitDataForMySql(PlatformTransactionManager platformTransactionManager, BoardRepository boardRepository, CommentRepository commentRepository, MemberRepository memberRepository, StudyRepository studyRepository, StudyMemberRepository studyMemberRepository, ApplyForStudyRepository applyForStudyRepository, MessageForApplicationRepository messageForApplicationRepository, EntityManager em) {
            super(platformTransactionManager, boardRepository, commentRepository, memberRepository, studyRepository, studyMemberRepository, applyForStudyRepository, messageForApplicationRepository);
            this.em = em;
        }

        @Override
        public void editConstraints() {
            setCommentConstraint();
            setBoardConstraint();
            setMemberConstraint();
            setStudyConstraint();
            setApplyForStudyConstraint();
        }

        private void setCommentConstraint() {
            String dropLikeOnCommentFKQuery = "ALTER TABLE like_on_comment DROP CONSTRAINT FK_COMMENT__LIKE_ON_COMMENT;";
            String addLikeOnCommentFKQuery = "ALTER TABLE like_on_comment ADD CONSTRAINT FK_COMMENT__LIKE_ON_COMMENT FOREIGN KEY (comment_id) REFERENCES comment(comment_id) ON DELETE CASCADE;";

            em.createNativeQuery(dropLikeOnCommentFKQuery).executeUpdate();
            em.createNativeQuery(addLikeOnCommentFKQuery).executeUpdate();
        }

        private void setBoardConstraint() {
            String dropCommentFKQuery = "ALTER TABLE comment DROP CONSTRAINT FK_BOARD__COMMENT;";
            String addCommentFKQuery = "ALTER TABLE comment ADD CONSTRAINT FK_BOARD__COMMENT FOREIGN KEY (board_id) REFERENCES board(board_id) ON DELETE CASCADE;";

            String dropBoardTagFKQuery = "ALTER TABLE board_tag DROP CONSTRAINT FK_BOARD__BOARD_TAG;";
            String addBoardTagFKQuery = "ALTER TABLE board_tag ADD CONSTRAINT FK_BOARD__BOARD_TAG FOREIGN KEY (board_id) REFERENCES board(board_id) ON DELETE CASCADE;";

            String dropLikeOnBoardFKQuery = "ALTER TABLE like_on_board DROP CONSTRAINT FK_BOARD__LIKE_ON_BOARD;";
            String addLikeOnBoardFKQuery = "ALTER TABLE like_on_board ADD CONSTRAINT FK_BOARD__LIKE_ON_BOARD FOREIGN KEY (board_id) REFERENCES board(board_id) ON DELETE CASCADE;";

            String dropBoardImageFKQuery = "ALTER TABLE board_image DROP CONSTRAINT FK_BOARD__BOARD_IMAGE;";
            String addBoardImageFKQuery = "ALTER TABLE board_image ADD CONSTRAINT FK_BOARD__BOARD_IMAGE FOREIGN KEY (board_id) REFERENCES board(board_id) ON DELETE SET NULL;";

            em.createNativeQuery(dropCommentFKQuery).executeUpdate();
            em.createNativeQuery(addCommentFKQuery).executeUpdate();
            em.createNativeQuery(dropBoardTagFKQuery).executeUpdate();
            em.createNativeQuery(addBoardTagFKQuery).executeUpdate();
            em.createNativeQuery(dropLikeOnBoardFKQuery).executeUpdate();
            em.createNativeQuery(addLikeOnBoardFKQuery).executeUpdate();
            em.createNativeQuery(dropBoardImageFKQuery).executeUpdate();
            em.createNativeQuery(addBoardImageFKQuery).executeUpdate();
        }

        private void setStudyConstraint() {
            String dropBoardFKQuery = "ALTER TABLE board DROP CONSTRAINT FK_STUDY__BOARD;";
            String dropStudyMemberFKQuery = "ALTER TABLE study_member DROP CONSTRAINT FK_STUDY__STUDY_MEMBER;";

            String addBoardFKQuery = "ALTER TABLE board ADD CONSTRAINT FK_STUDY__BOARD FOREIGN KEY (study_id) REFERENCES study(id) ON DELETE CASCADE;";
            String addStudyMemberFKQuery = "ALTER TABLE study_member ADD CONSTRAINT FK_STUDY__STUDY_MEMBER FOREIGN KEY (study_id) REFERENCES study(id) ON DELETE CASCADE;";

            String dropApplyForStudyFKQuery = "ALTER TABLE apply_for_study DROP CONSTRAINT FK_STUDY__APPLY_FOR_STUDY;";
            String addApplyForStudyFKQuery = "ALTER TABLE apply_for_study ADD CONSTRAINT FK_STUDY__APPLY_FOR_STUDY FOREIGN KEY (study_id) REFERENCES study(id) ON DELETE CASCADE;";

            em.createNativeQuery(dropBoardFKQuery).executeUpdate();
            em.createNativeQuery(dropStudyMemberFKQuery).executeUpdate();
            em.createNativeQuery(addBoardFKQuery).executeUpdate();
            em.createNativeQuery(addStudyMemberFKQuery).executeUpdate();
            em.createNativeQuery(dropApplyForStudyFKQuery).executeUpdate();
            em.createNativeQuery(addApplyForStudyFKQuery).executeUpdate();
        }

        private void setMemberConstraint() {
            String dropApplyForStudyFKQuery = "ALTER TABLE apply_for_study DROP CONSTRAINT FK_MEMBER__APPLY_FOR_STUDY;";
            String addApplyForStudyFKQuery = "ALTER TABLE apply_for_study ADD CONSTRAINT FK_MEMBER__APPLY_FOR_STUDY FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE;";

            String dropStudyMemberFKQuery = "ALTER TABLE study_member DROP CONSTRAINT FK_MEMBER__STUDY_MEMBER;";
            String addStudyMemberFKQuery = "ALTER TABLE study_member ADD CONSTRAINT FK_MEMBER__STUDY_MEMBER FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE;";

            String dropLikeOnBoardFKQuery = "ALTER TABLE like_on_board DROP CONSTRAINT FK_MEMBER__LIKE_ON_BOARD;";
            String addLikeOnBoardFKQuery = "ALTER TABLE like_on_board ADD CONSTRAINT FK_MEMBER__LIKE_ON_BOARD FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE;";

            String dropBoardFKQuery = "ALTER TABLE board DROP CONSTRAINT FK_MEMBER__BOARD;";
            String addBoardFKQuery = "ALTER TABLE board ADD CONSTRAINT FK_MEMBER__BOARD FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE;";

            String dropCommentFKQuery = "ALTER TABLE comment DROP CONSTRAINT FK_MEMBER__COMMENT;";
            String addCommentFKQuery = "ALTER TABLE comment ADD CONSTRAINT FK_MEMBER__COMMENT FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE;";

            String dropLikeOnCommentFKQuery = "ALTER TABLE like_on_comment DROP CONSTRAINT FK_MEMBER__LIKE_ON_COMMENT;";
            String addLikeOnCommentFKQuery = "ALTER TABLE like_on_comment ADD CONSTRAINT FK_MEMBER__LIKE_ON_COMMENT FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE;";

            String dropManagerFKQuery = "ALTER TABLE study DROP CONSTRAINT FK_MANAGER__STUDY;";
            String addManagerFKQuery = "ALTER TABLE study ADD CONSTRAINT FK_MANAGER__STUDY FOREIGN KEY (manager_id) REFERENCES member(member_id) ON DELETE CASCADE;";

            em.createNativeQuery(dropApplyForStudyFKQuery).executeUpdate();
            em.createNativeQuery(addApplyForStudyFKQuery).executeUpdate();
            em.createNativeQuery(dropStudyMemberFKQuery).executeUpdate();
            em.createNativeQuery(addStudyMemberFKQuery).executeUpdate();
            em.createNativeQuery(dropLikeOnBoardFKQuery).executeUpdate();
            em.createNativeQuery(addLikeOnBoardFKQuery).executeUpdate();
            em.createNativeQuery(dropBoardFKQuery).executeUpdate();
            em.createNativeQuery(addBoardFKQuery).executeUpdate();
            em.createNativeQuery(dropCommentFKQuery).executeUpdate();
            em.createNativeQuery(addCommentFKQuery).executeUpdate();
            em.createNativeQuery(dropLikeOnCommentFKQuery).executeUpdate();
            em.createNativeQuery(addLikeOnCommentFKQuery).executeUpdate();
            em.createNativeQuery(dropManagerFKQuery).executeUpdate();
            em.createNativeQuery(addManagerFKQuery).executeUpdate();
        }

        private void setApplyForStudyConstraint() {
            String dropApplyForStudyFKQuery = "ALTER TABLE message_for_application DROP CONSTRAINT FK_APPLY_FOR_STUDY__MESSAGE_FOR_APPLICATION;";
            String addApplyForStudyFKQuery = "ALTER TABLE message_for_application ADD CONSTRAINT FK_APPLY_FOR_STUDY__MESSAGE_FOR_APPLICATION FOREIGN KEY (apply_id) REFERENCES apply_for_study(apply_id) ON DELETE CASCADE;";

            em.createNativeQuery(dropApplyForStudyFKQuery).executeUpdate();
            em.createNativeQuery(addApplyForStudyFKQuery).executeUpdate();
        }
    }
}
