package highsquare.hirecoder.security.jwt;

import highsquare.hirecoder.security.exception.ExpiredTokenException;
import highsquare.hirecoder.security.exception.NoJwtTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;

import static highsquare.hirecoder.constant.CookieConstant.AUTHORIZATION_HEADER;
import static highsquare.hirecoder.constant.CookieConstant.REFRESH_HEADER;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter implements Filter {



    private final TokenProvider tokenProvider;

    private final RefreshTokenProvider refreshTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String jwt = resolveToken(httpServletRequest);
        String refreshToken = resolveRefreshToken(httpServletRequest);

        String requestURI = httpServletRequest.getRequestURI();
        String ipAddress = httpServletRequest.getRemoteAddr();

        try {
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                Authentication authentication = tokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication((authentication));
                log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}",
                        authentication.getName(), requestURI);
            } else {
                log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
                throw new NoJwtTokenException("JWT 토큰이 없습니다. Refresh 토큰을 확인하겠습니다.");
            }
        } catch (ExpiredTokenException | NoJwtTokenException e) {

            try {
                if (StringUtils.hasText(refreshToken) && refreshTokenProvider.validateToken(refreshToken, ipAddress)) {
                    Authentication authentication = tokenProvider.getAuthentication(refreshToken);
                    SecurityContextHolder.getContext().setAuthentication((authentication));
                    log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}",
                            authentication.getName(), requestURI);

                    String renewToken = tokenProvider.createToken(authentication);
                    Cookie cookie = new Cookie(AUTHORIZATION_HEADER, URLEncoder.encode("Bearer " + renewToken, "utf-8"));
                    cookie.setPath("/");

                    httpServletResponse.addCookie(cookie);

                } else {
                    log.debug("유효한 Refresh 토큰이 없습니다, uri: {}", requestURI);
                    Cookie cookie = new Cookie(REFRESH_HEADER, null);
                    cookie.setMaxAge(0);
                    cookie.setPath("/");

                    httpServletResponse.addCookie(cookie);

                }
            } catch (ExpiredTokenException et) {
                log.debug("Refresh 토큰의 만료기간이 지났습니다, uri: {}", requestURI);
                Cookie cookie = new Cookie(REFRESH_HEADER, null);
                cookie.setMaxAge(0);
                cookie.setPath("/");

                httpServletResponse.addCookie(cookie);
            }

        }


        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) throws UnsupportedEncodingException {
        String bearerToken = null;
        if (request.getCookies() != null)
            bearerToken = Arrays.stream(request.getCookies()).filter((cookie -> cookie.getName().equals(AUTHORIZATION_HEADER)))
                    .findFirst().map(cookie -> cookie.getValue()).orElse(null);

        log.debug("쿠키의 토큰 : {}", bearerToken);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer+")) {
            bearerToken = URLDecoder.decode(bearerToken, "utf-8");
            log.debug("헤더의 토큰은 {}", bearerToken);
            String token = bearerToken.substring(7);
            log.debug("jwt 토큰은 {}", token);
            return token;
        }

        return null;
    }

    private String resolveRefreshToken(HttpServletRequest request) throws UnsupportedEncodingException {
        String refreshToken = null;
        if (request.getCookies() != null)
            refreshToken = Arrays.stream(request.getCookies()).filter((cookie -> cookie.getName().equals(REFRESH_HEADER)))
                    .findFirst().map(cookie -> cookie.getValue()).orElse(null);

        log.debug("쿠키의 Refresh 토큰 : {}", refreshToken);
        if (StringUtils.hasText(refreshToken)) {
            refreshToken = URLDecoder.decode(refreshToken, "utf-8");
            log.debug("Refresh 토큰은 {}", refreshToken);
            return refreshToken;
        }

        return null;
    }
}
