package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.service.BoardService;
import highsquare.hirecoder.domain.service.CommentService;
import highsquare.hirecoder.domain.service.LikeOnBoardService;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Comment;
import highsquare.hirecoder.entity.LikeOnBoard;
import highsquare.hirecoder.web.form.BoardSelectedForm;
import highsquare.hirecoder.web.form.CommentSelectedForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;

    private final LikeOnBoardService likeOnBoardService;

    @GetMapping("/{id}")
    public String getBoard(@PathVariable("id") Long id, Model model, HttpServletRequest request, HttpServletResponse response) {
        // DB에서 id에 해당하는 board를 꺼내옴
        Board board = boardService.getBoard(id,request,response);
        // DB에서 board.id에 해당하는 List<Comment>를 꺼내옴
        List<Comment> comments = commentService.findAllByBoardId(board.getId());

        // 세션에 저장된 member와 해당 게시글을 이용해서 LikeOnBoard 엔티티 가져오기
        // 원래라면 로그인 작업에서 session을 생성할텐데 지금 페이지가 없으므로 true를 넣어서 하나 생성해주겠음
        HttpSession session = request.getSession(true);
        // 현재 로그인에 관한 페이지가 없으므로 session 값에 강욱 멤버의 memberId를 임의로 넣어두겠음
        session.setAttribute("memberId",1L);
        LikeOnBoard likeOnBoard = likeOnBoardService.getLikeOnBoard(board.getId(), (Long)session.getAttribute("memberId"));


        // board 엔티티를 boardForm으로 변환
        BoardSelectedForm boardForm =
                new BoardSelectedForm(board.getId(),board.getMember().getId(),board.getMember().getName(),board.getStudy().getId(),board.getTitle(),
                        board.getContent(),board.getFile(),board.getPublicYn(),board.getViewCnt(),board.getLikeCnt(),LocalDateTime.now(),LocalDateTime.now());

        // comment 엔티티를 commentForm으로 변환해서 List 저장
        List<CommentSelectedForm> commentsForm = comments.stream().map(o -> new CommentSelectedForm(o.getId(),
                o.getContent(), o.getLikeCount(), o.getMember().getId(), o.getBoard().getId())).collect(Collectors.toList());

        // view로 전달
        model.addAttribute("board", boardForm);
        model.addAttribute("comments", commentsForm);
        model.addAttribute("likeCheck", likeOnBoard.getLikeCheck());

        return "boards/board";
    }

    // 수정하기를 누르면 수정하기 폼 화면으로 가게 설정
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long boardId, Model model) {
        model.addAttribute("boardId", boardId);
        return "boards/editForm";
    }

    // 삭제하기를 누르면 해당 게시글을 삭제하고 그 게시글이 속했던 스터디 게시글 페이지로 이동하게 했음
    // 삭제 시 foreignKey 제약조건으로 인해 삭제가 안됨 -- 설정해야함
    @GetMapping("/{id}/delete")
    public String deleteBoard(@PathVariable("id") Long boardId, @RequestParam Long studyId, RedirectAttributes redirectAttributes) {
        boardService.deleteBoard(boardId);
        redirectAttributes.addAttribute("studyId", studyId);
        return "redirect:/study/{studyId}/boards";
    }


    @PostMapping("/like")
    @ResponseBody
    public List<Object> likeProcess(@RequestParam(name="board_id") Long boardId,
                              @RequestParam(name="member_id") Long memberId) {
        LikeOnBoard likeOnBoard = likeOnBoardService.updateLike(boardId, memberId);
        Integer likeCnt = likeOnBoardService.countLikeCnt(boardId, memberId);
        List<Object> data = new ArrayList<>();
        data.add(String.valueOf(likeOnBoard.getLikeCheck()));
        data.add(likeCnt);
        return data;
    }

}
