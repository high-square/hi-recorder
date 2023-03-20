package highsquare.hirecoder.web.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.validation.BindingResult;


@AllArgsConstructor
@Data
public class CommentSelectedForm {

    public static final int MAX_COMMENT_LENGTH=200;

    private Long id;

    private String content;

    private int likeCount;

    private Long memberId;

    private String memberName;

    private Long boardId;

    private Integer likeCheckWithMember;

    public CommentSelectedForm() {
    }

    private boolean isContentTooShort() {
        return content == null || content.trim().length()==0;
    }

    private boolean isContentTooLong() {
        return content != null && content.length() > MAX_COMMENT_LENGTH;
    }

    public boolean isContentTooShort(BindingResult bindingResult) {
        boolean isContentTooShort = isContentTooShort();

        if(isContentTooShort) {
            bindingResult.rejectValue("content","min.comment.content_length",null);
        }

        return isContentTooShort;
    }

    public boolean isContentTooLong(BindingResult bindingResult) {
        boolean isContentTooLong = isContentTooLong();

        if(isContentTooLong) {
            bindingResult.rejectValue("content","max.comment.content_length",null);
        }

        return isContentTooLong;
    }
}
