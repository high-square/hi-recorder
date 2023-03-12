package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.repository.ApplyForStudyRepository;
import highsquare.hirecoder.domain.repository.StudyMemberRepository;
import highsquare.hirecoder.entity.AuditState;
import highsquare.hirecoder.utils.ScriptUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static highsquare.hirecoder.constant.StudyCountConstant.STUDY_COUNT_MAX;

@Controller
@RequiredArgsConstructor
public class StudyManageController {

    private final StudyMemberRepository studyMemberRepository;

    private final ApplyForStudyRepository applyForStudyRepository;

    @GetMapping("/studyManage/enroll/check/{studyId}/{memberId}")
    public String checkStudyMember(@PathVariable("studyId") Long studyId,
                                   @PathVariable("memberId") Long memberId,
                                   Model model,
                                   HttpServletResponse response) throws IOException {

        // 로그인한 회원인지 검증로직

        // 해당 스터디가 존재하는지 확인

        // 스터디 멤버 테이블에서 studyId와 memberId를 검색조건으로 AttendState 상태 들고 올텐데
        // 레코드가 존재하면 AttendState 상태에 따라 메시지를 보냄
        // 존재하지 않을 시 신청 테이블을 생성함

        if (studyMemberRepository.existsMemberAndStudy(studyId,memberId)) {
            String auditState = studyMemberRepository.findAuditState(studyId, memberId);

            if (auditState.equals("참여")) {
                ScriptUtils.alert(response,"이미 가입하신 스터디입니다.");
            } else if (auditState.equals("탈퇴")) {
                ScriptUtils.alert(response,"탈퇴한 스터디는 가입이 불가능합니다.");
            } else if (auditState.equals("강퇴")) {
                ScriptUtils.alert(response,"강퇴당한 스터디는 가입이 불가능합니다.");
            }
            return null;
        } else {
            // 해당 멤버의 스터디 갯수 제한 검증 로직
            int studyCount = studyMemberRepository.checkMembersStudyCount(memberId);

            if(studyCount>=STUDY_COUNT_MAX) {
                ScriptUtils.alert(response,"가입할 수 있는 스터디 최대 갯수를 초과하셨습니다.");
            } else {
                enrollApplyForStudy(studyId, memberId);
                return "redirect/";
            }

            return null;
        }

    }


    @Transactional
    public void enrollApplyForStudy(Long studyId, Long memberId) {
        applyForStudyRepository.enrollApplyForStudy(studyId,memberId, AuditState.대기.name());

    }


}
