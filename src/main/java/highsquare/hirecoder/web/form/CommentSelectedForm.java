package highsquare.hirecoder.web.form;

import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@AllArgsConstructor
@Data
public class CommentSelectedForm {

    private Long id;

    private String content;

    private int likeCount;

    private Long memberId;

    private Long boardId;

    public CommentSelectedForm() {
    }
}
