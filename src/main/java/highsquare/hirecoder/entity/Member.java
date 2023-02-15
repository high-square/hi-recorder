package highsquare.hirecoder.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class Member {

    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;

    private String password;

    private String name;

    private String email;
}
