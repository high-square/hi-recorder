package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.service.MyPageService;
import highsquare.hirecoder.entity.*;
import highsquare.hirecoder.page.PageRequestDto;
import highsquare.hirecoder.page.PageResultDto;
import highsquare.hirecoder.web.form.BoardListForm;
import highsquare.hirecoder.web.form.CommentSelectedForm;
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
    public String myStudy(@RequestParam(defaultValue = "" + DEFAULT_PAGE) int page,
                          @RequestParam(defaultValue = "" + DEFAULT_SIZE) int size,
                          Model model, Principal principal) {

        Long memberId = Long.parseLong(principal.getName());

        PageRequestDto requestDto = new PageRequestDto(page, 2);
        PageResultDto<MyStudyForm, Study> myStudyForms = myPageService.pagingMyStudy(memberId, requestDto);

        System.out.println("myStudyForms = " + myStudyForms.dtoList);

        model.addAttribute("data", myStudyForms.dtoList);
        model.addAttribute("memberId", memberId);

        // 페이징 관련
        int nowPage = myStudyForms.getPage();
        int startPage = Math.max(nowPage - 3, 1);
        int endPage = Math.min(nowPage + 3, myStudyForms.getEnd());

        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

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
