package highsquare.hirecoder.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class LikeOnBoard extends TimeEntity {

    @Id
    @GeneratedValue
    @Column(name="like_board_id")
    private Long id;

    @ManyToOne(fetch= LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @ManyToOne(fetch= LAZY)
    @JoinColumn(name="board_id")
    private Board board;

    // 0이면 좋아요 클릭 x, 1이면 좋아요 클릭 o
    // 화면단에서 0,1로 관리하기 위해 간단하게 설정함
    private int likeCheck;

}
