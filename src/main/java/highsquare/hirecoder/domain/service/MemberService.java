package highsquare.hirecoder.domain.service;

import highsquare.hirecoder.domain.repository.MemberRepository;
import highsquare.hirecoder.entity.Member;
import highsquare.hirecoder.web.form.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean checkMemberIsExists(String name) {
        return memberRepository.findByName(name).isPresent();
    }

    @Transactional
    public Member signUp(SignUpForm signUpForm) {
        Member member = Member.builder()
                .name(signUpForm.getName())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .email(signUpForm.getEmail())
                .build();
        memberRepository.save(member);

        return member;
    }
}
