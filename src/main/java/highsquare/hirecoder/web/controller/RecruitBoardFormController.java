package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.service.StudyMemberService;
import highsquare.hirecoder.entity.Study;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/boards/recruit")
public class RecruitBoardFormController {

    private final StudyMemberService studyMemberService;

    @GetMapping("/create")
    public String getRecruitBoardCreateForm(HttpSession session, Model model) {

        Long memberId = (Long) session.getAttribute("member_id");

        List<Study> studies = studyMemberService.getAllMembersStudy(memberId);

        if (studies.isEmpty()) {
            model.addAttribute("unsociable", true);
        }

        return "form/recruitBoardCreateForm";
    }
}
