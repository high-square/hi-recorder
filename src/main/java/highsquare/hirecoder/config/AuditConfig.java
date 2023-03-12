package highsquare.hirecoder.config;

import highsquare.hirecoder.constant.SessionConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AuditConfig implements AuditorAware<Long> {

    private final HttpSession httpSession;
    @Override
    public Optional<Long> getCurrentAuditor() {

        Long memberId = (Long) httpSession.getAttribute(SessionConstant.MEMBER_ID);

        return Optional.of(memberId);
    }
}
