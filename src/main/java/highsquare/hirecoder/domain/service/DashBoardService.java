package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.BoardRepository;
import highsquare.hirecoder.domain.repository.StudyMemberRepository;
import highsquare.hirecoder.domain.repository.StudyRepository;
import highsquare.hirecoder.dto.DashWeekStats;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Kind;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Collator;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashBoardService {

    public final BoardRepository boardRepository;

    public final StudyRepository studyRepository;
    public final StudyMemberRepository studyMemberRepository;

    public Long getCountBoard(Long studyId, Kind kind) {
        return boardRepository.countByStudyId(studyId, kind);
    }

    public Long getViewCntBoard(Long studyId, Kind kind){
        // 해당 스터디의 전체 글 조회수
        return boardRepository.getViewCntByStudyId(studyId, kind);
    }

    // 요일별 차트
    public List<DashWeekStats> getWeekStats(Long studyId, Kind kind){
        // 게시글 수 조회
        List<Board> boards = boardRepository.findByStudyIdAndKind(studyId, kind);
        Map<DayOfWeek, List<Board>> boardsByDayOfWeek = boards.stream()
                .collect(Collectors.groupingBy(board -> board.getCreateDate().getDayOfWeek()));
        // 조회수 조회
        Map<DayOfWeek, List<Board>> allViewCntByDayOfWeek = boards.stream()
                .collect(Collectors.groupingBy(board -> board.getCreateDate().getDayOfWeek()));

        //

        return null;
    }

}
