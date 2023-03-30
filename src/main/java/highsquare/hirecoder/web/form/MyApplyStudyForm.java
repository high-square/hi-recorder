package highsquare.hirecoder.web.form;

import highsquare.hirecoder.entity.AuditState;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MyApplyStudyForm {

    private Long id;

    private Long studyId;

    private String studyName;

    private AuditState auditstate;
}
