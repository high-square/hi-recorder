package highsquare.hirecoder.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor
public class BoardTag {

    @Id @GeneratedValue
    @Column(name="board_tag_id")
    private Long id;

    @ManyToOne(fetch= LAZY)
    @JoinColumn(name="tag_id")
    private Tag tag;

    @ManyToOne(fetch=LAZY)
    @JoinColumn(name="board_id")
    private Board board;

    public BoardTag(Tag tag, Board board) {
        this.tag = tag;
        this.board = board;
    }
}
