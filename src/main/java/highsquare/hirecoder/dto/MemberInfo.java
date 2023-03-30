package highsquare.hirecoder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberInfo {
    private Long id;
    private String name;
    private Long totalWrite;
    private Long totalComment;
    private String attendState;
}
