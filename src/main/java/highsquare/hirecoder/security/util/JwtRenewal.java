package highsquare.hirecoder.security.util;

import highsquare.hirecoder.security.jwt.JwtFilter;
import highsquare.hirecoder.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static highsquare.hirecoder.constant.CookieConstant.AUTHORIZATION_HEADER;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtRenewal {

    private final TokenProvider tokenProvider;

    public void managerInjection(HttpServletResponse response, Authentication authentication, final Long studyId) {
        log.debug("AuthorityInjection managerInjection 발생 전{}", authentication.getAuthorities());

        renewToken(response, authentication, new AuthorityCallback() {
            @Override
            public void process(List<GrantedAuthority> authorities) {
                authorities.add(new SimpleGrantedAuthority(Long.toString(studyId)));
            }
        });


    }

    public void managerRemove(HttpServletResponse response, Authentication authentication, final Long studyId){
        log.debug("AuthorityInjection managerRemove 발생 전{}", authentication.getAuthorities());

        renewToken(response, authentication, new AuthorityCallback() {
            @Override
            public void process(List<GrantedAuthority> authorities) {
                authorities.remove(new SimpleGrantedAuthority(Long.toString(studyId)));
            }
        });

    }

    private void renewToken(HttpServletResponse response, Authentication authentication,AuthorityCallback ab)  {

        List<GrantedAuthority> authorities = new ArrayList<>(authentication.getAuthorities());
        ab.process(authorities);

        String token = tokenProvider.createToken(new UsernamePasswordAuthenticationToken(authentication, "", authorities));

        try {
            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, URLEncoder.encode("Bearer " + token, "utf-8"));
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (UnsupportedEncodingException ue ){
            throw new RuntimeException("UnsupportedEncodingException 발생");
        }

    }


}
