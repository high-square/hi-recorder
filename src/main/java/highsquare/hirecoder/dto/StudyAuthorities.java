package highsquare.hirecoder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyAuthorities {

    /**
     * 매니저 권한을 가진 studyId의 리스트
     */
    private List<Long> managers = new ArrayList<>();

    /**
     * 해당 멤버가 속한 studyId의 리스트
     */
    private List<Long> studies = new ArrayList<>();

}
