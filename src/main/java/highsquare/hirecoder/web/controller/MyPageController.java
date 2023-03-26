package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.repository.StudyRepository;
import highsquare.hirecoder.domain.service.MyPageService;
import highsquare.hirecoder.domain.service.StudyService;
import highsquare.hirecoder.entity.*;
import highsquare.hirecoder.security.jwt.TokenProvider;
import highsquare.hirecoder.security.util.JwtRenewal;
import highsquare.hirecoder.utils.ScriptUtils;
import highsquare.hirecoder.web.form.BoardListForm;
import highsquare.hirecoder.web.form.MyStudyForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study")
public class MyPageController {

    private final MyPageService myPageService;


    private final StudyService studyService;

    private final StudyRepository studyRepository;

    private final JwtRenewal jwtRenewal;

    @GetMapping("/myStudy")
    public String myStudy(Model model, HttpSession session) {

        // session에서 member 꺼내옴

        // 현재 로그인에 관한 페이지가 없으므로 session 값에 강욱 멤버의 memberId와 memberName을 임의로 넣어둔다.
        session.setAttribute("memberId",1L);//////////////로그인 구현 후 수정
        session.setAttribute("memberName", "강욱");////////로그인 구현 후 수정

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
    public String myStudyPostList(@PathVariable(value="studyId") Long studyId,
                                  @PathVariable(value="memberId") Long memberId,
                                  Model model, HttpSession session) {
        // session에 넣는 것?

        // session에서 studyMember 꺼내옴

        // 현재 로그인에 관한 페이지가 없으므로 session 값에 강욱 멤버의 memberId와 memberName을 임의로 넣어둔다.
//        session.setAttribute("memberId",1L);
        session.setAttribute("memberId", memberId);
        session.setAttribute("memberName", "강욱");

        model.addAttribute("memberName", "강욱");

        // session에 studyId도 넣어준다.
        session.setAttribute("studyId", studyId);

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

    @PostMapping("/leave/{studyId}/{memberId}")
    public String leaveStudy(@PathVariable(value="studyId") Long studyId,
                             @PathVariable(value="memberId") Long memberId,
                             Authentication authentication,
                             HttpServletResponse response) throws IOException {

        Long loginMemberId=Long.parseLong(authentication.getName());

        if (!(memberId==loginMemberId)) {
            ScriptUtils.alert(response,"해당 로그인 사용자가 아닙니다.");
        }

        //스터디 존재 여부
        if (!studyService.isExistingStudy(studyId)) {
            ScriptUtils.alert(response, "해당 스터디가 존재하지 않습니다.");
        }

        /**
         * if 스터디 매니저였다면 스터디 매니저 권한 삭제 작업
         */

        if (authentication.getAuthorities().stream()
                .mapToLong((auth)->Long.parseLong(auth.getAuthority()))
                .anyMatch((id)->id == studyId))  {

            Study study = studyRepository.findById(studyId).get();

            if (study.getCrewNumber()>1) {
                ScriptUtils.alert(response,"스터디 매니저는 스터디원이 모두 탈퇴한 후 스터디를 삭제할 수 있습니다.");
            }

            jwtRenewal.managerRemove(response,authentication,studyId);

            // 스터디를 제거하는데 이와 연관관계가 있는 엔티티도 같이 삭제해줘야함
            studyService.deleteStudy(study);

        } else {
            // leaveStudy 호출
            myPageService.leaveStudy(studyId, memberId,response);
        }






        return "redirect:/study/myStudy";
    }
}
