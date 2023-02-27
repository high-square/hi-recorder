package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.constant.SessionConstant;
import highsquare.hirecoder.domain.service.BoardService;
import highsquare.hirecoder.domain.service.StudyMemberService;
import highsquare.hirecoder.domain.service.TagService;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Kind;
import highsquare.hirecoder.entity.Study;
import highsquare.hirecoder.web.form.BoardForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpSession;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(RecruitBoardFormController.class)
class RecruitBoardFormControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    StudyMemberService studyMemberService;

    @MockBean
    BoardService boardService;

    @MockBean
    TagService tagService;

    @Test
    @DisplayName("스터디 소개글 생성 폼 접근 로직 테스트")
    public void getCreateFormTest() throws Exception {
        // given

        Study study1 = new Study();
        study1.setId(0L);
        study1.setName("study1");

        Study study2 = new Study();
        study2.setId(1L);
        study2.setName("study2");

        MockHttpSession session1 = new MockHttpSession();
        session1.setAttribute(SessionConstant.MEMBER_ID, 1L);

        MockHttpSession session2 = new MockHttpSession();
        session2.setAttribute(SessionConstant.MEMBER_ID, 2L);

        // 스터디가 존재하는 경우

        given(studyMemberService.getAllMembersStudy(1L))
                .willReturn(List.of(study1, study2));

        // 스터디가 없을 경우
        given(studyMemberService.getAllMembersStudy(2L))
                .willReturn(List.of());

        // when
        ResultActions resultActions1 = mockMvc.perform(get("/boards/recruit/create")
                .session(session1));

        ResultActions resultActions2 = mockMvc.perform(get("/boards/recruit/create")
                .session(session2));

        // then
        resultActions1
                .andExpect(view().name("form/recruitBoardCreateForm"))
                .andExpect(model().attributeDoesNotExist("unsociable"));

        resultActions2
                .andExpect(view().name("form/recruitBoardCreateForm"))
                .andExpect(model().attribute("unsociable", true));
    }

    @Test
    @DisplayName("폼 데이터 검증 로직(정상일때)")
    public void BoardFormValidate1() throws Exception {
        // given

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConstant.MEMBER_ID, 1L);
        given(studyMemberService.doesMemberBelongToStudy(1L, 1L))
                .willReturn(true);

        BoardForm expectedCreateForm = new BoardForm(1L,
                "helloTitle", List.of("tag1", "tag2"), "# hello jaeDoo");

        Board expectedBoard = new Board();
        expectedBoard.setId(1L);
        given(boardService.createBoard(1L, 1L, Kind.RECRUIT, expectedCreateForm))
                .willReturn(expectedBoard);

        MultiValueMap<String, String> normalParams = new LinkedMultiValueMap<>();
        normalParams.add("studyId", expectedCreateForm.getStudyId().toString());
        normalParams.add("title", expectedCreateForm.getTitle());
        normalParams.add("tags", expectedCreateForm.getTags().get(0));
        normalParams.add("tags", expectedCreateForm.getTags().get(1));
        normalParams.add("content", expectedCreateForm.getContent());

        // when

        ResultActions result = mockMvc.perform(post("/boards/recruit/create")
                .session(session)
                .params(normalParams));

        // then

        result.andExpect(view().name("redirect:/boards/recruit/1/1"));
    }

    @Test
    @DisplayName("폼 데이터 검증 로직(스터디가 널일 때)")
    public void BoardFormValidate2() throws Exception {

        // given

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConstant.MEMBER_ID, 1L);
        given(studyMemberService.doesMemberBelongToStudy(1L, 1L))
                .willReturn(true);

        BoardForm expectedCreateForm = new BoardForm(null,
                "helloTitle", List.of("tag1", "tag2"), "# hello jaeDoo");

        Board expectedBoard = new Board();
        expectedBoard.setId(1L);
        given(boardService.createBoard(1L, 1L, Kind.RECRUIT, expectedCreateForm))
                .willReturn(expectedBoard);

        MultiValueMap<String, String> normalParams = new LinkedMultiValueMap<>();
        normalParams.add("title", expectedCreateForm.getTitle());
        normalParams.add("tags", expectedCreateForm.getTags().get(0));
        normalParams.add("tags", expectedCreateForm.getTags().get(1));
        normalParams.add("content", expectedCreateForm.getContent());

        // when

        ResultActions result = mockMvc.perform(post("/boards/recruit/create")
                .session(session)
                .params(normalParams));

        // then

        result.andExpect(view().name("form/recruitBoardCreateForm"))
                .andExpect(model().attributeHasErrors("boardForm"));

    }

    @Test
    @DisplayName("폼 데이터 검증 로직 테스트(널일때)")
    public void BoardFormValidate3() throws Exception {

        // given

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("study_id", 1L);
        session.setAttribute(SessionConstant.MEMBER_ID, 1L);
        given(studyMemberService.doesMemberBelongToStudy(1L, 1L))
                .willReturn(true);

        MultiValueMap<String, String> nullParams = new LinkedMultiValueMap<>();

        // when

        ResultActions nullResult = mockMvc.perform(post("/boards/recruit/create")
                .session(session)
                .params(nullParams));

        // then

        nullResult.andExpect(view().name("form/recruitBoardCreateForm"))
                .andExpect(model().attributeHasFieldErrors("boardForm", "title", "content"));
    }
}