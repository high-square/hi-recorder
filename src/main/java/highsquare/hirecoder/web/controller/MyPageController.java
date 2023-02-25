package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.service.MyPageService;
import highsquare.hirecoder.entity.*;
import highsquare.hirecoder.web.form.BoardListForm;
import highsquare.hirecoder.web.form.CommentSelectedForm;
import highsquare.hirecoder.web.form.MyStudyForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/myStudy")
    public String myStudy(Model model, HttpSession session) {

        // session에서 member 꺼내옴

        // 현재 로그인에 관한 페이지가 없으므로 session 값에 강욱 멤버의 memberId와 memberName을 임의로 넣어둔다.
        session.setAttribute("memberId",1L);
        session.setAttribute("memberName", "강욱");

        // db에서 memberId로 내 스터디 목록 저장
        List<Study> myStudyList = myPageService.findMyStudy((Long) session.getAttribute("memberId"));
        for (Study study : myStudyList) {
            System.out.println("study.getName() = " + study.getName());
//            study.getName() = 백엔드1팀
//            study.getName() = 백엔드2팀
        }

        // Study 엔티티를 form으로 변환
        List<MyStudyForm> myStudyFormList = myStudyList.stream().map(o -> new MyStudyForm(o.getId(), o.getName(), o.getActivityState(), o.getStudyStartDate(), o.getStudyFinishDate(), o.getCrewNumber(), o.getMeetingType()))
                .collect(Collectors.toList());

        model.addAttribute("data", myStudyFormList);
        model.addAttribute("memberId", (Long) session.getAttribute("memberId"));

        return "myPage";
    }

    @GetMapping("/{studyId}/{memberId}")
    public String myStudyPostList(Model model, HttpSession session) {

        // session에서 member 꺼내옴

        // 현재 로그인에 관한 페이지가 없으므로 session 값에 강욱 멤버의 memberId와 memberName을 임의로 넣어둔다.
        session.setAttribute("memberId",1L);
        session.setAttribute("memberName", "강욱");

        model.addAttribute("memberName", "강욱");

        // session에 studyId도 넣어준다.
        session.setAttribute("studyId", 5L);

        model.addAttribute("studyId", 5L);

        // db에서 memberId와 studyId가 일치하는 board를 찾는다. -> list
        List<Board> myPosts = myPageService.findMyPosts((Long) session.getAttribute("studyId"), (Long) session.getAttribute("memberId"));
        for (Board myPost : myPosts) {
            System.out.println("myPost.getTitle() = " + myPost.getTitle());
        }
        
        // dto로 변환
        List<BoardListForm> boardListFormList = myPosts.stream().map(o -> new BoardListForm(o.getId(), o.getTitle()))
                .collect(Collectors.toList());

        model.addAttribute("data", boardListFormList);

        return "myPostList";
    }

}
