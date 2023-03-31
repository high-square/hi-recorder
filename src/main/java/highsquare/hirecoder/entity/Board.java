package highsquare.hirecoder.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
@Getter @Setter
@NoArgsConstructor
public class Board extends TimeEntity {
    // 게시글 아이디
    @Id @GeneratedValue()
    @Column(name="board_id")
    private Long id;
    // 게시글 작성자 Member 아이디
    @ManyToOne(fetch= LAZY)
    @JoinColumn(name="member_id")
    private Member member;
    // 게시글 제목
    @ManyToOne(fetch= LAZY)
    @JoinColumn(name="study_id")
    private Study study;

    private String title;

    @Column(columnDefinition="TEXT")
    private String content;

    private String headImageUrl;

    private String publicYn;

    // 조회수
    private int viewCnt;

    // 좋아요 수
    private int likeCnt;

    @Enumerated(value = EnumType.STRING)
    private Kind kind;

    public Board(Member member, Study study, String title, String content, String headImageUrl, boolean publicYn, Kind kind) {
        this.member = member;
        this.study = study;
        this.title = title;
        this.content = content;
        this.headImageUrl = headImageUrl;
        this.publicYn = publicYn ? "y" : "n";;
        this.viewCnt = 0;
        this.likeCnt = 0;
        this.kind = kind;
    }

    public void viewPlus() {
        viewCnt++;
    }


}
