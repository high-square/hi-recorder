package highsquare.hirecoder.domain.repository.custom;

import highsquare.hirecoder.entity.Comment;
import highsquare.hirecoder.web.form.CommentSelectedRecruitForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface CommentRepositoryCustom {

    public boolean isCommentWriter(Long commentId, Long memberId);

    Page<CommentSelectedRecruitForm> findAllCommentsRecruit(Long boardId, Pageable pageable);
}
