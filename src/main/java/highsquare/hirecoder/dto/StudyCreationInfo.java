package highsquare.hirecoder.dto;

import highsquare.hirecoder.entity.ActivityState;
import highsquare.hirecoder.entity.MeetingType;
import highsquare.hirecoder.entity.RecruitState;
import highsquare.hirecoder.web.form.StudyCreationForm;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class StudyCreationInfo {
    private String studyName;
    private ActivityState activityState;
    private RecruitState recruitState;
    private MeetingType meetingType;
    private Integer crewNumber;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long managerId;

    public StudyCreationInfo(StudyCreationForm form, Long managerId) {

        studyName = form.getStudyName();
        activityState = form.getActivityState();
        recruitState = form.getRecruitState();
        meetingType = form.getMeetingType();
        crewNumber = form.getCrewNumber();
        startDate = formDateToLocalDateTime(form.getStartDate());
        endDate = formDateToLocalDateTime(form.getEndDate());
        this.managerId = managerId;
    }

    public LocalDateTime formDateToLocalDateTime(String date) {

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate localDate = LocalDate.parse(date, format);

        return LocalDateTime.of(localDate, LocalTime.MIN);
    }
}
