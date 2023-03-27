package highsquare.hirecoder.domain.repository.custom;

import highsquare.hirecoder.entity.MessageForApplication;

public interface MessageForApplicationRepositoryCustom {

    public MessageForApplication findByApplyForStudyIdWithApplyForStudy(Long applyForStudyId);
}
