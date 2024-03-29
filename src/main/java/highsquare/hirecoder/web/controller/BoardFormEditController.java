package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.service.BoardService;
import highsquare.hirecoder.domain.service.StudyMemberService;
import highsquare.hirecoder.domain.service.TagService;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Kind;
import highsquare.hirecoder.entity.Tag;
import highsquare.hirecoder.web.form.BoardForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/boards/{kind}/{study_id}")
public class BoardFormEditController {

    private final StudyMemberService studyMemberService;
    private final BoardService boardService;
    private final TagService tagService;

    @GetMapping("/{board_id}/edit")
    public String getBoardEditForm(@PathVariable(name = "study_id") Long studyId,
                                   @PathVariable(name = "board_id") Long boardId,
                                   @PathVariable(name = "kind") Kind kind,
                                   Principal principal, Model model) {

        Long memberId = Long.parseLong(principal.getName());

        if (isIdNull(studyId, memberId)) {
            log.warn("스터디 id나 멤버 id가 널입니다.");
            model.addAttribute("access", true);
        } else if (!studyMemberService.doesMemberBelongToStudy(studyId, memberId)) {
            log.warn("멤버가 스터디에 속하지 않습니다.");
            model.addAttribute("not_member", true);
        } else if (!boardService.isMemberWriter(memberId, boardId)) {
            log.warn("글쓴이가 아닙니다.");
            model.addAttribute("writer", true);
        }

        BoardForm boardForm = new BoardForm();

        model.addAttribute("edit", true);

        if (model.containsAttribute("access") ||
                model.containsAttribute("not_member")) {
            model.addAttribute("boardForm", boardForm);
            return "form/contentBoardCreateForm";
        }

        Board board = boardService.getBoard(boardId).get();
        List<Tag> foundTags = tagService.getTags(boardId);

        List<String> tags = foundTags.stream().map(Tag::getContent).collect(Collectors.toList());

        boolean isOpen = board.getPublicYn().equals("y") ? true : false;
        boardForm = new BoardForm(board.getTitle(), tags, isOpen, board.getContent(), board.getHeadImageUrl(), null, kind);

        model.addAttribute("boardForm", boardForm);

        return "form/contentBoardCreateForm";
    }

    @PostMapping("/{board_id}/edit")
    public String postContentBoardEditForm(@PathVariable("study_id") Long studyId, @PathVariable("board_id") Long boardId,
                                           @ModelAttribute BoardForm boardForm, BindingResult bindingResult, Principal principal) {

        Long memberId = Long.parseLong(principal.getName());

        if (!studyMemberService.doesMemberBelongToStudy(studyId, memberId)) {
            bindingResult.reject("access.not_member");
        }

        if (!boardService.isMemberWriter(memberId, boardId)) {
            bindingResult.reject("access.form.writer");
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

        Board board = boardService.updateBoard(boardId, boardForm);
        tagService.updateTags(board, boardForm.getTags());

        return String.format("redirect:/boards/%s/%d/%d", board.getKind().name().toLowerCase(), studyId, boardId);
    }

    private boolean isIdNull(Long studyId, Long writerId) {
        return studyId == null || writerId == null;
    }

}
