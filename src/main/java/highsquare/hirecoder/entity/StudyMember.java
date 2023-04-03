package highsquare.hirecoder.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.EnumType.*;
import static javax.persistence.FetchType.*;

@Entity
@Getter @Setter
public class StudyMember extends TimeEntity {

    @Id @GeneratedValue
    @Column(name="study_member_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="member_id", foreignKey = @ForeignKey(name = "FK_MEMBER__STUDY_MEMBER"))
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="study_id", foreignKey = @ForeignKey(name = "FK_STUDY__STUDY_MEMBER"))
    private Study study;

    @Enumerated(value = STRING)
    private AttendState attendState;
}
