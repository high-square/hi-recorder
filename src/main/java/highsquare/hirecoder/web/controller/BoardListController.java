package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.repository.BoardRepository;
import highsquare.hirecoder.domain.service.BoardListService;
import highsquare.hirecoder.domain.service.BoardService;
import highsquare.hirecoder.entity.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

import static highsquare.hirecoder.entity.Kind.CONTENT;
import static highsquare.hirecoder.entity.Kind.RECRUIT;

@Controller
@RequestMapping("/")

@RequiredArgsConstructor
public class BoardListController {
    private final BoardRepository boardRepository;
    private final BoardListService boardListService;

    @GetMapping("/")
    public String studies(Model model){
        List<Board> studyLists = boardRepository.findStudyAll(RECRUIT);
        model.addAttribute("studies", studyLists);
        return "study/studyMain";
    }

    @GetMapping("/study/{studyId}")
    public String getBoardsByStudyId(@PathVariable Long studyId, HttpSession session, Model model) {
        Long memberId = (Long) session.getAttribute("memberId");
        List<Board> studyBoardList = boardListService.findAllByStudyId(studyId, memberId);
        model.addAttribute("studyBoardList", studyBoardList);
        return "study/myStudyBoardList";
    }
}
