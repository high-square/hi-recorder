package highsquare.hirecoder.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "messageforapplication")
public class MessageForApplication {

    @Id
    @Column(unique = true,name="message_id")
    private Long id;

    @Column(name="appealmessage")
    private String appealMessage;

    @Column(name="rejectmessage")
    private String rejectMessage;

}
