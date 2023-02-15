package highsquare.hirecoder.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class StudyMember {

    @Id @GeneratedValue
    @Column(name="study_member_id")
    private Long id;

}
