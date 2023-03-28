package highsquare.hirecoder.domain.repository.custom;

import highsquare.hirecoder.entity.ApplyForStudy;

import java.util.Optional;

public interface ApplyForStudyRepositoryCustom {
    Optional<ApplyForStudy> findByStudyIdAndMemberId(Long studyId, Long memberId);
}
