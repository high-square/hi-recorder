package highsquare.hirecoder.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@Setter
public class Comment extends TimeEntity {

    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    private String content;

    @CreatedDate
    private LocalDateTime createdTime;

    @LastModifiedDate
    private LocalDateTime modifiedTime;

    private int likeCnt;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="member_id", foreignKey = @ForeignKey(name = "FK_MEMBER__COMMENT"))
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="board_id", foreignKey = @ForeignKey(name = "FK_BOARD__COMMENT"))
    private Board board;

}
