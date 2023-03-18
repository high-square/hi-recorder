package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.service.*;
import highsquare.hirecoder.dto.StudyCreationInfo;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Kind;
import highsquare.hirecoder.entity.Study;
import highsquare.hirecoder.web.form.StudyCreationForm;
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

import java.security.Principal;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/boards/recruit")
public class StudyCreateFormController {
    private static final int MAX_STUDY_COUNT = 5;
    private final StudyService studyService;
    private final BoardService boardService;
    private final TagService tagService;
    private final StudyMemberService studyMemberService;

    private final ImageService imageService;

    @GetMapping("/create")
    public String getRecruitBoardCreateForm(Principal principal, Model model) {

        Long memberId = Long.parseLong(principal.getName());

        if (memberId == null) {
            model.addAttribute("not_member", true);
        } else if (studyService.isTooManyToManage(memberId, MAX_STUDY_COUNT)) {
            model.addAttribute("max_study", true);
        }

        model.addAttribute("studyCreationForm", new StudyCreationForm());


        return "form/recruitBoardCreateForm";
    }

    @PostMapping("/create")
    public String postRecruitBoardCreateForm(@ModelAttribute StudyCreationForm studyCreationForm, BindingResult bindingResult, Principal principal) {

        Long memberId = Long.parseLong(principal.getName());

        if (memberId == null) {
            bindingResult.reject("access.form.not_member");
        } else if (studyService.isTooManyToManage(memberId, MAX_STUDY_COUNT)) {
            bindingResult.reject("access.form.max_study", new Object[]{MAX_STUDY_COUNT}, null);
        }

        // studyName 검증
        if (!studyCreationForm.isStudyNameTooShort(bindingResult))
            studyCreationForm.isStudyNameTooLong(bindingResult);

        // crewNumber 검증
        if (!studyCreationForm.isCrewNumberTooSmall(bindingResult))
            studyCreationForm.isCrewNumberTooBig(bindingResult);

        // 시작일, 종료일 검증
        studyCreationForm.isTimeValid(bindingResult);

        // 셀렉트 리스트 검증
        studyCreationForm.isSelectionValid(bindingResult);

        // title 검증
        if (!studyCreationForm.isTitleTooShort(bindingResult))
            studyCreationForm.isTitleTooLong(bindingResult);

        // content 검증
        if (!studyCreationForm.isContentTooShort(bindingResult))
            studyCreationForm.isContentTooLong(bindingResult);

        // tags 검증
        if (!studyCreationForm.areTooManyTags(bindingResult))
            studyCreationForm.areAnyTagsTooLong(bindingResult);

        for (ObjectError error : bindingResult.getAllErrors()) {
            log.warn("{}", error.toString());
        }

        if (bindingResult.hasErrors()) {
            return "form/recruitBoardCreateForm";
        }

        // tags를 제외한 모두는 널이 아님을 보장
        // tags는 비었을 때 널이다.

        StudyCreationInfo info = new StudyCreationInfo(studyCreationForm, memberId);
        Study study = studyService.createStudy(info);

        studyMemberService.registerMemberToStudy(study.getId(), memberId);

        studyCreationForm.setOpen(true);
        Board board = boardService.createBoard(memberId, study.getId(), Kind.RECRUIT, studyCreationForm);

        imageService.connectBoardAndImage(board, studyCreationForm.getImages());

        tagService.registerTags(board, studyCreationForm.getTags());

        return String.format("redirect:/boards/recruit/%d/%d", study.getId(), board.getId());
    }
}
