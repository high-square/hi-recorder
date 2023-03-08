package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.constant.SessionConstant;
import highsquare.hirecoder.domain.service.BoardService;
import highsquare.hirecoder.domain.service.ImageService;
import highsquare.hirecoder.domain.service.StudyMemberService;
import highsquare.hirecoder.domain.service.TagService;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Kind;
import highsquare.hirecoder.web.form.BoardForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpSession;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardFormCreateController.class)
@MockBean(JpaMetamodelMappingContext.class)
class BoardFormCreateControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    StudyMemberService studyMemberService;

    @MockBean
    BoardService boardService;

    @MockBean
    TagService tagService;

    @MockBean
    ImageService imageService;

    @Test
    @DisplayName("게시글 생성 폼 접근 로직 테스트")
    public void getCreateFormTest() throws Exception {
        // given

        // session에 정상적인 값이 있는 경우
        MockHttpSession session1 = new MockHttpSession();
        session1.setAttribute(SessionConstant.MEMBER_ID, 1L);
        given(studyMemberService.doesMemberBelongToStudy(1L, 1L))
                .willReturn(true);

        // session에 있는 값이 잘못된 경우
        MockHttpSession session2 = new MockHttpSession();
        session2.setAttribute(SessionConstant.MEMBER_ID, 2L);
        given(studyMemberService.doesMemberBelongToStudy(1L, 2L))
                .willReturn(false);

        // session에 값이 없는 경우
        MockHttpSession session3 = new MockHttpSession();
        given(studyMemberService.doesMemberBelongToStudy(1L, null))
                .willReturn(false);

        // when
        ResultActions resultActions1 = mockMvc.perform(get("/boards/content/1/create")
                .session(session1));

        ResultActions resultActions2 = mockMvc.perform(get("/boards/content/1/create")
                .session(session2));

        ResultActions resultActions3 = mockMvc.perform(get("/boards/content/1/create")
                .session(session3));

        // then
        resultActions1
                .andExpect(view().name("form/contentBoardCreateForm"))
                .andExpect(model().attributeDoesNotExist("access", "not_member"));

        /**
         * 통합 테스트를 위해 컨트롤러에서 세션에 멤버 아이디를 넣어줄 경우
         * 테스트가 실패할 수 있다.
         * {@link BoardFormCreateController#getPostCreatePage(Long, HttpSession, Model)}
         */
        resultActions2
                .andExpect(view().name("form/contentBoardCreateForm"))
                .andExpect(model().attribute("not_member", true));

        resultActions3
                .andExpect(view().name("form/contentBoardCreateForm"))
                .andExpect(model().attribute("access", true));

    }

    @Test
    @DisplayName("폼 데이터 검증 로직(정상일때)")
    public void BoardFormValidate1() throws Exception {
        // given

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConstant.MEMBER_ID, 1L);
        given(studyMemberService.doesMemberBelongToStudy(1L, 1L))
                .willReturn(true);

        BoardForm expectedCreateForm = new BoardForm(
                "helloTitle", List.of("tag1", "tag2"), "# hello jaeDoo", null);

        Board expectedBoard = new Board();
        expectedBoard.setId(1L);
        given(boardService.createBoard(1L, 1L, Kind.CONTENT, expectedCreateForm))
                .willReturn(expectedBoard);

        MultiValueMap<String, String> normalParams = new LinkedMultiValueMap<>();
        normalParams.add("title", expectedCreateForm.getTitle());
        normalParams.add("tags", expectedCreateForm.getTags().get(0));
        normalParams.add("tags", expectedCreateForm.getTags().get(1));
        normalParams.add("content", expectedCreateForm.getContent());

        // when

        ResultActions result = mockMvc.perform(post("/boards/content/1/create")
                .session(session)
                .params(normalParams));

        // then

        result.andExpect(view().name("redirect:/boards/content/1/1"));
    }

    @Test
    @DisplayName("폼 데이터 검증 로직(세션 이상)")
    public void BoardFormValidate2() throws Exception {

        // given

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConstant.MEMBER_ID, 1L);
        given(studyMemberService.doesMemberBelongToStudy(1L, 1L))
                .willReturn(false);

        BoardForm expectedCreateForm = new BoardForm(
                "helloTitle", List.of("tag1", "tag2"), "# hello jaeDoo", null);

        Board expectedBoard = new Board();
        expectedBoard.setId(1L);
        given(boardService.createBoard(1L, 1L, Kind.CONTENT, expectedCreateForm))
                .willReturn(expectedBoard);

        MultiValueMap<String, String> normalParams = new LinkedMultiValueMap<>();
        normalParams.add("title", expectedCreateForm.getTitle());
        normalParams.add("tags", expectedCreateForm.getTags().get(0));
        normalParams.add("tags", expectedCreateForm.getTags().get(1));
        normalParams.add("content", expectedCreateForm.getContent());

        // when

        ResultActions result = mockMvc.perform(post("/boards/content/1/create")
                .session(session)
                .params(normalParams));

        // then

        result.andExpect(view().name("form/contentBoardCreateForm"))
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

        ResultActions nullResult = mockMvc.perform(post("/boards/content/1/create")
                .session(session)
                .params(nullParams));

        // then

        nullResult.andExpect(view().name("form/contentBoardCreateForm"))
                .andExpect(model().attributeHasFieldErrors("boardForm", "title", "content"));
    }


}