package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.MemberRepository;
import highsquare.hirecoder.domain.repository.UserAuthorityRepository;
import highsquare.hirecoder.entity.Authority;
import highsquare.hirecoder.entity.Member;
import highsquare.hirecoder.entity.UserAuthority;
import highsquare.hirecoder.security.util.SecurityUtil;
import highsquare.hirecoder.web.form.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final UserAuthorityRepository userAuthorityRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Member signUp(SignUpForm signUpForm) {
        memberRepository.findByName(signUpForm.getName())
                .ifPresent(user -> { throw new RuntimeException("이미 가입된 유저입니다."); });

        Member member = Member.builder()
                .name(signUpForm.getName())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .email(signUpForm.getEmail())
                .build();
        memberRepository.save(member);

        UserAuthority userAuthority = new UserAuthority(member, Authority.USER);
        userAuthorityRepository.save(userAuthority);

        return member;
    }

    @Transactional(readOnly = true)
    public Optional<Member> getMemberWithAuthorities(String memberName) {
        return memberRepository.findByName(memberName);
    }

    @Transactional(readOnly = true)
    public Optional<Member> getMyMemberWithAuthorities() {
        return SecurityUtil.getCurrentMemberName()
                .flatMap(memberRepository::findByName);
    }
}
