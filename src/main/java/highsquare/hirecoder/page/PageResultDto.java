package highsquare.hirecoder.page;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static highsquare.hirecoder.constant.PageConstant.DEFAULT_PAGE_SIZE;

@Data
@Slf4j
public class PageResultDto<DTO, EN> {
    public List<DTO> dtoList; //DTO 리스트

    private int totalPages; //총 페이지 번호

    private int page; //현재 페이지 번호

    private int size; //페이지 당 사이즈

    private int start, end; //시작, 끝 페이지

    boolean first, last; //첫 페이지인지, 마지막 페이지인지 체크

    private boolean prev,next; //이전 페이지, 다음 페이지 있는지 체크

    private List<Integer> pageList;

    public boolean isNotEmpty() {
        return !this.dtoList.isEmpty();
    }


    public PageResultDto(Page<EN> result, Function<EN,DTO> fn) {
        dtoList = result.stream().map(fn).collect(Collectors.toList());
        log.info("dtoList type={}", dtoList.getClass());
        totalPages = result.getTotalPages();
        makePageList(result.getPageable());
    }

    private void makePageList(Pageable pageable){

        this.page = pageable.getPageNumber() + 1; //0부터 시작하므로 1을 추가
        this.size = pageable.getPageSize();

        int tempEnd = (int)(Math.ceil(page/(float)DEFAULT_PAGE_SIZE)) * DEFAULT_PAGE_SIZE;

        start = tempEnd - 9;

        prev = start > 1;

        first =(page+1<=size+1);

        last =(Math.floor(totalPages/DEFAULT_PAGE_SIZE)*DEFAULT_PAGE_SIZE<=start);

        end = totalPages > tempEnd ? tempEnd : totalPages;

        next = totalPages > tempEnd;

        pageList = IntStream.rangeClosed(start,end).boxed().collect(Collectors.toList());
    }
}
