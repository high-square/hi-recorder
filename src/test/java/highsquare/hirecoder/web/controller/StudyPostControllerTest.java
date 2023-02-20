package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.service.StudyMemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        session1.setAttribute("writer_id", 1L);
        given(studyMemberService.doesMemberBelongToStudy(1L, 1L))
                .willReturn(true);

        // session에 있는 값이 잘못된 경우
        MockHttpSession session2 = new MockHttpSession();
        session2.setAttribute("study_id", 1L);
        session2.setAttribute("writer_id", 2L);
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

}