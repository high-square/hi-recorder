package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.service.*;
import highsquare.hirecoder.dto.ApplyInfo;
import highsquare.hirecoder.dto.ApplyPagingRequest;
import highsquare.hirecoder.dto.MemberInfo;
import highsquare.hirecoder.dto.MemberPagingRequest;
import highsquare.hirecoder.entity.AttendState;
import highsquare.hirecoder.entity.Study;
import highsquare.hirecoder.page.PageResultDto;
import highsquare.hirecoder.utils.ScriptUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static highsquare.hirecoder.entity.Kind.CONTENT;

@Controller
@RequiredArgsConstructor
@RequestMapping("/studyManage/manager/{studyId}")
@Slf4j
public class StudyManageController {
    private final DashBoardService dashBoardService;
    private final StudyMemberService studyMemberService;
    private final ApplyForStudyService applyForStudyService;
    private final StudyService studyService;
    private final TagService tagService;

    // TODO: 2023-03-29 스터디 매니저 메인 페이지 작성 필요
    @GetMapping
    public String getManagerMainPage(@PathVariable("studyId") Long studyId,
                                     Model model) {
        // 해당 스터디의 게시글 수
        Long boardCount = dashBoardService.getCountBoard(studyId, CONTENT);
        // 해당 스터디 글의 전체 조회수
        Long countViewCnt = dashBoardService.getViewCntBoard(studyId, CONTENT);
        // 해당 스터디의 스터디원 수
        // 여기서 Study를 불러와서 변수에 다 저장하는 게 좋을까??
        // 이 과정을 컨트롤러에서 해도 되는지 모르겠음
        Study myManageStudy = studyService.getStudyAll(studyId);
        int totalMember = myManageStudy.getCrewNumber().intValue();
        int currentMember = studyMemberService.getCountCurrentMember(studyId);
        String studyName = myManageStudy.getName();

        // tag 사용 수
        List<Map<String, Object>> tagList = tagService.getTagList(CONTENT);
        // tagList 들고 와서 개수 반환
        System.out.println(tagList);

        model.addAttribute("boardCount", boardCount);
        model.addAttribute("countViewCnt", countViewCnt);
        model.addAttribute("studyName", studyName);
        model.addAttribute("tagList", tagList);
        model.addAttribute("currentMember", currentMember);
        model.addAttribute("totalMember", totalMember);

        return "admin/adminMain";
    }

    @GetMapping("/memberList")
    public String getStudyMemberListPage(@ModelAttribute MemberPagingRequest memberPagingRequest,
                                                                    @PathVariable("studyId") Long studyId,
                                                                     Model model) {

        Sort sort = Sort.by(memberPagingRequest.getSort().toString());

        Pageable pageable = PageRequest.of(memberPagingRequest.getPage() - 1, memberPagingRequest.getSize(),
                                    memberPagingRequest.getIsAsc() == 1 ? sort.ascending() : sort.descending());

        PageResultDto<MemberInfo, ?> pageResultDto
                = studyMemberService.manageStudyMember(studyId, pageable);

        model.addAttribute("memberInfo", pageResultDto);

        return "admin/admin-member";
    }

    @GetMapping("/applyList")
    public String getStudyApplyListPage(@ModelAttribute ApplyPagingRequest applyPagingRequest,
                                        @PathVariable("studyId") Long studyId,
                                        Model model) {

        Sort sort = Sort.by(applyPagingRequest.getSort().toString());

        Pageable pageable = PageRequest.of(applyPagingRequest.getPage() - 1, applyPagingRequest.getSize(),
                applyPagingRequest.getIsAsc() == 1 ? sort.ascending() : sort.descending());

        PageResultDto<ApplyInfo, ?> applyInfoPageResultDto
                = applyForStudyService.ManageApplyForStudy(studyId, pageable);

        model.addAttribute("applyInfo", applyInfoPageResultDto);

        return "admin/admin-apply";
    }

    /**
     * 스터디장이 신청 테이블의 목록에서 승인하는 로직
     * memberId, studyId는 applyForStudyId에 해당하는 신청 테이블의 컬럼 값임(스터디장의 memberId값이 아님)
     * 우선 ScriptUtils를 이용하여 간단하게 로직 작성함(map에 오류를 넣어서 페이지로 반환시키든 리펙토링 필요)
     */
    @GetMapping("/approval/{memberId}/{applyForStudyId}")
    public String approval(@PathVariable("studyId") Long studyId,
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

        return "redirect:studyManage/manager/{studyId}/applyList";
    }

    @GetMapping("/reject/{applyForStudyId}")
    @ResponseBody
    public ResponseEntity<?> isRejectable(@PathVariable("applyForStudyId") Long applyForStudyId) {

        // 신청테이블에 존재하는지, AuditState가 '대기'인지 확인하기
        if(!applyForStudyService.isValidApplication(applyForStudyId)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok().build();
        }
    }

    /**
     * 스터디장이 신청 테이블의 목록에서 거절하는 로직
     * 스터디장이 거절했을 시 사유 테이블에 거절 메시지 작성
     */
    @PostMapping("/reject/{applyForStudyId}")
    @ResponseBody
    public ResponseEntity<?> reject(@PathVariable("applyForStudyId") Long applyForStudyId,
                         @RequestParam String rejectReason,
                         HttpServletResponse response) throws IOException {

        Map<String, Object> params = new HashMap<>();
        // 신청테이블에 존재하는지, AuditState가 '대기'인지 확인하기
        if(!applyForStudyService.isValidApplication(applyForStudyId)) {
            params.put("error", "유효한 신청이 아닙니다.");
        } else if (rejectReason.length() < 10 || rejectReason.length() > 250) {
            params.put("error", "거절 사유는 10 ~ 250자 사이여야 합니다.");
        }

        if (params.containsKey("error")) {
            log.error("{}", params.get("error"));
            return ResponseEntity.badRequest().body(params);
        }

        applyForStudyService.reject(applyForStudyId, rejectReason);

        return ResponseEntity.status(HttpStatus.FOUND).build();
    }

    /**
     * 스터디장의 스터디 현황 페이지에서 현재 속해있는 스터디의 구성원을 강퇴시키는 기능
     */
    @PostMapping("/kickout/{memberId}")
    @ResponseBody
    public ResponseEntity<?> kickOut(@PathVariable("studyId") Long studyId,
                          @PathVariable("memberId") Long memberId) {

        boolean canKickOut = true;

        // 해당 스터디 존재 여부
        // 해당 스터디의 스터디 팀장인지 확인하는 로직
        if (studyService.getStudyManagerId(studyId) == memberId) {
            log.error("매니저는 강퇴가 불가능합니다.");
            canKickOut = false;
        }

        // 해당 studyMember가 존재하는지 확인하기
        // 해당 studyMember의 AttendState가 '참여'인지 확인하기
        if (!studyMemberService.doesMemberBelongToStudy(studyId, memberId)) {
            log.error("스터디의 멤버가 아닙니다.");
            canKickOut = false;
        }

        // <---- 검증 종료

        // 해당 studyMember의 AttendState를 '강퇴'로 바꾸는 로직
        if (canKickOut) {
            studyMemberService.changeAttendState(studyId, memberId, AttendState.강퇴);
            return ResponseEntity.status(HttpStatus.FOUND).build();
        } else {
            return ResponseEntity.badRequest().body("강퇴가 불가능합니다.");
        }
    }

}
