package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.service.BoardService;
import highsquare.hirecoder.domain.service.ImageService;
import highsquare.hirecoder.domain.service.StudyMemberService;
import highsquare.hirecoder.domain.service.TagService;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Kind;
import highsquare.hirecoder.web.form.BoardForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/boards/content/{study_id}")
public class BoardFormCreateController {
    private final StudyMemberService studyMemberService;
    private final BoardService boardService;
    private final TagService tagService;

    private final ImageService imageService;

    @GetMapping("/create")
    public String getContentBoardCreateForm(@PathVariable(name="study_id") Long studyId,
                                            Principal principal, Model model) {

        Long memberId = Long.parseLong(principal.getName());

        if (isIdNull(studyId, memberId)) {
            model.addAttribute("access", true);
        } else if (!studyMemberService.doesMemberBelongToStudy(studyId, memberId)) {
            model.addAttribute("not_member", true);
        }
        model.addAttribute("boardForm", new BoardForm());

        return "form/contentBoardCreateForm";
    }

    @PostMapping("/create")
    public String postContentBoardCreateForm(@ModelAttribute BoardForm boardForm, BindingResult bindingResult,
                             @PathVariable(name = "study_id") Long studyId, Principal principal) {

        Long memberId = Long.parseLong(principal.getName());

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
            return "form/contentBoardCreateForm";
        }
        // <----- 검증 로직 종료

        // title, content는 널이 아님을 보장
        // tags는 비었을 때 널이다.
        assert boardForm.getTitle() != null;
        assert boardForm.getContent() != null;

        Board board = boardService.createBoard(memberId, studyId, Kind.CONTENT, boardForm);

        imageService.connectBoardAndImage(board, boardForm.getImages());

        tagService.registerTags(board, boardForm.getTags());

        return String.format("redirect:/boards/content/%d/%d", studyId, board.getId());
    }

    private boolean isIdNull(Long studyId, Long writerId) {
        return studyId == null || writerId == null;
    }

}
