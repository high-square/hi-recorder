package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.repository.BoardRepository;
import highsquare.hirecoder.domain.service.BoardListService;
import highsquare.hirecoder.domain.service.BoardService;
import highsquare.hirecoder.domain.service.TagService;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.BoardTag;
import highsquare.hirecoder.entity.Kind;
import highsquare.hirecoder.entity.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

import static highsquare.hirecoder.entity.Kind.RECRUIT;


@Controller
@RequestMapping("/")

@RequiredArgsConstructor
public class BoardListController {
    private final BoardListService boardListService;
    private final TagService tagService;

    // 스터디 구인 글이기 때문에 로그인 로직이 없어도 됨
    // 아무나 접속 가능
    @GetMapping("/")
    public String studies(Model model,
                          @PageableDefault(page = 0, size =8, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                          String searchKeyword)
    {
        // 게시글 목록 가져오기
        Page<Board> list = boardListService.boardList(pageable);


        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());


        if(searchKeyword == null){
            list = boardListService.boardList(pageable);
        }else{
            list = boardListService.boardSearchList(searchKeyword, pageable);
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "viewCnt");
        Pageable pageable2 = PageRequest.of(0, 5, sort);

        // 인기 게시글 목록 가져오기
        List<Board> popularList = boardListService.getPopularBoards(RECRUIT, pageable2);

        // 태그 목록 가져오기
        List<Map<String, Object>> tagList = tagService.getTagList();


        model.addAttribute("studies", list);
        model.addAttribute("popularList", popularList);
        model.addAttribute("tagList", tagList);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "study/studyMain";
    }


    @GetMapping("/study/{studyId}")
    public String getBoardsByStudyId(@PathVariable Long studyId, HttpSession session
                                    , Model model
                                    , @PageableDefault(page = 0, size =8, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
                                    , String searchKeyword)
    {
        Long memberId = (Long) session.getAttribute("memberId");
        Page<Board> studyBoardList = boardListService.findAllByStudyId(studyId, memberId, pageable);

        int nowPage = studyBoardList.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, studyBoardList.getTotalPages());

        if(searchKeyword == null){
            studyBoardList = boardListService.findAllByStudyId(studyId, memberId, pageable);
        }else{
            studyBoardList = boardListService.studyBoardSearchList(studyId, memberId, searchKeyword, pageable);
        }

        model.addAttribute("studyBoardList", studyBoardList);
        model.addAttribute("studyId", studyId);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "study/myStudyBoardList";
    }
}
