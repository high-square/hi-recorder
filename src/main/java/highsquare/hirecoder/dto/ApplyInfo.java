package highsquare.hirecoder.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ApplyInfo {
    private Long applyId;
    private Long memberId;
    private String memberName;
    private String reason;
    private String email;
    private LocalDate date;

    public ApplyInfo(Long applyId, Long memberId, String memberName, String reason, String email, LocalDateTime dateTime) {
        this.applyId = applyId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.reason = reason;
        this.email = email;
        this.date = dateTime.toLocalDate();
    }
}
