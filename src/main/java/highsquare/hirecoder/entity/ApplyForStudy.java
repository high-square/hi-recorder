package highsquare.hirecoder.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;

import static javax.persistence.EnumType.*;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "applyforstudy")
public class ApplyForStudy {

    @Id @GeneratedValue
    @Column(name = "apply_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @Enumerated(value = STRING)
    private AuditState auditstate;

    public ApplyForStudy(Member member, Study study, AuditState auditstate) {
        this.member = member;
        this.study = study;
        this.auditstate = auditstate;
    }
}
