package highsquare.hirecoder.web.form;

import highsquare.hirecoder.entity.Kind;
import highsquare.hirecoder.entity.MeetingType;
import highsquare.hirecoder.entity.RecruitState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private String startDate;
    private String endDate;
    private Integer crewNumber;
    private RecruitState recruitState;
    private MeetingType meetingType;

    public StudyCreationForm(String title, List<String> tags, boolean isPublic,
                             String content, List<String> images, String studyName,
                             String startDate, String endDate, Integer crewNumber,
                              RecruitState recruitState,
                             MeetingType meetingType, String headImageUrl) {
        super(title, tags, isPublic, content, headImageUrl, images, Kind.RECRUIT);
        this.studyName = studyName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.crewNumber = crewNumber;
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

    public boolean isTimeValid() {
        boolean isStartDateEmpty = startDate == null || startDate == "";
        boolean isEndDateEmpty = endDate == null || endDate == "";

        if (isStartDateEmpty || isEndDateEmpty) return false;

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate start = LocalDate.parse(startDate, format);
        LocalDate end = LocalDate.parse(endDate, format);
        return start.isBefore(end);
    }

    public boolean isSelectionValid() {
        boolean isRecruitNull = recruitState == null;
        boolean isMeetingTypeNull = meetingType == null;

        return   !isRecruitNull && !isMeetingTypeNull;
    }

    // BindingResult 유틸
    public boolean isStudyNameTooShort(BindingResult bindingResult) {
        boolean isStudyNameTooShort = isStudyNameTooShort();

        if (isStudyNameTooShort) {
            bindingResult.rejectValue("studyName", "min.form.study_name",
                    new Object[] {MIN_STUDY_NAME_LENGTH}, null);
        }

        return isStudyNameTooShort;
    }

    public boolean isStudyNameTooLong(BindingResult bindingResult) {
        boolean isStudyNameTooLong = isStudyNameTooLong();

        if (isStudyNameTooLong) {
            bindingResult.rejectValue("studyName", "max.form.study_name",
                    new Object[] {MAX_STUDY_NAME_LENGTH}, null);
        }

        return isStudyNameTooLong;
    }

    public boolean isCrewNumberTooSmall(BindingResult bindingResult) {
        boolean isCrewNumberTooSmall = isCrewNumberTooSmall();

        if (isCrewNumberTooSmall) {
            bindingResult.rejectValue("crewNumber", "min.form.crew_number",
                    new Object[] {MIN_CREW_NUMBER}, null);
        }

        return isCrewNumberTooSmall;
    }

    public boolean isCrewNumberTooBig(BindingResult bindingResult) {
        boolean isCrewNumberTooBig = isCrewNumberTooBig();

        if (isCrewNumberTooBig) {
            bindingResult.rejectValue("crewNumber", "max.form.crew_number",
                    new Object[] {MAX_CREW_NUMBER}, null);
        }

        return isCrewNumberTooBig;
    }

    public boolean isTimeValid(BindingResult bindingResult) {
        boolean isTimeValid = isTimeValid();

        if (!isTimeValid) {
            bindingResult.reject("invalid.time");
        }

        return isTimeValid;
    }

    public boolean isSelectionValid(BindingResult bindingResult) {
        boolean isSelectionValid = isSelectionValid();

        if (!isSelectionValid) {
            bindingResult.reject("null.form.select");
        }

        return isSelectionValid;
    }
}
