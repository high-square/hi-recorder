package highsquare.hirecoder.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.*;

@Entity
@NoArgsConstructor
@Getter
public class Study {
    @Id @GeneratedValue
    private Long id;

    private String name;
    @Enumerated(value = EnumType.STRING)
    private ActivityState activityState;
    @Enumerated(value = EnumType.STRING)
    private RecruitState recruitState;
    private LocalDateTime studyStartDate;
    private LocalDateTime studyFinishDate;
    private Integer crewNumber;
    private MeetingType meetingType;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name="manager_id")
    private Member manager;

}
