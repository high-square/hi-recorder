package highsquare.hirecoder.api.service;

import highsquare.hirecoder.domain.repository.StudyRepository;
import highsquare.hirecoder.dto.MyStudyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyApiService {

    private final StudyRepository studyRepository;

    public List<MyStudyResponse> getMyStudies(Long memberId) {
        return studyRepository.findAllByManager_Id(memberId)
                .stream()
                .map((study)->new MyStudyResponse(study.getId(), study.getName()))
                .collect(Collectors.toList());
    }
}
