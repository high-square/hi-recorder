package highsquare.hirecoder.web.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateForm {
    public static final int MIN_TITLE_LENGTH = 1;
    public static final int MAX_TITLE_LENGTH = 50;
    public static final int MAX_TAGS_COUNT = 5;
    public static final int MAX_TAG_LENGTH = 20;
    public static final int MIN_CONTENT_LENGTH = 10;
    public static final int MAX_CONTENT_LENGTH = 10000;
    private String title;
    private List<String> tags;
    private String content = "";

    // 폼 검증 로직
    public boolean isTitleTooShort() {
        return title == null || title.length() < MIN_TITLE_LENGTH;
    }
    public boolean isTitleTooLong() {
        return title != null && title.length() > MAX_TITLE_LENGTH;
    }
    public boolean AreTooManyTags() {
        return tags != null && tags.size() > MAX_TAGS_COUNT;
    }
    public boolean AreAnyTagsTooLong() {
        return tags != null && tags.stream().anyMatch((tag)-> tag.length() > 20);
    }
    public boolean isContentTooShort() {
        return content == null ||content.length() < MIN_CONTENT_LENGTH;
    }
    public boolean isContentTooLong() {
        return content != null && content.length() > MAX_CONTENT_LENGTH;
    }

}
