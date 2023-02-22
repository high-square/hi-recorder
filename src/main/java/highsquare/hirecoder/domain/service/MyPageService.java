package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.MemberRepository;
import highsquare.hirecoder.domain.repository.StudyMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final StudyMemberRepository studyMemberRepository;

    /**
     * studyMemberId -> study List 조회
     * entity 수정?
     */


    /**
     * 특정 study로 들어가면
     * 거기서 내가 쓴 글 조회
     * studyMember.study.id == board.study.id
     */

    /**
     * 스터디 탈퇴
     */
}
