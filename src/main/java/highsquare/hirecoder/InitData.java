package highsquare.hirecoder;

import highsquare.hirecoder.domain.repository.*;
import highsquare.hirecoder.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static highsquare.hirecoder.entity.ActivityState.진행전;
import static highsquare.hirecoder.entity.ActivityState.진행중;
import static highsquare.hirecoder.entity.Kind.CONTENT;
import static highsquare.hirecoder.entity.Kind.RECRUIT;
import static highsquare.hirecoder.entity.MeetingType.오프라인;
import static highsquare.hirecoder.entity.MeetingType.온라인;
import static highsquare.hirecoder.entity.RecruitState.모집완료;
import static highsquare.hirecoder.entity.RecruitState.모집중;

@Component
@RequiredArgsConstructor
public class InitData {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;
    private final StudyMemberRepository studyMemberRepository;


    @PostConstruct
    public void init() {
        // ----------------멤버-----------------
        Member member1 = setMember("강욱", "1234", "kimkangwook@naver.com");
        Member member2 = setMember("재원", "1234", "jaewon@naver.com");
        Member member3 = setMember("경림", "1234", "rim@naver.com");
        Member member4 = setMember("효진", "1234", "hyojin@naver.com");

            // ----------------스터디-----------------
        Study study1 = setStudyData("백엔드1팀", member1, 4, 진행중, 모집완료, 오프라인);
        Study study2 = setStudyData("백엔드2팀", member2, 5, 진행전, 모집중, 온라인);


        // ----------------스터디-멤버-----------------
        StudyMember studyMember1 = setStudy(member1, study1);
        StudyMember studyMember2 = setStudy(member2, study1);
        StudyMember studyMember3 = setStudy(member3, study2);
        StudyMember studyMember4 = setStudy(member4, study2);
        StudyMember studyMember5 = setStudy(member1, study2);


        // ----------------게시글-----------------
        Board board1 = setBoard(member1, study1, "공부할사람 모집합니다 1", 3, 5,"# 1. Content", CONTENT);
        Board board2 = setBoard(member2, study1, "공부할사람 모집합니다 2", 3, 5,"반갑습니다 !!", CONTENT);
        Board board3 = setBoard(member3, study2, "공부할사람 모집합니다 3", 3, 5,"반갑습니다 !!", CONTENT);
        Board board4 = setBoard(member4, study2, "공부할사람 모집합니다 4", 3, 5,"반갑습니다 !!", CONTENT);
        Board board5 = setBoard(member1, study1, "인프런 김영한 강좌 자바 ORM 표준 JPA 프로그래밍 스터디", 5, 10,
                "안녕하세요 오프라인 스터디 인원 구인합니다. 연락 주세요. </p>", RECRUIT);



        // ----------------댓글-----------------
        Comment comment1 = setComment(member1, board2, "글 잘읽었습니다 - 김강욱", 6);
        Comment comment2 = setComment(member2, board3, "글 잘읽었습니다 - 재원", 6);
        Comment comment3 = setComment(member3, board4, "글 잘읽었습니다 - 경림", 6);
        Comment comment4 = setComment(member4, board1, "글 잘읽었습니다 - 효진", 6);

        Comment comment5 = setComment(member1, board3, "글 못읽었습니다 - 김강욱", 6);
        Comment comment6 = setComment(member2, board4, "글 못읽었습니다 - 재원", 6);
        Comment comment7 = setComment(member3, board1, "글 못읽었습니다 - 경림", 6);
        Comment comment8 = setComment(member4, board2, "글 못읽었습니다 - 효진", 6);


        // ----------------DB 저장-----------------
        persistMember(member1, member2, member3, member4);
        persistStudy(study1, study2);
        persistStudyMember(studyMember1, studyMember2, studyMember3, studyMember4, studyMember5);
        persistBoard(board1, board2, board3, board4, board5);
        persistComment(comment1, comment2, comment3, comment4, comment5, comment6, comment7, comment8);
    }

    private void persistComment(Comment comment1, Comment comment2, Comment comment3, Comment comment4, Comment comment5, Comment comment6, Comment comment7, Comment comment8) {
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);
        commentRepository.save(comment4);
        commentRepository.save(comment5);
        commentRepository.save(comment6);
        commentRepository.save(comment7);
        commentRepository.save(comment8);
    }

    private void persistStudyMember(StudyMember studyMember1, StudyMember studyMember2, StudyMember studyMember3, StudyMember studyMember4, StudyMember studyMember5) {
        studyMemberRepository.save(studyMember1);
        studyMemberRepository.save(studyMember2);
        studyMemberRepository.save(studyMember3);
        studyMemberRepository.save(studyMember4);
        studyMemberRepository.save(studyMember5);
    }

    private void persistStudy(Study study1, Study study2) {
        studyRepository.save(study1);
        studyRepository.save(study2);
    }

    private void persistBoard(Board board1, Board board2, Board board3, Board board4, Board board5) {
        boardRepository.save(board1);
        boardRepository.save(board2);
        boardRepository.save(board3);
        boardRepository.save(board4);
        boardRepository.save(board5);
    }

    private void persistMember(Member member1, Member member2, Member member3, Member member4) {
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
    }

    private static Comment setComment(Member member1, Board board2, String content, int likeCnt) {
        Comment comment = new Comment();
        comment.setMember(member1);
        comment.setBoard(board2);
        comment.setContent(content);
        comment.setLikeCnt(likeCnt);
        return comment;
    }

    private static StudyMember setStudy(Member member1, Study study1) {
        StudyMember studyMember = new StudyMember();
        studyMember.setMember(member1);
        studyMember.setStudy(study1);
        return studyMember;
    }

    private static Board setBoard(Member member1, Study study1, String title, int likeCnt, int viewCnt, String content, Kind kind) {
        Board board = new Board();
        board.setMember(member1);
        board.setStudy(study1);
        board.setTitle(title);
        board.setLikeCnt(likeCnt);
        board.setViewCnt(viewCnt);
        board.setContent(content);
        board.setKind(kind);
        return board;
    }

    private static Study setStudyData(String name, Member managerId, int crewCnt, ActivityState activityState, RecruitState recruitState, MeetingType meetingType) {
        Study study = new Study();
        study.setName(name);
        study.setManager(managerId);
        study.setCrewNumber(crewCnt);
        study.setActivityState(activityState);
        study.setRecruitState(recruitState);
        study.setMeetingType(meetingType);

        return study;
    }

    private static Member setMember(String name, String password, String email) {
        Member member = new Member();
        member.setName(name);
        member.setPassword(password);
        member.setEmail(email);
        return member;
    }
}
