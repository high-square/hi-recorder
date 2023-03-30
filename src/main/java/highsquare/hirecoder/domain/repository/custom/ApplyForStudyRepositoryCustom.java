package highsquare.hirecoder.domain.repository.custom;

import highsquare.hirecoder.dto.ApplyInfo;
import highsquare.hirecoder.entity.ApplyForStudy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ApplyForStudyRepositoryCustom {
    Optional<ApplyForStudy> findByStudyIdAndMemberId(Long studyId, Long memberId);

    Page<ApplyInfo> searchApplyInfo(Long studyId, Pageable page);
}
