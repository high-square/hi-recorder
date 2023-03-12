package highsquare.hirecoder.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter @Setter
@NoArgsConstructor
public class MessageForApplication {

    @Id
    @Column(unique = true)
    private Long id;

    private String appealMessage;

    private String rejectMessage;

}
