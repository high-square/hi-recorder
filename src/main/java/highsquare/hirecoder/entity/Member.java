package highsquare.hirecoder.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Member extends TimeEntity {

    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;

    private String password;

    private String name;

    private String email;
}
