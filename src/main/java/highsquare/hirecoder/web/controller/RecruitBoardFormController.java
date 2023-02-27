package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.constant.SessionConstant;
import highsquare.hirecoder.domain.service.BoardService;
import highsquare.hirecoder.domain.service.StudyMemberService;
import highsquare.hirecoder.domain.service.TagService;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Kind;
import highsquare.hirecoder.entity.Study;
import highsquare.hirecoder.web.form.BoardForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/boards/recruit")
public class RecruitBoardFormController {

    private final StudyMemberService studyMemberService;
    private final BoardService boardService;
    private final TagService tagService;

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

    @PostMapping("/create")
    public String postRecruitBoardCreateForm(@ModelAttribute BoardForm boardForm, BindingResult bindingResult, HttpSession session) {

        Long memberId = (Long) session.getAttribute(SessionConstant.MEMBER_ID);
        Long studyId = boardForm.getStudyId();

        if (!studyMemberService.doesMemberBelongToStudy(studyId, memberId)) {
            bindingResult.reject("access.not_member");
        }

        // title 검증
        if (!boardForm.isTitleTooShort(bindingResult))
            boardForm.isTitleTooLong(bindingResult);

        // content 검증
        if (!boardForm.isContentTooShort(bindingResult))
            boardForm.isContentTooLong(bindingResult);

        // tags 검증
        if (!boardForm.areTooManyTags(bindingResult))
            boardForm.areAnyTagsTooLong(bindingResult);

        for (ObjectError error : bindingResult.getAllErrors()) {
            log.warn("{}", error.toString());
        }

        if (bindingResult.hasErrors()) {
            return "form/recruitBoardCreateForm";
        }

        // title, content는 널이 아님을 보장
        // tags는 비었을 때 널이다.
        assert boardForm.getTitle() != null;
        assert boardForm.getContent() != null;

        Board board = boardService.createBoard(memberId, studyId, Kind.RECRUIT, boardForm);

        tagService.registerTags(board, boardForm.getTags());

        return String.format("redirect:/boards/recruit/%d/%d", boardForm.getStudyId(), board.getId());
    }
}
