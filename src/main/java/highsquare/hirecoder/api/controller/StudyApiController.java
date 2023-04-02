package highsquare.hirecoder.api.controller;

import highsquare.hirecoder.dto.MyStudyResponse;
import highsquare.hirecoder.api.service.StudyApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
public class StudyApiController {

    private final StudyApiService studyApiService;

    @GetMapping("/api/myStudy")
    public ResponseEntity<List<MyStudyResponse>> getMyStudies(Principal principal) {

        long memberId = Long.parseLong(principal.getName());
        return ResponseEntity.ok(studyApiService.getMyStudies(memberId));
    }
}
