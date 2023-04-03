package highsquare.hirecoder.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "message_for_application")
public class MessageForApplication {

    @Id @GeneratedValue
    private Long id;
    @OneToOne
    @JoinColumn(unique = true, name="apply_id", foreignKey = @ForeignKey(name = "FK_APPLY_FOR_STUDY__MESSAGE_FOR_APPLICATION"))
    private ApplyForStudy applyForStudy;

    @Column(name="appeal_message")
    private String appealMessage;

    @Column(name="reject_message")
    private String rejectMessage;

    public MessageForApplication(ApplyForStudy applyForStudy, String appealMessage) {
        this.applyForStudy = applyForStudy;
        this.appealMessage = appealMessage;
    }

    public MessageForApplication(ApplyForStudy applyForStudy, String appealMessage, String rejectMessage) {
        this.applyForStudy = applyForStudy;
        this.appealMessage = appealMessage;
        this.rejectMessage = rejectMessage;
    }
}
