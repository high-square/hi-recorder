package highsquare.hirecoder.web.controller;

import highsquare.hirecoder.domain.repository.MemberRepository;
import highsquare.hirecoder.domain.service.MemberService;
import highsquare.hirecoder.dto.TokenInfo;
import highsquare.hirecoder.entity.Member;
import highsquare.hirecoder.security.jwt.JwtFilter;
import highsquare.hirecoder.security.jwt.RefreshTokenProvider;
import highsquare.hirecoder.security.jwt.TokenProvider;
import highsquare.hirecoder.web.form.LoginForm;
import highsquare.hirecoder.web.form.SignUpForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final RefreshTokenProvider refreshTokenProvider;
    private final TokenProvider tokenProvider;
    private final MemberService memberService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @GetMapping("/login")
    public String loginPage() {
        return "form/login";
    }

    @GetMapping("/signup")
    public String signUpPage() {
        return "form/signUp";
    }

    @PostMapping("/login")
    public String authorize(@Valid @ModelAttribute LoginForm loginForm,
                            HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {

        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);
        String refreshToken = refreshTokenProvider.createToken(authentication,request.getRemoteAddr());

        Cookie cookie = new Cookie(JwtFilter.AUTHORIZATION_HEADER, URLEncoder.encode("Bearer " + jwt, "utf-8"));
        cookie.setPath("/");
        Cookie refreshCookie = new Cookie(JwtFilter.REFRESH_HEADER, URLEncoder.encode(refreshToken, "utf-8"));
        cookie.setPath("/");

        response.addCookie(cookie);
        response.addCookie(refreshCookie);


        return "redirect:/";
    }

    @PostMapping("/signup")
    public String signUp(@Valid @ModelAttribute SignUpForm signUpForm) {
        Member member = memberService.signUp(signUpForm);
        return "redirect:/login";
    }
}
