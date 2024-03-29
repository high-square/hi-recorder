package highsquare.hirecoder.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Study extends TimeEntity {
    @Id @GeneratedValue
    private Long id;

    private String name;


    @Enumerated(value = EnumType.STRING)
    private RecruitState recruitState;

    private LocalDateTime studyStartDate;

    private LocalDateTime studyFinishDate;

    private Integer crewNumber;

    @Enumerated(value = EnumType.STRING)
    private MeetingType meetingType;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name="manager_id", foreignKey = @ForeignKey(name = "FK_MANAGER__STUDY"))
    private Member manager;

    public Study(String name, RecruitState recruitState, LocalDateTime studyStartDate, LocalDateTime studyFinishDate, Integer crewNumber, MeetingType meetingType, Member manager) {
        this.name = name;
        this.recruitState = recruitState;
        this.studyStartDate = studyStartDate;
        this.studyFinishDate = studyFinishDate;
        this.crewNumber = crewNumber;
        this.meetingType = meetingType;
        this.manager = manager;
    }
}
