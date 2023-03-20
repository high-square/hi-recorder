package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.repository.ApplyForStudyRepository;
import highsquare.hirecoder.domain.repository.MessageForApplicationRepository;
import highsquare.hirecoder.domain.service.MessageForApplicationService;
import highsquare.hirecoder.entity.MessageForApplication;
import highsquare.hirecoder.utils.ScriptUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MessageForApplicationController {

    private final MessageForApplicationService messageService;

    private final MessageForApplicationRepository messageRepository;

    private final ApplyForStudyRepository applyForStudyRepository;


    /**
     * 해당 신청 테이블에 매핑된 사유 테이블(MessageForApplication) 신청사유,거절사유 읽어오기
     */
    @GetMapping("/applyStatus/Message/{applyForStudyId}")
    @ResponseBody
    public void getMessage(@PathVariable("applyForStudyId") Long applyForStudyId,
                           Principal principal,
                           HttpServletResponse response) throws IOException {

        Long loginMemberId = Long.parseLong(principal.getName());

        // 신청자 본인 or 신청한 스터디의 팀장인지 확인하는 로직
        if (!messageService.checkEligibility(applyForStudyId,loginMemberId)) {
            ScriptUtils.alert(response,"다른 회원의 신청 메시지를 보실 수 없습니다.");
        }

        // 신청사유 읽어오기
        MessageForApplication findMessage = messageRepository.findById(applyForStudyId).get();
        String appealMessage = findMessage.getAppealMessage();
        String rejectMessage = findMessage.getRejectMessage();

        // 화면단에 데이터 전달하기
    }





    /**
     * 해당 스터디에 신청할 때 신청 사유 작성하기(ApplyForStudy가 만들어질 때 만들기)
     */

    /**
     * 스터디장이 신청에 대해 거절할 때 거절 사유 작성하기
     */

    /**
     * 신청 사유 수정하기
     */

    /**
     * 거절 사유 수정하기
     */







}

