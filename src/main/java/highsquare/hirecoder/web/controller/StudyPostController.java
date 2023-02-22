package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.service.StudyMemberService;
import highsquare.hirecoder.web.form.PostCreateForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class StudyPostController {
    private final StudyMemberService studyMemberService;

    @GetMapping("/create")
    public String getPostCreatePage(HttpSession session, Model model) {

//        Long studyId = (Long) session.getAttribute("study_id");
//        Long memberId = (Long) session.getAttribute("member_id");
//
//        if (isIdNull(studyId, memberId)) {
//            model.addAttribute("access", true);
//        } else if (!studyMemberService.doesMemberBelongToStudy(studyId, memberId)) {
//            model.addAttribute("not_member", true);
//        }
        model.addAttribute("postCreateForm", new PostCreateForm());

        return "postEdit";
    }

    @PostMapping("/create")
    public String createPost(@ModelAttribute PostCreateForm postForm, BindingResult bindingResult, HttpSession session, Model model) {
//        Long studyId = (Long) session.getAttribute("study_id");
//        Long memberId = (Long) session.getAttribute("member_id");
//
//        if (!studyMemberService.doesMemberBelongToStudy(studyId, memberId)) {
//            bindingResult.reject("access.not_member");
//        }

        if (postForm.isTitleTooShort()) {
            bindingResult.rejectValue("title", "min.title",
                    new Object[] {PostCreateForm.MIN_TITLE_LENGTH}, null);
        } else if (postForm.isTitleTooLong()) {
            bindingResult.rejectValue("title", "max.title",
                    new Object[]{PostCreateForm.MAX_TITLE_LENGTH}, null);
        }

        if (postForm.isContentTooShort()) {
            bindingResult.rejectValue("content", "min.content",
                    new Object[]{PostCreateForm.MIN_CONTENT_LENGTH}, null);
        } else if (postForm.isContentTooLong()) {
            bindingResult.rejectValue("content", "max.content");
        }

        if (postForm.AreTooManyTags()) {
            bindingResult.rejectValue("tags", "max.tags_length",
                    new Object[]{PostCreateForm.MAX_TAGS_COUNT}, null);
        } else if (postForm.AreAnyTagsTooLong()) {
            bindingResult.rejectValue("tags", "max.tag_length");
        }

        if (bindingResult.hasErrors()) {
            return "postEdit";
        }

        return "post";
    }


    private boolean isIdNull(Long studyId, Long writerId) {
        return studyId == null || writerId == null;
    }

}
