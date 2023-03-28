package highsquare.hirecoder.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "messageforapplication")
public class MessageForApplication {

    @Id @GeneratedValue
    private Long id;
    @OneToOne
    @JoinColumn(unique = true, name="apply_id")
    private ApplyForStudy applyForStudy;

    @Column(name="appealmessage")
    private String appealMessage;

    @Column(name="rejectmessage")
    private String rejectMessage;

    public MessageForApplication(ApplyForStudy applyForStudy, String appealMessage) {
        this.applyForStudy = applyForStudy;
        this.appealMessage = appealMessage;
    }
}
