package highsquare.hirecoder.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Entity
public class BoardImage {
    @Id
    private String uuid;

    @Embedded
    private Image image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
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
