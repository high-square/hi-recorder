package highsquare.hirecoder.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class Tag {

    @Id @GeneratedValue
    @Column(name="tag_id")
    private Long id;

    private String content;

}
