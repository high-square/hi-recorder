package highsquare.hirecoder.web.form;

import highsquare.hirecoder.entity.MeetingType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
//@AllArgsConstructor
@NoArgsConstructor
public class MyStudyForm {

    private Long id;

    private String name;


    private LocalDateTime studyStartDate;

    private LocalDateTime studyFinishDate;

    private Integer crewNumber;

    private MeetingType meetingType;

}
