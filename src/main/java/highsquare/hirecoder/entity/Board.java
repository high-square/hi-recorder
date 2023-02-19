package highsquare.hirecoder.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
@Getter @Setter
public class Board {
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
    // 파일
    /**
     * 파일 테이블을 따로 뺄 거면 여기에 파일 테이블에 해당하는 아이디가 들어가야 하고,
     * 아니라면 file 의 변수에 filePath가 들어가야 함
     */
    private String file;
    // 생성일시
    // 수정일시
    // 공개 여부
    private String publicYn;
    // 스터디 아이디

    // 조회수
    private int viewCnt;
    // 좋아요 수
    private int likeCnt;

    public void viewPlus() {
        viewCnt++;
    }

    public void likeCntPlus() {
        likeCnt++;
    }

}
