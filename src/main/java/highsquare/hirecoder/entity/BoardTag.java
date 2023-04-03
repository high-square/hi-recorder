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
    @JoinColumn(name="tag_id", foreignKey = @ForeignKey(name = "FK_TAG__BOARD_TAG"))
    private Tag tag;

    @ManyToOne(fetch=LAZY)
    @JoinColumn(name="board_id", foreignKey = @ForeignKey(name = "FK_BOARD__BOARD_TAG"))
    private Board board;

    public BoardTag(Tag tag, Board board) {
        this.tag = tag;
        this.board = board;
    }
}
