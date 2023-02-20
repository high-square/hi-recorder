package highsquare.hirecoder.domain.repository.custom;

public interface StudyMemberRepositoryCustom {

    public boolean doesMemberBelongToStudy(Long study_id, Long member_id);

}
