package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.constant.SessionConstant;
import highsquare.hirecoder.domain.service.*;
import highsquare.hirecoder.dto.StudyCreationInfo;
import highsquare.hirecoder.entity.*;
import highsquare.hirecoder.web.form.StudyCreationForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(StudyCreateFormController.class)
@MockBean(JpaMetamodelMappingContext.class)
class StudyCreateFormControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    BoardService boardService;
    @MockBean
    TagService tagService;
    @MockBean
    StudyService studyService;
    @MockBean
    StudyMemberService studyMemberService;

    @MockBean
    ImageService imageService;

    @Test
    @DisplayName("스터디 소개글 생성 폼 접근 로직 테스트")
    public void getCreateFormTest() throws Exception {
        // given

        MockHttpSession session1 = new MockHttpSession();
        session1.setAttribute(SessionConstant.MEMBER_ID, 1L);

        MockHttpSession session2 = new MockHttpSession();
        session2.setAttribute(SessionConstant.MEMBER_ID, 2L);


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

    @Test
    @DisplayName("POST 로직(정상 동작시)")
    public void postCreateFormTest1() throws Exception {

        // given

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConstant.MEMBER_ID, 1L);

        StudyCreationForm expectedForm = new StudyCreationForm("벡엔드 스터디입니다.", List.of("tag1", "tag2"), true, "# hello world", null, "백엔드 스터디",
                "2023-01-01", "2023-03-31", 5, ActivityState.진행전, RecruitState.모집중, MeetingType.오프라인);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", expectedForm.getTitle());
        params.add("tags", expectedForm.getTags().get(0));
        params.add("tags", expectedForm.getTags().get(1));
        params.add("content", expectedForm.getContent());
        params.add("studyName", expectedForm.getStudyName());
        params.add("startDate", expectedForm.getStartDate());
        params.add("endDate", expectedForm.getEndDate());
        params.add("crewNumber", Integer.toString(expectedForm.getCrewNumber()));
        params.add("activityState", expectedForm.getActivityState().name());
        params.add("recruitState", expectedForm.getRecruitState().name());
        params.add("meetingType", expectedForm.getMeetingType().name());

        Study expectedStudy = new Study();
        expectedStudy.setId(10L);

        Board expectedBoard = new Board();
        expectedBoard.setId(9L);

        given(studyService.isTooManyToManage(1L, 5)).willReturn(false);
        given(studyService.createStudy(any(StudyCreationInfo.class))).willReturn(expectedStudy);
        given(boardService.createBoard(1L, 10L, Kind.RECRUIT, expectedForm))
                .willReturn(expectedBoard);

        // when

        ResultActions perform = mockMvc.perform(post("/boards/recruit/create")
                .params(params)
                .session(session));

        // then

        perform.andExpect(view().name("redirect:/boards/recruit/10/9"))
                .andExpect(model().hasNoErrors());
    }

    @Test
    @DisplayName("POST 로직(파라미터가 널일때)")
    public void postCreateFormTest2() throws Exception {

        // given

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionConstant.MEMBER_ID, 1L);

        given(studyService.isTooManyToManage(1L, 5)).willReturn(false);

        // when

        ResultActions perform = mockMvc.perform(post("/boards/recruit/create")
                .session(session));

        // then

        perform.andExpect(view().name("form/recruitBoardCreateForm"))
                .andExpect(model().attributeHasFieldErrors(
                        "studyCreationForm", "content", "title", "crewNumber", "studyName"));
    }
}