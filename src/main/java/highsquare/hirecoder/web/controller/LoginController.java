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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static highsquare.hirecoder.constant.CookieConstant.AUTHORIZATION_HEADER;
import static highsquare.hirecoder.constant.CookieConstant.REFRESH_HEADER;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final RefreshTokenProvider refreshTokenProvider;
    private final TokenProvider tokenProvider;
    private final MemberService memberService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "form/login";
    }

    @GetMapping("/signup")
    public String signUpPage(Model model) {
        model.addAttribute("signUpForm", new SignUpForm());
        return "form/signUp";
    }

    @PostMapping("/login")
    public String authorize(@Valid @ModelAttribute LoginForm loginForm, BindingResult bindingResult,
                            HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {

        if (bindingResult.hasErrors()) return "form/login";

        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);
        String refreshToken = refreshTokenProvider.createToken(authentication,request.getRemoteAddr());

        Cookie cookie = new Cookie(AUTHORIZATION_HEADER, URLEncoder.encode("Bearer " + jwt, "utf-8"));
        cookie.setPath("/");
        Cookie refreshCookie = new Cookie(REFRESH_HEADER, URLEncoder.encode(refreshToken, "utf-8"));
        cookie.setPath("/");

        response.addCookie(cookie);
        response.addCookie(refreshCookie);


        return "redirect:/";
    }

    @PostMapping("/signup")
    public String signUp(@Valid @ModelAttribute SignUpForm signUpForm, BindingResult bindingResult) {

        if (memberService.checkMemberIsExists(signUpForm.getName())) {
            bindingResult.rejectValue("name", "signup.name.duplication", "아이디가 중복되었습니다.");
        }

        if (!signUpForm.getPassword().equals(signUpForm.getRePassword())) {
            bindingResult.rejectValue("rePassword", "signup.password", "비밀번호가 다릅니다.");
        }

        if (bindingResult.hasErrors()) return "form/signUp";

        return "redirect:/login";
    }
}
