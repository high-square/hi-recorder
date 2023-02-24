package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.constant.SessionConstant;
import highsquare.hirecoder.domain.service.StudyMemberService;
import highsquare.hirecoder.entity.Study;
import highsquare.hirecoder.web.form.BoardForm;
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

        // 테스트용 데이터
        session.setAttribute(SessionConstant.MEMBER_ID, 1L);

        Long memberId = (Long) session.getAttribute(SessionConstant.MEMBER_ID);

        List<Study> studies = studyMemberService.getAllMembersStudy(memberId);

        model.addAttribute("studies", studies);
        model.addAttribute("boardForm", new BoardForm());

        if (studies.isEmpty()) {
            model.addAttribute("unsociable", true);
        }

        return "form/recruitBoardCreateForm";
    }
}
