package highsquare.hirecoder.security.util;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@FunctionalInterface
public interface AuthorityCallback {

    void process(List<GrantedAuthority> authorities);
}
