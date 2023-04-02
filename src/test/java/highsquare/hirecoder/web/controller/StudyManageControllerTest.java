package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.repository.StudyMemberRepository;
import highsquare.hirecoder.domain.service.ApplyForStudyService;
import highsquare.hirecoder.domain.service.MessageForApplicationService;
import highsquare.hirecoder.domain.service.StudyMemberService;
import highsquare.hirecoder.domain.service.StudyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@WebMvcTest(StudyManageController.class)
@MockBean(JpaMetamodelMappingContext.class)
class StudyManageControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    StudyMemberRepository studyMemberRepository;
    @MockBean
    StudyMemberService studyMemberService;
    @MockBean
    ApplyForStudyService applyForStudyService;
    @MockBean
    StudyService studyService;
    @MockBean
    MessageForApplicationService messageForApplicationService;

    @Test
    public void rejectParamTest() throws Exception {
        // given
        given(applyForStudyService.isValidApplication(6L)).willReturn(true);

        // when
        ResultActions result = mockMvc.perform(post("/studyManage/manager/reject/5/6")
                );

        // then
        result.andReturn().getModelAndView();
    }
}