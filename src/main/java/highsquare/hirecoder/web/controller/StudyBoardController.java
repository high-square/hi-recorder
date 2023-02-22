package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.repository.BoardRepository;
import highsquare.hirecoder.entity.Board;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static highsquare.hirecoder.entity.Kind.RECRUIT;

@Controller
@RequestMapping("/study")
@RequiredArgsConstructor
public class StudyBoardController {
    private final BoardRepository boardRepository;

    @GetMapping("/studyMain")
    public String studies(Model model){
        List<Board> studyLists = boardRepository.findStudyAll(RECRUIT);
        model.addAttribute("studies", studyLists);
        return "/study/studyMain";
    }
}
