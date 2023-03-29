package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.service.ApplyForStudyService;
import highsquare.hirecoder.domain.service.MessageForApplicationService;
import highsquare.hirecoder.domain.service.StudyMemberService;
import highsquare.hirecoder.domain.service.StudyService;
import highsquare.hirecoder.entity.ApplyForStudy;
import highsquare.hirecoder.entity.AttendState;
import highsquare.hirecoder.entity.AuditState;
import highsquare.hirecoder.utils.ScriptUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.security.Principal;

import static highsquare.hirecoder.constant.MessageConstant.MAX_APPEALMESSAGE_LENGTH;
import static highsquare.hirecoder.constant.MessageConstant.MIN_APPEALMESSAGE_LENGTH;
import static highsquare.hirecoder.constant.StudyCountConstant.STUDY_COUNT_MAX;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StudyManageController {
    private final StudyMemberService studyMemberService;

    private final ApplyForStudyService applyForStudyService;
    private final StudyService studyService;
    private final MessageForApplicationService messageForApplicationService;
//
//    @GetMapping("studyManage/manager/{studyId}")
//    public String getManagerMainPage()

    /**
     * 스터디장이 자신의 스터디 페이지에서 신청 테이블을 읽어옴
     * 페이징 처리 필요
     */

    /**
     * 일반 멤버가 myPage에서 자신이 신청한 신청 테이블을 읽어옴
     * 페이징 처리 필요
     */

    /**
     * 스터디장이 신청 테이블의 목록에서 승인하는 로직
     * memberId, studyId는 applyForStudyId에 해당하는 신청 테이블의 컬럼 값임(스터디장의 memberId값이 아님)
     * 우선 ScriptUtils를 이용하여 간단하게 로직 작성함(map에 오류를 넣어서 페이지로 반환시키든 리펙토링 필요)
     */
    @PatchMapping("/studyManage/manager/{studyId}/approval/{memberId}/{applyForStudyId}")
    public void approval(@PathVariable("studyId") Long studyId,
                           @PathVariable("memberId") Long memberId,
                           @PathVariable("applyForStudyId") Long applyForStudyId,
                           HttpServletResponse response) throws IOException {

        // 해당 스터디 존재여부
        if (studyService.isExistingStudy(studyId)) {
            if (applyForStudyService.isValidApplication(applyForStudyId)) {

                // 신청 테이블의 AuditState를 '승인'으로 바꾸는 로직
                applyForStudyService.approval(applyForStudyId);
                // 해당 studyId와 memberId를 가지고 StudyMember에 '참여'로 insert 추가
                studyMemberService.saveStudyMember(studyId, memberId);

            } else {
                ScriptUtils.alert(response,"유효한 신청이 아닙니다.");
            }
        } else {
            ScriptUtils.alert(response,"해당 스터디가 존재하지 않습니다.");
        }
    }

    @GetMapping("/studyManage/manager/{studyId}/reject/{applyForStudyId}")
    @ResponseBody
    public ResponseEntity<?> isRejectable(@PathVariable("applyForStudyId") Long applyForStudyId) {

        // 신청테이블에 존재하는지, AuditState가 '대기'인지 확인하기
        if(!applyForStudyService.isValidApplication(applyForStudyId)) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok().build();
        }
    }

    /**
     * 스터디장이 신청 테이블의 목록에서 거절하는 로직
     * 스터디장이 거절했을 시 사유 테이블에 거절 메시지 작성
     */
    @PostMapping("/studyManage/manager/{studyId}/reject/{applyForStudyId}")
    public void reject(@PathVariable("applyForStudyId") Long applyForStudyId,
                       @NotBlank @Range(min = 10, max = 500) @ModelAttribute("rejectReason")
                       String rejectReason,
                       BindingResult bindingResult,
                       HttpServletResponse response) throws IOException {

        // 신청테이블에 존재하는지, AuditState가 '대기'인지 확인하기
        if(!applyForStudyService.isValidApplication(applyForStudyId)) {
            ScriptUtils.alert(response,"유효한 신청이 아닙니다.");
            return;
        }

        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.error(error.toString());
            }
            return;
        }

        // <---- 검증 종료
        // 신청 테이블의 AuditState를 '거절'로 바꾸는 로직
        applyForStudyService.reject(applyForStudyId, rejectReason);
    }

    /**
     * 스터디장의 스터디 현황 페이지에서 현재 속해있는 스터디의 구성원을 강퇴시키는 기능
     */
    @PatchMapping("/studyManage/manager/{studyId}/kickout/{studyMemberId}")
    public void kickOut(@PathVariable("studyId") Long studyId,
                          @PathVariable("studyMemberId") Long studyMemberId,
                          HttpServletResponse response,
                          Principal principal) throws IOException {

        Long loginMemberId
                = Long.parseLong(principal.getName());

        boolean canKickOut = true;

        // 해당 스터디 존재 여부
        // 해당 스터디의 스터디 팀장인지 확인하는 로직
        if (!studyService.isExistingStudy(studyId)) {
            ScriptUtils.alert(response,"해당 스터디가 존재하지 않습니다.");
            canKickOut = false;
        }

        if(studyService.getStudyManagerId(studyId)!=loginMemberId
        ) {
            ScriptUtils.alert(response,"해당 스터디의 팀장이 아닙니다.");
            canKickOut = false;
        }

        // 해당 studyMember가 존재하는지 확인하기
        // 해당 studyMember의 AttendState가 '참여'인지 확인하기
        if (!studyMemberService.checkMemberInStudy(studyMemberId)) {
            ScriptUtils.alert(response,"해당 스터디에 참여하고 있지 않습니다.");
            canKickOut = false;
        }

        // <---- 검증 종료

        // 해당 studyMember의 AttendState를 '강퇴'로 바꾸는 로직
        if (canKickOut)
            studyMemberService.changeAttendState(studyMemberId, AttendState.강퇴);
    }

}
