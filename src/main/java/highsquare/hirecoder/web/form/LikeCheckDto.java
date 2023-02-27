package highsquare.hirecoder.web.form;

import lombok.Data;

@Data
public class LikeCheckDto {

    private Long commentId;

    private Integer likeCheck;

    public LikeCheckDto(Long commentId, Integer likeCheck) {
        this.commentId = commentId;
        this.likeCheck = likeCheck;
    }
}
