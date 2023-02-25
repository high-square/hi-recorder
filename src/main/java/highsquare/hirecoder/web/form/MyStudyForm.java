package highsquare.hirecoder.web.form;

import highsquare.hirecoder.entity.ActivityState;
import highsquare.hirecoder.entity.MeetingType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MyStudyForm {

    private Long id;

    private String name;

    private ActivityState activityState;

    private LocalDateTime studyStartDate;

    private LocalDateTime studyFinishDate;

    private Integer crewNumber;

    private MeetingType meetingType;

}
