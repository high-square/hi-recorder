package highsquare.hirecoder.domain.repository.custom;

import highsquare.hirecoder.dto.MemberInfo;
import highsquare.hirecoder.entity.StudyMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudyMemberRepositoryCustom {

    public boolean existsMemberAndStudy(Long study_id, Long member_id);

    public List<StudyMember> getAllMembersStudy(Long member_id);

    public Page<MemberInfo> searchStudyMemberInfo(Long studyId, Pageable pageable);

}
