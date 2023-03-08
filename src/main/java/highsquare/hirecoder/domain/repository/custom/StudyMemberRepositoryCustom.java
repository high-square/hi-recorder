package highsquare.hirecoder.domain.repository.custom;

import highsquare.hirecoder.entity.StudyMember;

import java.util.List;

public interface StudyMemberRepositoryCustom {

    public boolean existsMemberAndStudy(Long study_id, Long member_id);

    public List<StudyMember> getAllMembersStudy(Long member_id);

}
