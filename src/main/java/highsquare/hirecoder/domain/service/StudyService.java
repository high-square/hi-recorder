package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.StudyRepository;
import highsquare.hirecoder.entity.Study;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StudyService {
    private final StudyRepository studyRepository;



}

