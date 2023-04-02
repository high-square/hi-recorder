package highsquare.hirecoder.security.jwt;

import highsquare.hirecoder.utils.ScriptUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

import static highsquare.hirecoder.security.jwt.JwtFilter.AUTHORIZATION_HEADER;

@Component
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.debug(accessDeniedException.getMessage());

        ScriptUtils.alertAndBackPage(response, "잘못된 접근입니다.");
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}
