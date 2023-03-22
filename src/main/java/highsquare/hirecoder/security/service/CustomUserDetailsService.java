package highsquare.hirecoder.security.service;

import highsquare.hirecoder.domain.repository.MemberRepository;
import highsquare.hirecoder.domain.repository.StudyMemberRepository;
import highsquare.hirecoder.domain.repository.StudyRepository;
import highsquare.hirecoder.domain.repository.UserAuthorityRepository;
import highsquare.hirecoder.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String memberName) throws UsernameNotFoundException {
        return memberRepository.findByName(memberName)
                .map(this::createUser)
                .orElseThrow(() -> {
                    log.debug("DB에 {} 이 없습니다.", memberName);
                    return new UsernameNotFoundException(memberName + " -> 데이터베이스에서 찾을 수 없습니다.");
                });
    }

    public User createUser(Member member) {
        log.debug("member 정보 : {}", member);

        List<SimpleGrantedAuthority> grantedAuthorities = studyRepository.findAllByManager_Id(member.getId()).stream()
                .map((study -> new SimpleGrantedAuthority(Long.toString(study.getId()))))
                .collect(Collectors.toList());

        return new User(member.getId().toString(),
                member.getPassword(),
                grantedAuthorities);
    }
}

