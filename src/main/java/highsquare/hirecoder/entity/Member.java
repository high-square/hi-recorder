package highsquare.hirecoder.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Builder
@Entity
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Member extends TimeEntity {

    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(nullable = false)
    private String password;

    private String email;

}
