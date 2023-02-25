package highsquare.hirecoder.web.form;

import highsquare.hirecoder.entity.Member;
import highsquare.hirecoder.entity.Study;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Data
@AllArgsConstructor
public class BoardSelectedForm {

    private Long id;

    private Long memberId;

    private String memberName;

    private Long studyId;

    private String studyName;

    private String title;

    private String content;

    private String file;

    private String publicYn;

    private int viewCnt;

    private int likeCnt;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;


}
