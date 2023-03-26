package highsquare.hirecoder.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import highsquare.hirecoder.domain.repository.MemberRepository;
import highsquare.hirecoder.domain.repository.StudyMemberRepository;
import highsquare.hirecoder.domain.repository.StudyRepository;
import highsquare.hirecoder.domain.repository.UserAuthorityRepository;
import highsquare.hirecoder.dto.StudyAuthorities;
import highsquare.hirecoder.entity.Member;
import highsquare.hirecoder.entity.Study;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final StudyRepository studyRepository;

    private final StudyMemberRepository studyMemberRepository;

    private final ObjectMapper objectMapper;


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

        /**
         * grantedAuthorities에 studyManager 권한과 study 권한을 넣어줘야함
         * StudyAuthorities라는 DTO에 넣어서 JSON으로 바꾼 값을 SimpleGrantedAuthority에 넣어줌
         */
        List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>();

        List<Long> managers = new ArrayList<>();
        List<Long> studies = new ArrayList<>();

        studyRepository.findAllByManager_Id(member.getId()).forEach(study -> managers.add(study.getId()));
        studyMemberRepository.findAllStudyByMemberId(member.getId()).forEach(study ->studies.add(study.getId()));

        StudyAuthorities studyAuthorities = new StudyAuthorities();
        studyAuthorities.setManagers(managers);
        studyAuthorities.setStudies(studies);

        try {
            String authority = objectMapper.writeValueAsString(studyAuthorities);
            log.debug("authority {}",authority);
            grantedAuthorities.add(new SimpleGrantedAuthority(authority));
        }  catch (JsonProcessingException e) {
            log.debug("JsonProcessingException 발생");
            throw new RuntimeException("JsonProcessingException 발생");
        }


        return new User(member.getId().toString(),
                member.getPassword(),
                grantedAuthorities);
    }
}

