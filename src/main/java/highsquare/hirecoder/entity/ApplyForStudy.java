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
@Table(name = "apply_for_study")
public class ApplyForStudy extends TimeEntity {

    @Id @GeneratedValue
    @Column(name = "apply_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(name = "FK_MEMBER__APPLY_FOR_STUDY"))
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "study_id", foreignKey = @ForeignKey(name = "FK_STUDY__APPLY_FOR_STUDY"))
    private Study study;

    @Enumerated(value = STRING)
    private AuditState auditstate;

    public ApplyForStudy(Member member, Study study, AuditState auditstate) {
        this.member = member;
        this.study = study;
        this.auditstate = auditstate;
    }
}
