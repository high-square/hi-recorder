package highsquare.hirecoder.entity;

import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Getter
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


}
