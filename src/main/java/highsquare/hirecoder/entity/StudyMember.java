package highsquare.hirecoder.entity;

import lombok.Getter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
public class StudyMember {

    @Id @GeneratedValue
    @Column(name="study_member_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="study_id")
    private Study study;

}
