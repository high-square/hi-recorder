package highsquare.hirecoder.domain.repository;

import highsquare.hirecoder.entity.Member;
import highsquare.hirecoder.entity.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAuthorityRepository extends JpaRepository<UserAuthority, Long> {

    List<UserAuthority> findByMember(Member member);
}
