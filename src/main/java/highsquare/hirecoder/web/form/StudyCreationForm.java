package highsquare.hirecoder.web.form;

import highsquare.hirecoder.entity.ActivityState;
import highsquare.hirecoder.entity.MeetingType;
import highsquare.hirecoder.entity.RecruitState;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class StudyCreationForm extends BoardForm {

    public static final int MIN_STUDY_NAME_LENGTH = 1;
    public static final int MAX_STUDY_NAME_LENGTH = 50;
    public static final int MIN_CREW_NUMBER = 1;
    public static final int MAX_CREW_NUMBER = 100;

    private String studyName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer crewNumber;
    private ActivityState activityState = ActivityState.진행전;
    private RecruitState recruitState = RecruitState.모집중;
    private MeetingType meetingType = MeetingType.오프라인;

    public StudyCreationForm(String title, List<String> tags, String content, String studyName, LocalDateTime startDate, LocalDateTime endDate, Integer crewNumber, ActivityState activityState, RecruitState recruitState, MeetingType meetingType) {
        super(title, tags, content);
        this.studyName = studyName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.crewNumber = crewNumber;
        this.activityState = activityState;
        this.recruitState = recruitState;
        this.meetingType = meetingType;
    }

    // 폼 검증 로직
    public boolean isStudyNameTooShort() {
        return studyName == null || studyName.length() < MIN_STUDY_NAME_LENGTH;
    }

    public boolean isStudyNameTooLong() {
        return studyName != null && studyName.length() > MAX_STUDY_NAME_LENGTH;
    }

    public boolean isCrewNumberTooSmall() {
        return crewNumber == null || crewNumber < MIN_CREW_NUMBER;
    }

    public boolean isCrewNumberTooBig() {
        return crewNumber != null && crewNumber > MAX_CREW_NUMBER;
    }

    public boolean isTimeValidate() {
        boolean isStartDateNull = startDate == null;
        boolean isEndDateNull = endDate == null;

        if (isStartDateNull || isEndDateNull) return false;

        return startDate.isBefore(endDate);
    }
}
