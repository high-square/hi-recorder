package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.constant.SessionConstant;
import highsquare.hirecoder.domain.repository.StudyRepository;
import highsquare.hirecoder.domain.service.BoardService;
import highsquare.hirecoder.domain.service.TagService;
import highsquare.hirecoder.entity.Study;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(RecruitBoardFormController.class)
@MockBean(JpaMetamodelMappingContext.class)
class RecruitBoardFormControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    BoardService boardService;
    @MockBean
    TagService tagService;
    @MockBean
    StudyRepository studyRepository;

    @Test
    @DisplayName("스터디 소개글 생성 폼 접근 로직 테스트")
    public void getCreateFormTest() throws Exception {
        // given

        MockHttpSession session1 = new MockHttpSession();
        session1.setAttribute(SessionConstant.MEMBER_ID, 1L);

        MockHttpSession session2 = new MockHttpSession();
        session2.setAttribute(SessionConstant.MEMBER_ID, 2L);

        given(studyRepository.findAllByManager_Id(1L))
                .willReturn(List.of(new Study(), new Study(), new Study(), new Study(), new Study()));

        given(studyRepository.findAllByManager_Id(2L))
                .willReturn(List.of());

        // when
        ResultActions resultActions1 = mockMvc.perform(get("/boards/recruit/create")
                .session(session1));

        ResultActions resultActions2 = mockMvc.perform(get("/boards/recruit/create")
                .session(session2));

        // then
        resultActions1
                .andExpect(view().name("form/recruitBoardCreateForm"))
                .andExpect(model().attribute("max_study", true));

        resultActions2
                .andExpect(view().name("form/recruitBoardCreateForm"))
                .andExpect(model().attributeDoesNotExist("max_study"));
    }
}