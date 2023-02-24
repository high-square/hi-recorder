package highsquare.hirecoder.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/boards/recruit")
public class RecruitBoardFormController {

    @GetMapping("/create")
    public void chooseStudy(HttpSession session) {

        Long memberId = (Long) session.getAttribute("member_id");
    }
}
