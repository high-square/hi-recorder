package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.repository.StudyMemberRepository;
import highsquare.hirecoder.domain.service.ApplyForStudyService;
import highsquare.hirecoder.domain.service.MessageForApplicationService;
import highsquare.hirecoder.domain.service.StudyMemberService;
import highsquare.hirecoder.domain.service.StudyService;
import highsquare.hirecoder.entity.AttendState;
import highsquare.hirecoder.entity.AuditState;
import highsquare.hirecoder.utils.ScriptUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;

import static highsquare.hirecoder.constant.MessageConstant.MAX_APPEALMESSAGE_LENGTH;
import static highsquare.hirecoder.constant.MessageConstant.MIN_APPEALMESSAGE_LENGTH;
import static highsquare.hirecoder.constant.StudyCountConstant.STUDY_COUNT_MAX;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StudyManageController {

    private final StudyMemberRepository studyMemberRepository;

    private final StudyMemberService studyMemberService;


    private final ApplyForStudyService applyForStudyService;

    private final StudyService studyService;

    private final MessageForApplicationService messageForApplicationService;


    /**
     * 일반 멤버가 스터디 신청하기 버튼 클릭시 신청테이블에 등록되는 로직
     * 스터디에 신청할 때 사유테이블에 신청사유를 작성
     * 우선 ScriptUtils를 이용하여 간단하게 로직 작성함(map에 오류를 넣어서 페이지로 반환시키든 리펙토링 필요)
     */
    @GetMapping("/studyManage/check/{studyId}")
    public String checkStudyMember(@PathVariable("studyId") Long studyId,
                                   Principal principal,
                                   HttpServletResponse response,
                                   Model model) throws IOException {

        Long loginMemberId = Long.parseLong(principal.getName());

        // 해당 스터디와 멤버가 존재하는지 확인(로그인 검증에서 멤버 존재 확인)
        if (!studyService.isExistingStudy(studyId)) {
            ScriptUtils.alert(response, " 해당 스터디가 존재하지 않습니다.");
        }

        // 스터디 멤버 테이블에서 studyId와 memberId를 검색조건으로 AttendState 상태 들고 올텐데
        // 레코드가 존재하면 AttendState 상태에 따라 메시지를 보냄
        // 존재하지 않을 시 신청 테이블을 생성함
        if (studyMemberRepository.existsMemberAndStudy(studyId,loginMemberId)) {
            String auditState = studyMemberRepository.findAuditState(studyId, loginMemberId);

            if (auditState.equals("참여")) {
                ScriptUtils.alert(response,"이미 가입하신 스터디입니다.");
            } else if (auditState.equals("탈퇴")) {
                ScriptUtils.alert(response,"탈퇴한 스터디는 가입이 불가능합니다.");
            } else if (auditState.equals("강퇴")) {
                ScriptUtils.alert(response,"강퇴당한 스터디는 가입이 불가능합니다.");
            }
        } else {
            // 해당 멤버의 스터디 갯수 제한 검증 로직
            int studyCount = studyMemberRepository.checkMembersStudyCount(loginMemberId);

            if(studyCount>=STUDY_COUNT_MAX) {
                ScriptUtils.alert(response,"가입할 수 있는 스터디 최대 갯수를 초과하셨습니다.");
            }

        }

        model.addAttribute("studyId", studyId);

        String studyName = studyService.getStudyNameById(studyId);
        model.addAttribute("studyName", studyName);
        return "form/messageForApply";
    }

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
    @PatchMapping("/studyManage/manager/approval/{studyId}/{memberId}/{applyForStudyId}")
    public void approval(@PathVariable("studyId") Long studyId,
                           @PathVariable("memberId") Long memberId,
                           Principal principal,
                           @PathVariable("applyForStudyId") Long applyForStudyId,
                           HttpServletResponse response,
                           HttpSession session,
                           Model model) throws IOException {

        Long loginMemberId
                = Long.parseLong(principal.getName());

        // 해당 스터디 존재여부
        // 해당 스터디의 스터디 팀장인지 확인하는 로직
        if (!studyService.isExistingStudy(studyId)) {
            ScriptUtils.alert(response,"해당 스터디가 존재하지 않습니다.");
        }

        if(studyService.getStudyManagerId(studyId)!=loginMemberId) {
            ScriptUtils.alert(response,"해당 스터디의 팀장이 아닙니다.");
        }

        // 신청테이블에 존재하는지, AuditState가 '대기'인지 확인하기
        if(!applyForStudyService.isValidApplication(applyForStudyId)) {
            ScriptUtils.alert(response,"유효한 신청이 아닙니다.");
        }

        // <---- 검증 종료

        // 신청 테이블의 AuditState를 '승인'으로 바꾸는 로직
        applyForStudyService.approval(applyForStudyId);

        // 해당 studyId와 memberId를 가지고 StudyMember에 '참여'로 insert 추가
        studyMemberService.saveStudyMember(studyId, memberId);


    }

    /**
     * 스터디장이 신청 테이블의 목록에서 거절하는 로직
     * 스터디장이 거절했을 시 사유 테이블에 거절 메시지 작성
     */
    @PatchMapping("/studyManage/manager/reject/{studyId}/{applyForStudyId}")
    public void reject(@PathVariable("studyId") Long studyId,
                           @PathVariable("applyForStudyId") Long applyForStudyId,
                           HttpServletResponse response,
                           Principal principal,
                           Model model) throws IOException {

        Long loginMemberId
                = Long.parseLong(principal.getName());

        // 해당 스터디 존재 여부
        // 해당 스터디의 스터디 팀장인지 확인하는 로직
        if (!studyService.isExistingStudy(studyId)) {
            ScriptUtils.alert(response,"해당 스터디가 존재하지 않습니다.");
        }

        if(studyService.getStudyManagerId(studyId)!=loginMemberId
        ) {
            ScriptUtils.alert(response,"해당 스터디의 팀장이 아닙니다.");
        }

        // 신청테이블에 존재하는지, AuditState가 '대기'인지 확인하기
        if(!applyForStudyService.isValidApplication(applyForStudyId)) {
            ScriptUtils.alert(response,"유효한 신청이 아닙니다.");
        }


        // <---- 검증 종료
        // 신청 테이블의 AuditState를 '거절'로 바꾸는 로직
        applyForStudyService.reject(applyForStudyId);



    }

    /**
     * 스터디장의 스터디 현황 페이지에서 현재 속해있는 스터디의 구성원을 강퇴시키는 기능
     */
    @PatchMapping("/studyManage/manager/kickout/{studyId}/{studyMemberId}")
    public void kickOut(@PathVariable("studyId") Long studyId,
                          @PathVariable("studyMemberId") Long studyMemberId,
                          HttpServletResponse response,
                          Principal principal) throws IOException {

        Long loginMemberId
                = Long.parseLong(principal.getName());

        // 해당 스터디 존재 여부
        // 해당 스터디의 스터디 팀장인지 확인하는 로직
        if (!studyService.isExistingStudy(studyId)) {
            ScriptUtils.alert(response,"해당 스터디가 존재하지 않습니다.");
        }

        if(studyService.getStudyManagerId(studyId)!=loginMemberId
        ) {
            ScriptUtils.alert(response,"해당 스터디의 팀장이 아닙니다.");
        }

        // 해당 studyMember가 존재하는지 확인하기
        // 해당 studyMember의 AttendState가 '참여'인지 확인하기
        if (!studyMemberService.checkMemberInStudy(studyMemberId)) {
            ScriptUtils.alert(response,"해당 스터디에 참여하고 있지 않습니다.");
        }

        // <---- 검증 종료

        // 해당 studyMember의 AttendState를 '강퇴'로 바꾸는 로직
        studyMemberService.changeAttendState(studyMemberId, AttendState.강퇴);

    }





    /**
     * 신청 테이블에 대기상태로 저장됨
     */
    @PostMapping("/studyManage/enroll")
    public String enrollApplyForStudy(@RequestParam("appealMessage") String appealMessage,
                                    @RequestParam("studyId") Long studyId, Principal principal,
                                    HttpServletResponse response,
                                    Model model) throws IOException {

        Long loginMemberId = Long.parseLong(principal.getName());
        log.debug("스터디 id = {}",studyId);
        // 해당 스터디와 멤버가 존재하는지 확인(로그인 검증에서 멤버 존재 확인)
        if (!studyService.isExistingStudy(studyId)) {
            ScriptUtils.alert(response, " 해당 스터디가 존재하지 않습니다.");
        }

        // 스터디 멤버 테이블에서 studyId와 memberId를 검색조건으로 AttendState 상태 들고 올텐데
        // 레코드가 존재하면 AttendState 상태에 따라 메시지를 보냄
        // 존재하지 않을 시 신청 테이블을 생성함
        if (studyMemberRepository.existsMemberAndStudy(studyId,loginMemberId)) {
            String auditState = studyMemberRepository.findAuditState(studyId, loginMemberId);

            if (auditState.equals("참여")) {
                ScriptUtils.alert(response,"이미 가입하신 스터디입니다.");
            } else if (auditState.equals("탈퇴")) {
                ScriptUtils.alert(response,"탈퇴한 스터디는 가입이 불가능합니다.");
            } else if (auditState.equals("강퇴")) {
                ScriptUtils.alert(response,"강퇴당한 스터디는 가입이 불가능합니다.");
            }
        } else {
            // 해당 멤버의 스터디 갯수 제한 검증 로직
            int studyCount = studyMemberRepository.checkMembersStudyCount(loginMemberId);

            if(studyCount>=STUDY_COUNT_MAX) {
                ScriptUtils.alert(response,"가입할 수 있는 스터디 최대 갯수를 초과하셨습니다.");
            } else {
                // appealMessage 검증로직
                if (!validMessage(appealMessage,model)) {
                    model.addAttribute("studyId", studyId);

                    String studyName = studyService.getStudyNameById(studyId);
                    model.addAttribute("studyName", studyName);
                    return "form/messageForApply";
                }
                Long applyForStudyId = applyForStudyService.enrollApplyForStudy(studyId, loginMemberId, AuditState.대기.name());
                messageForApplicationService.addMessage(applyForStudyId, appealMessage);
            }

        }

        return "redirect:/study/myStudy";
    }

    private boolean validMessage(String appealMessage,Model model) {
        if (appealMessage.length()< MIN_APPEALMESSAGE_LENGTH) {
            model.addAttribute("invalidMessageLength", true);
            return false;
        }

        if (appealMessage.length()> MAX_APPEALMESSAGE_LENGTH) {
            model.addAttribute("invalidMessageLength", true);
            model.addAttribute("rejectedMessage", appealMessage.substring(0,MAX_APPEALMESSAGE_LENGTH-1));
            return false;
        }
        return true;
    }


}
