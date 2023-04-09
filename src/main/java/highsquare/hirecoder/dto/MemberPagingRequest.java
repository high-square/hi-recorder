package highsquare.hirecoder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

import static highsquare.hirecoder.constant.PageConstant.DEFAULT_PAGE;
import static highsquare.hirecoder.constant.PageConstant.DEFAULT_SIZE;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberPagingRequest {
    @NotNull
    private int page = DEFAULT_PAGE;
    @NotNull
    private int size = DEFAULT_SIZE;
    private int isAsc = 1;
    private MemberSort sort = MemberSort.ID;

}
