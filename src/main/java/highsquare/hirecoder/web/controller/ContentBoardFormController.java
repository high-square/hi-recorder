package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.constant.SessionConstant;
import highsquare.hirecoder.domain.service.BoardService;
import highsquare.hirecoder.domain.service.StudyMemberService;
import highsquare.hirecoder.domain.service.TagService;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.web.form.BoardForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/boards/content/{study_id}")
public class ContentBoardFormController {
    private final StudyMemberService studyMemberService;
    private final BoardService boardService;
    private final TagService tagService;

    @GetMapping("/create")
    public String getContentBoardCreatePage(@PathVariable(name="study_id") Long studyId,
                                    HttpSession session, Model model) {

        // 테스트용 데이터
        session.setAttribute(SessionConstant.MEMBER_ID, 1L);

        Long memberId = (Long) session.getAttribute(SessionConstant.MEMBER_ID);

        if (isIdNull(studyId, memberId)) {
            model.addAttribute("access", true);
        } else if (!studyMemberService.doesMemberBelongToStudy(studyId, memberId)) {
            model.addAttribute("not_member", true);
        }
        model.addAttribute("boardForm", new BoardForm());

        return "form/contentBoardCreateForm";
    }

    @PostMapping("/create")
    public String createPost(@ModelAttribute BoardForm postForm, BindingResult bindingResult,
                             @PathVariable(name = "study_id") Long studyId, HttpSession session) {

        Long memberId = (Long) session.getAttribute(SessionConstant.MEMBER_ID);

        if (!studyMemberService.doesMemberBelongToStudy(studyId, memberId)) {
            bindingResult.reject("access.not_member");
        }

        if (postForm.isTitleTooShort()) {
            bindingResult.rejectValue("title", "min.title",
                    new Object[] {BoardForm.MIN_TITLE_LENGTH}, null);
        } else if (postForm.isTitleTooLong()) {
            bindingResult.rejectValue("title", "max.title",
                    new Object[]{BoardForm.MAX_TITLE_LENGTH}, null);
        }

        if (postForm.isContentTooShort()) {
            bindingResult.rejectValue("content", "min.content",
                    new Object[]{BoardForm.MIN_CONTENT_LENGTH}, null);
        } else if (postForm.isContentTooLong()) {
            bindingResult.rejectValue("content", "max.content");
        }

        if (postForm.AreTooManyTags()) {
            bindingResult.rejectValue("tags", "max.tags_length",
                    new Object[]{BoardForm.MAX_TAGS_COUNT}, null);
        } else if (postForm.AreAnyTagsTooLong()) {
            bindingResult.rejectValue("tags", "max.tag_length");
        }

        for (ObjectError error : bindingResult.getAllErrors()) {
            log.warn(error.toString());
        }

        if (bindingResult.hasErrors()) {
            return "form/contentBoardCreateForm";
        }
        // <----- 검증 로직 종료

        // title, content는 널이 아님을 보장
        // tags는 비었을 때 널이다.
        assert postForm.getTitle() != null;
        assert postForm.getContent() != null;

        Board board = boardService.createBoard(memberId, studyId, postForm);

        tagService.registerTags(board, postForm.getTags());

        return String.format("redirect:/boards/content/%d/%d", studyId, board.getId());
    }


    private boolean isIdNull(Long studyId, Long writerId) {
        return studyId == null || writerId == null;
    }

}
