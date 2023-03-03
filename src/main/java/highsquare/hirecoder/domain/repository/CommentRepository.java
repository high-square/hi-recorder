package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

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
}
