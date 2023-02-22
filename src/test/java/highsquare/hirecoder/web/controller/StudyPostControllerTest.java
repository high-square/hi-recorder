package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.service.StudyMemberService;
import highsquare.hirecoder.web.form.PostCreateForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudyPostController.class)
class StudyPostControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    StudyMemberService studyMemberService;

    @Test
    @DisplayName("게시글 생성 폼 접근 로직 테스트")
    public void getCreateFormTest() throws Exception {
        // given

        // session에 정상적인 값이 있는 경우
        MockHttpSession session1 = new MockHttpSession();
        session1.setAttribute("study_id", 1L);
        session1.setAttribute("member_id", 1L);
        given(studyMemberService.doesMemberBelongToStudy(1L, 1L))
                .willReturn(true);

        // session에 있는 값이 잘못된 경우
        MockHttpSession session2 = new MockHttpSession();
        session2.setAttribute("study_id", 1L);
        session2.setAttribute("member_id", 2L);
        given(studyMemberService.doesMemberBelongToStudy(1L, 2L))
                .willReturn(false);

        // session에 값이 없는 경우
        MockHttpSession session3 = new MockHttpSession();
        given(studyMemberService.doesMemberBelongToStudy(null, null))
                .willReturn(false);

        // when
        ResultActions resultActions1 = mockMvc.perform(get("/post/create")
                .session(session1));

        ResultActions resultActions2 = mockMvc.perform(get("/post/create")
                .session(session2));

        ResultActions resultActions3 = mockMvc.perform(get("/post/create")
                .session(session3));

        // then
        resultActions1
                .andExpect(view().name("postEdit"))
                .andExpect(model().attributeDoesNotExist("access", "not_member"));

        resultActions2
                .andExpect(view().name("postEdit"))
                .andExpect(model().attribute("not_member", true));

        resultActions3
                .andExpect(view().name("postEdit"))
                .andExpect(model().attribute("access", true));

    }

    @Test
    @DisplayName("폼 데이터 검증 로직(세션 이상)")
    public void postCreateFormValidate0() throws Exception {

        // given

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("study_id", 1L);
        session.setAttribute("member_id", 1L);
        given(studyMemberService.doesMemberBelongToStudy(1L, 1L))
                .willReturn(false);

        MultiValueMap<String, String> normalParams = new LinkedMultiValueMap<>();
        normalParams.add("title", "helloTitle");
        normalParams.add("tags", "tag1");
        normalParams.add("tags", "tag2");
        normalParams.add("content", "# hello jaeDoo");

        // when

        ResultActions result = mockMvc.perform(post("/post/create")
                .session(session)
                .params(normalParams));

        // then

        result.andExpect(view().name("postEdit"))
                .andExpect(model().attributeHasErrors("postCreateForm"));

    }

    @Test
    @DisplayName("폼 데이터 검증 로직(정상일때)")
    public void postCreateFormValidate1() throws Exception {
        // given

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("study_id", 1L);
        session.setAttribute("member_id", 1L);
        given(studyMemberService.doesMemberBelongToStudy(1L, 1L))
                .willReturn(true);

        MultiValueMap<String, String> normalParams = new LinkedMultiValueMap<>();
        normalParams.add("title", "helloTitle");
        normalParams.add("tags", "tag1");
        normalParams.add("tags", "tag2");
        normalParams.add("content", "# hello jaeDoo");
        PostCreateForm expectedForm =
                new PostCreateForm("helloTitle", List.of("tag1", "tag2"), "# hello jaeDoo");

        // when

        ResultActions result = mockMvc.perform(post("/post/create")
                .session(session)
                .params(normalParams));

        // then

        result.andExpect(view().name("post"))
                .andExpect(model().attributeHasNoErrors("postCreateForm"))
                .andExpect(model().attribute("postCreateForm", expectedForm));
    }

    @Test
    @DisplayName("폼 데이터 검증 로직 테스트(널일때)")
    public void postCreateFormValidate2() throws Exception {

        // given

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("study_id", 1L);
        session.setAttribute("member_id", 1L);
        given(studyMemberService.doesMemberBelongToStudy(1L, 1L))
                .willReturn(true);

        MultiValueMap<String, String> nullParams = new LinkedMultiValueMap<>();

        // when

        ResultActions nullResult = mockMvc.perform(post("/post/create")
                .session(session)
                .params(nullParams));

        // then

        nullResult.andExpect(view().name("postEdit"))
                .andExpect(model().attributeHasFieldErrors("postCreateForm", "title", "content"));
    }


}