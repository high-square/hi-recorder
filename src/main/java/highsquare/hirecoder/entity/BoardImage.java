package highsquare.hirecoder.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Entity
public class BoardImage extends TimeEntity {
    @Id
    private String uuid;

    @Embedded
    private Image image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", foreignKey = @ForeignKey(name = "FK_BOARD__BOARD_IMAGE"))
    private Board board;

    public BoardImage(UUID uuid, Image image, Board board) {
        this.uuid = uuid.toString();
        this.image = image;
        this.board = board;
    }

    public void updateBoard(Board board) {
        this.board = board;
    }
}
