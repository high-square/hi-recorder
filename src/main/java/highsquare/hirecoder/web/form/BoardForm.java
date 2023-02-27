package highsquare.hirecoder.web.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardForm {
    public static final int MIN_TITLE_LENGTH = 1;
    public static final int MAX_TITLE_LENGTH = 50;
    public static final int MAX_TAGS_COUNT = 5;
    public static final int MAX_TAG_LENGTH = 20;
    public static final int MIN_CONTENT_LENGTH = 10;
    public static final int MAX_CONTENT_LENGTH = 10000;

    protected String title;
    protected List<String> tags = new ArrayList<>();
    protected String content;

    // 폼 검증 로직
    public boolean isTitleTooShort() {
        return title == null || title.length() < MIN_TITLE_LENGTH;
    }
    public boolean isTitleTooLong() {
        return title != null && title.length() > MAX_TITLE_LENGTH;
    }
    public boolean areTooManyTags() {
        return tags != null && tags.size() > MAX_TAGS_COUNT;
    }
    public boolean areAnyTagsTooLong() {
        return tags != null && tags.stream().anyMatch((tag)-> tag.length() > 20);
    }
    public boolean isContentTooShort() {
        return content == null ||content.length() < MIN_CONTENT_LENGTH;
    }
    public boolean isContentTooLong() {
        return content != null && content.length() > MAX_CONTENT_LENGTH;
    }

    // BindingResult 유틸
    public boolean isTitleTooShort(BindingResult bindingResult) {
        boolean isTitleTooShort = isTitleTooShort();

        if (isTitleTooShort) {
            bindingResult.rejectValue("title", "min.form.title",
                    new Object[] {MIN_TITLE_LENGTH}, null);
        }

        return isTitleTooShort;
    }
    public boolean isTitleTooLong(BindingResult bindingResult) {
        boolean isTitleTooLong = isTitleTooLong();

        if (isTitleTooLong) {
            bindingResult.rejectValue("title", "max.form.title",
                    new Object[] {MAX_TITLE_LENGTH}, null);
        }

        return isTitleTooLong;
    }
    public boolean areTooManyTags(BindingResult bindingResult) {
        boolean areTooManyTags = areTooManyTags();

        if (areTooManyTags) {
            bindingResult.rejectValue("tags", "max.form.tags_length",
                    new Object[] {MAX_TAGS_COUNT}, null);
        }

        return areTooManyTags;
    }
    public boolean areAnyTagsTooLong(BindingResult bindingResult) {
        boolean areAnyTagsTooLong = areAnyTagsTooLong();

        if (areAnyTagsTooLong) {
            bindingResult.rejectValue("tags", "max.form.tag_length",
                    new Object[] {MAX_TAG_LENGTH}, null);
        }

        return areAnyTagsTooLong;
    }
    public boolean isContentTooShort(BindingResult bindingResult) {
        boolean isContentTooShort = isContentTooShort();

        if (isContentTooShort) {
            bindingResult.rejectValue("content", "min.form.content",
                    new Object[] {MIN_CONTENT_LENGTH}, null);
        }

        return isContentTooShort;
    }
    public boolean isContentTooLong(BindingResult bindingResult) {
        boolean isContentTooLong = isContentTooLong();

        if (isContentTooLong) {
            bindingResult.rejectValue("content", "min.form.content",
                    new Object[] {MAX_CONTENT_LENGTH}, null);
        }

        return isContentTooLong;
    }

}
