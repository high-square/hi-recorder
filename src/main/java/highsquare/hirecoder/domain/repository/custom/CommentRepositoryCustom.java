package highsquare.hirecoder.domain.repository.custom;


public interface CommentRepositoryCustom {

    public boolean isCommentWriter(Long commentId, Long memberId);

}
