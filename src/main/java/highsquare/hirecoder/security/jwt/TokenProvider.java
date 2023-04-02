package highsquare.hirecoder.security.jwt;

import highsquare.hirecoder.security.exception.ExpiredTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static highsquare.hirecoder.constant.TokenConstant.AUTHORITIES_KEY;


@Component
@Slf4j
public class TokenProvider implements InitializingBean {

    private final String secret;

    private final long tokenValidityInMilliseconds;

    private Key key;

    public TokenProvider(@Value("${jwt.secret}") String secret,
                         @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Authentication authentication) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        log.info("token 생성 시 authorities : {}", authorities);

        long now = System.currentTimeMillis();


        String token = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(new Date(now + this.tokenValidityInMilliseconds))
                .compact();

        log.info("생성된 토큰 : {}", token);

        return token;
    }

    public Authentication getAuthentication(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        log.debug("claims : {}", claims.getSubject());

        Collection<? extends GrantedAuthority> authorities;
        if (claims.get(AUTHORITIES_KEY).toString().isBlank()) {
            authorities = new ArrayList<>();
        } else {
            authorities =
                    Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
        }

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다.");
            throw e;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.");
            throw new ExpiredTokenException("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 잘못되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("예외발생");
        }

        return false;
    }
}
