package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.domain.repository.custom.CommentRepositoryCustom;
import highsquare.hirecoder.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
    @Modifying
    @Transactional
    @Query("update Comment c set c.likeCnt=:likeCnt where c.id=:commentId")
    void updateLikeCnt(@Param("commentId")Long commentId, @Param("likeCnt")Integer likeCnt);

    @Query("select c from Comment c where c.board.id =:boardId")
    List<Comment> findAllByBoardId(@Param("boardId") Long boardId);

    @Query(value = "select c from Comment c join fetch c.member where c.board.id=:boardId",
            countQuery = "select count(c) from Comment c where c.board.id=:boardId")
    Page<Comment> findAllComments(@Param("boardId") Long boardId, Pageable pageable);

    @Query(value = "select c from Comment c join fetch c.member where c.board.id=:boardId",
            countQuery = "select count(c) from Comment c where c.board.id=:boardId")
    Page<Comment> findBestComments(@Param("boardId") Long boardId, Pageable pageable);

    @Query("select count(c) from Comment c where c.board.id=:boardId")
    Integer countTotalComments(@Param("boardId") Long boardId);

    @Modifying(clearAutomatically=true)
    @Query("update Comment c set c.content =:commentContent where c.id=:commentId")
    Integer updateCommentContent(@Param("commentId") Long commentId,@Param("commentContent") String commentContent);

    @Modifying(clearAutomatically=true)
    @Transactional
    @Query("delete from Comment c where c.id=:commentId")
    void deleteComment(@Param("commentId") Long commentId);
}
