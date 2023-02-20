package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.service.StudyMemberService;
import highsquare.hirecoder.web.form.CreatePostForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class StudyPostController {
    private final StudyMemberService studyMemberService;

    @GetMapping("/create")
    public String getPostCreatePage(HttpSession session, Model model) {

        Long study_id = (Long) session.getAttribute("study_id");
        Long writer_id = (Long) session.getAttribute("writer_id");

        if (study_id == null || writer_id == null) {
            model.addAttribute("access", true);
        } else if (!studyMemberService.doesMemberBelongToStudy(study_id, writer_id)) {
            model.addAttribute("not_member", true);
        }

        return "postEdit";
    }

}
