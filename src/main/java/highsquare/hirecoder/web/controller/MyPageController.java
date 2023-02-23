package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.service.MyPageService;
import highsquare.hirecoder.entity.LikeOnBoard;
import highsquare.hirecoder.entity.Member;
import highsquare.hirecoder.entity.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/study/myStudy")
    public String myStudy(Model model, HttpSession session) {

        // session에서 member 꺼내옴

        // 현재 로그인에 관한 페이지가 없으므로 session 값에 강욱 멤버의 memberId를 임의로 넣어두겠음
        // 현재 로그인한 멤버의 name도 넣어두겠음
        session.setAttribute("memberId",1L);
        session.setAttribute("memberName", "강욱");

        // 내 스터디 목록 저장
        List<Study> myStudyList = myPageService.findMyStudy((Long) session.getAttribute("memberId"));
        for (Study study : myStudyList) {
            System.out.println("study.getName() = " + study.getName());
//            study.getName() = 백엔드1팀
//            study.getName() = 백엔드2팀
        }

        model.addAttribute("data", myStudyList);

        return "myPage";
    }


}
