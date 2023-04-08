package highsquare.hirecoder.api.controller;

import highsquare.hirecoder.domain.service.StudyService;
import highsquare.hirecoder.dto.MyStudyResponse;
import highsquare.hirecoder.api.service.StudyApiService;
import highsquare.hirecoder.entity.RecruitState;
import highsquare.hirecoder.utils.ScriptUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class StudyApiController {

    private final StudyApiService studyApiService;

    private final StudyService studyService;

    @GetMapping("/api/myStudy")
    public ResponseEntity<List<MyStudyResponse>> getMyStudies(Principal principal) {

        long memberId = Long.parseLong(principal.getName());
        return ResponseEntity.ok(studyApiService.getMyStudies(memberId));
    }

    @PatchMapping("/api/study/recruitOff")
    public ResponseEntity<RecruitState> recruitOff(@RequestBody HashMap<String, Long> map, Principal principal, HttpServletResponse response) throws IOException {

        Long studyId =  map.get("studyId");

        Long loginMemberId = Long.parseLong(principal.getName());

        if (studyService.getStudyManagerId(studyId)!=loginMemberId) {
            ScriptUtils.alert(response,"해당 스터디의 매니저가 아닙니다");
        }

        studyService.modifyRecruitOff(studyId);
        return ResponseEntity.ok(RecruitState.모집완료);
    }

    @PatchMapping("/api/study/recruitOn")
    public ResponseEntity<RecruitState> recruitOn(@RequestBody HashMap<String, Long> map, Principal principal, HttpServletResponse response) throws IOException {

        Long studyId = map.get("studyId");

        Long loginMemberId = Long.parseLong(principal.getName());

        if (studyService.getStudyManagerId(studyId)!=loginMemberId) {
            ScriptUtils.alert(response,"해당 스터디의 매니저가 아닙니다");
        }

        studyService.modifyRecruitOn(studyId);
        return ResponseEntity.ok(RecruitState.모집중);
    }


}
