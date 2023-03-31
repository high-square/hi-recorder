package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.domain.repository.custom.BoardRepositoryCustom;
import highsquare.hirecoder.entity.Board;
import highsquare.hirecoder.entity.Kind;
import net.bytebuddy.asm.Advice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface BoardRepository extends JpaRepository<Board, Long>, BoardRepositoryCustom {
    // kind가 RECRUIT일 때 OR CONTENT 일 때
    @Query(value = "select b from Board b join fetch b.study s where b.kind = :kind",
            countQuery = "select count(b) from Board b join b.study s where b.kind = :kind")
    Page<Board> findStudyAll(@Param("kind") Kind kind, Pageable pageable);

    @Query(value = "select b from Board b join fetch b.study s where (b.title LIKE %:search% OR b.content LIKE %:search%) AND b.kind = :kind",
            countQuery = "select count(b) from Board b join b.study s where b.kind = :kind")
    Page<Board> findBySearch(@Param("search") String searchKeyword, @Param("kind")Kind kind, Pageable pageable);

    Page<Board> findByMemberIdInAndStudyIdAndKind(List<Long> memberIds, Long studyId, Kind kind, Pageable pageable);

    @Query(value = "select b from Board b where b.member = :member and b.study = :study and b.kind = :kind and (b.title LIKE %:search% OR b.content LIKE %:search%)")
    Page<Board> findByStudyBoardSearch(@Param("member") List<Long> memberIds,
                                                          @Param("study") Long studyId,
                                                          @Param("kind") Kind kind,
                                                          @Param("search") String searchKeyword,
                                                          Pageable pageable);

    @Query(value = "select b from Board b where b.kind=:kind and b.createDate BETWEEN :start and :end order by b.viewCnt DESC")
    List<Board> findTop5ByBoardList(@Param("start")LocalDateTime start,
                                    @Param("end")LocalDateTime end,
                                    @Param("kind") Kind kind,
                                    Pageable pageable);

    @Modifying
    @Transactional
    @Query("update Board b set b.likeCnt=:likeCnt where b.id=:boardId")
    void updateLikeCnt(@Param("boardId") Long boardId, @Param("likeCnt") Integer likeCnt);
}
