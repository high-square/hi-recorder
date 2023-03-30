package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.service.MyPageService;
import highsquare.hirecoder.entity.*;
import highsquare.hirecoder.page.PageRequestDto;
import highsquare.hirecoder.page.PageResultDto;
import highsquare.hirecoder.web.form.BoardListForm;
import highsquare.hirecoder.web.form.CommentSelectedForm;
import highsquare.hirecoder.web.form.MyApplyStudyForm;
import highsquare.hirecoder.web.form.MyStudyForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static highsquare.hirecoder.constant.PageConstant.DEFAULT_PAGE;
import static highsquare.hirecoder.constant.PageConstant.DEFAULT_SIZE;

@Controller
@RequiredArgsConstructor
@RequestMapping("/study")
public class MyPageController {

    private final MyPageService myPageService;


    @GetMapping("/myStudy")
    public String myStudy(@RequestParam(defaultValue = "" + DEFAULT_PAGE) int myStudyPage,
                          @RequestParam(defaultValue = "" + DEFAULT_PAGE) int applyingStudyPage,
                          Model model, Principal principal) {

        Long memberId = Long.parseLong(principal.getName());

        model.addAttribute("memberId", memberId);

        // 진행 중인 스터디 목록
        PageRequestDto myStudyRequestDto = new PageRequestDto(myStudyPage, 2);
        PageResultDto<MyStudyForm, Study> myStudyForms = myPageService.pagingMyStudy(memberId, myStudyRequestDto);

        model.addAttribute("myStudyForms", myStudyForms.dtoList);

        // 페이징 관련
        int myStudyNowPage = myStudyForms.getPage();
        int myStudyStartPage = Math.max(myStudyNowPage - 3, 1);
        int myStudyEndPage = Math.min(myStudyNowPage + 3, myStudyForms.getEnd());

        model.addAttribute("myStudyNowPage", myStudyNowPage);
        model.addAttribute("myStudyStartPage", myStudyStartPage);
        model.addAttribute("myStudyEndPage", myStudyEndPage);

        // 신청한 스터디 목록
        PageRequestDto applyingStudyRequestDto = new PageRequestDto(applyingStudyPage, 2);
        PageResultDto<MyApplyStudyForm, ApplyForStudy> myApplyingStudyForms = myPageService.pagingMyApplyingStudy(memberId, applyingStudyRequestDto);

        model.addAttribute("applyingStudyForms", myApplyingStudyForms.dtoList);

        // 페이징 관련
        int applyingStudyNowPage = myApplyingStudyForms.getPage();
        int applyingStudyStartPage = Math.max(applyingStudyNowPage - 3, 1);
        int applyingStudyEndPage = Math.min(applyingStudyNowPage + 3, myApplyingStudyForms.getEnd());

        model.addAttribute("applyingStudyNowPage", applyingStudyNowPage);
        model.addAttribute("applyingStudyStartPage", applyingStudyStartPage);
        model.addAttribute("applyingStudyEndPage", applyingStudyEndPage);

        return "myPage";
    }

    @GetMapping("/{studyId}/{memberId}")
    public String myStudyPostList(@PathVariable(value="studyId") Long studyId,
                                  @PathVariable(value="memberId") Long memberId,
                                  Model model, HttpSession session) {

        model.addAttribute("studyId", String.valueOf(studyId));

        String nameById = myPageService.findNameById(memberId);
        model.addAttribute("memberName", String.valueOf(nameById));

        // db에서 memberId와 studyId가 일치하는 board를 찾는다. -> list
        List<Board> myPosts = myPageService.findMyPosts(studyId, memberId);
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
    public String leaveStudy(@PathVariable(value="studyId") String studyId,
                             @PathVariable(value="memberId") String memberId) {
        // leaveStudy 호출
        myPageService.leaveStudy(Long.parseLong(studyId), Long.parseLong(memberId));
        return "redirect:/study/myStudy";
    }
}
