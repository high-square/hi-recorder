package highsquare.hirecoder;

import highsquare.hirecoder.domain.repository.*;
import highsquare.hirecoder.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;

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

    private static final Long DEFAULT_CREATED_BY = 1L;

    private final PlatformTransactionManager platformTransactionManager;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;
    private final StudyMemberRepository studyMemberRepository;
    private final ApplyForStudyRepository applyForStudyRepository;
    private final MessageForApplicationRepository messageForApplicationRepository;

//    @PostConstruct
    public void init() {

        TransactionStatus ts = platformTransactionManager.getTransaction(new DefaultTransactionDefinition());

        // ----------------멤버-----------------
        List<Member> members = List.of(
                setMember("김강욱", "$2y$04$yzTsYmYmgOLVSrOY1eiIm.Zupgpsukn.poLZL6cZqWsl1DK1dX.xi", "kimkangwook@naver.com"),
                setMember("송재원", "$2y$04$yzTsYmYmgOLVSrOY1eiIm.Zupgpsukn.poLZL6cZqWsl1DK1dX.xi", "jaewon@naver.com"),
                setMember("박경림", "$2y$04$yzTsYmYmgOLVSrOY1eiIm.Zupgpsukn.poLZL6cZqWsl1DK1dX.xi", "rim@naver.com"),
                setMember("이효진", "$2y$04$yzTsYmYmgOLVSrOY1eiIm.Zupgpsukn.poLZL6cZqWsl1DK1dX.xi", "hyojin@naver.com"),

                setMember("aaa", "$2y$04$yzTsYmYmgOLVSrOY1eiIm.Zupgpsukn.poLZL6cZqWsl1DK1dX.xi", null),
                setMember("bbb", "$2y$04$yzTsYmYmgOLVSrOY1eiIm.Zupgpsukn.poLZL6cZqWsl1DK1dX.xi", null),
                setMember("ccc", "$2y$04$yzTsYmYmgOLVSrOY1eiIm.Zupgpsukn.poLZL6cZqWsl1DK1dX.xi", null),
                setMember("ddd", "$2y$04$yzTsYmYmgOLVSrOY1eiIm.Zupgpsukn.poLZL6cZqWsl1DK1dX.xi", null)
        );


        // ----------------스터디-----------------
        List<Study> studies = List.of(
                setStudyData("백엔드1팀", members.get(0), 4, 진행중, 모집완료, 오프라인),
                setStudyData("백엔드2팀", members.get(2), 5, 진행전, 모집중, 온라인),

                setStudyData("백엔드3팀", members.get(4), 5, 진행전, 모집중, 온라인),
                setStudyData("백엔드4팀", members.get(6), 5, 진행전, 모집중, 온라인)
        );


        // ----------------스터디-멤버-----------------
        List<StudyMember> studyMembers = List.of(
                setStudy(members.get(0), studies.get(0)),
                setStudy(members.get(1), studies.get(0)),
                setStudy(members.get(2), studies.get(1)),
                setStudy(members.get(3), studies.get(1)),
                setStudy(members.get(0), studies.get(1)),

                setStudy(members.get(5), studies.get(2)),
                setStudy(members.get(4), studies.get(2)),
                setStudy(members.get(6), studies.get(2)),
                setStudy(members.get(6), studies.get(3))
        );


        // ----------------게시글-----------------
        List<Board> boards = List.of(
                setBoard(members.get(0), studies.get(0), "공부할사람 모집합니다 1", "y",3, 5,"# 1. Content", CONTENT),
                setBoard(members.get(1), studies.get(0), "공부할사람 모집합니다 2", "y",3, 5,"반갑습니다 !!", CONTENT),
                setBoard(members.get(2), studies.get(1), "공부할사람 모집합니다 3", "n",3, 5,"반갑습니다 !!", CONTENT),
                setBoard(members.get(3), studies.get(1), "공부할사람 모집합니다 4", "n",3, 5,"반갑습니다 !!", CONTENT),
                setBoard(members.get(0), studies.get(0), "인프런 김영한 강좌 자바 ORM 표준 JPA 프로그래밍 스터디", "y",5, 10,
                "안녕하세요 오프라인 스터디 인원 구인합니다. 연락 주세요. </p>", RECRUIT),
                setBoard(members.get(4), studies.get(2), "JAVA 기초 스터디", "y",5, 10,
                        "안녕하세요 오프라인 스터디 인원 구인합니다. 연락 주세요. </p>", RECRUIT),
                setBoard(members.get(6), studies.get(3), "스프링 스터디", "y",5, 10,
                        "안녕하세요 오프라인 스터디 인원 구인합니다. 연락 주세요. </p>", RECRUIT)
        );


        // ----------------댓글-----------------
        List<Comment> comments = List.of(
            setComment(members.get(0), boards.get(1), "글 잘읽었습니다 - 김강욱", 6),
            setComment(members.get(1), boards.get(2), "글 잘읽었습니다 - 재원", 6),
            setComment(members.get(2), boards.get(3), "글 잘읽었습니다 - 경림", 6),
            setComment(members.get(3), boards.get(0), "글 잘읽었습니다 - 효진", 6),

            setComment(members.get(0), boards.get(2), "글 못읽었습니다 - 김강욱", 6),
            setComment(members.get(1), boards.get(3), "글 못읽었습니다 - 재원", 6),
            setComment(members.get(2), boards.get(0), "글 못읽었습니다 - 경림", 6),
            setComment(members.get(3), boards.get(1), "글 못읽었습니다 - 효진", 6)
        );

        // ----------------스터디 신청------------------
        List<ApplyForStudy> applyForStudies = List.of(
                new ApplyForStudy(members.get(2), studies.get(0), AuditState.대기),
                new ApplyForStudy(members.get(1), studies.get(0), AuditState.승인),
                new ApplyForStudy(members.get(1), studies.get(1), AuditState.거절),

                new ApplyForStudy(members.get(0), studies.get(3), AuditState.대기),
                new ApplyForStudy(members.get(1), studies.get(3), AuditState.대기),
                new ApplyForStudy(members.get(2), studies.get(3), AuditState.대기),
                new ApplyForStudy(members.get(3), studies.get(3), AuditState.대기),
                new ApplyForStudy(members.get(4), studies.get(3), AuditState.대기),
                new ApplyForStudy(members.get(5), studies.get(3), AuditState.대기),
                new ApplyForStudy(members.get(7), studies.get(3), AuditState.대기)
        );

        List<MessageForApplication> messageForApplications = List.of(
                new MessageForApplication(applyForStudies.get(0), "스터디 참여를 희망합니다."),
                new MessageForApplication(applyForStudies.get(1), "스터디 가입시켜 주세요."),
                new MessageForApplication(applyForStudies.get(2), "열심히 하겠습니다."),

                new MessageForApplication(applyForStudies.get(3), "열심히 하겠습니다."),
                new MessageForApplication(applyForStudies.get(4), "열심히 하겠습니다."),
                new MessageForApplication(applyForStudies.get(5), "열심히 하겠습니다."),
                new MessageForApplication(applyForStudies.get(6), "열심히 하겠습니다."),
                new MessageForApplication(applyForStudies.get(7), "열심히 하겠습니다."),
                new MessageForApplication(applyForStudies.get(8), "열심히 하겠습니다."),
                new MessageForApplication(applyForStudies.get(9), "열심히 하겠습니다.")
        );

        // ----------------DB 저장-----------------
        memberRepository.saveAll(members);
        studyRepository.saveAll(studies);
        studyMemberRepository.saveAll(studyMembers);
        boardRepository.saveAll(boards);
        commentRepository.saveAll(comments);
        applyForStudyRepository.saveAll(applyForStudies);
        messageForApplicationRepository.saveAll(messageForApplications);

        platformTransactionManager.commit(ts);
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
        studyMember.setAttendState(AttendState.참여);

        return studyMember;
    }

    private static Board setBoard(Member member1, Study study1, String title,String pulicYn, int likeCnt, int viewCnt, String content, Kind kind) {
        Board board = new Board();
        board.setMember(member1);
        board.setStudy(study1);
        board.setPublicYn(pulicYn);
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
