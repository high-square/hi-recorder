package highsquare.hirecoder.domain.repository.custom;

public interface StudyMemberRepositoryCustom {

    public boolean existsMemberAndStudy(Long study_id, Long member_id);

}
