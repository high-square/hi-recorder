package highsquare.hirecoder.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    public static final String AUTHORIZATION_HEADER ="Authorization";

    private final TokenProvider tokenProvider;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String jwt = resolveToken(httpServletRequest);
        String requestURI = httpServletRequest.getRequestURI();

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication((authentication));
            log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}",
                    authentication.getName(), requestURI);
        } else {
            log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
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
}
