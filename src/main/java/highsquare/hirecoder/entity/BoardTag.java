package highsquare.hirecoder.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class BoardTag {

    @Id @GeneratedValue
    @Column(name="board_tag_id")
    private Long id;


}
