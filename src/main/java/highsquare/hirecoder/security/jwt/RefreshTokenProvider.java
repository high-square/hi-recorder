package highsquare.hirecoder.security.jwt;

import highsquare.hirecoder.security.exception.ExpiredTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.stream.Collectors;

import static highsquare.hirecoder.constant.TokenConstant.AUTHORITIES_KEY;

@Component
@Slf4j
public class RefreshTokenProvider implements InitializingBean {

    private static final String IP_ADDRESS_KEY = "ipAddress";

    private final String secret;

    private final long tokenValidityInMilliseconds;

    private Key key;


    public RefreshTokenProvider(@Value("${jwt.secret}") String secret,
                                @Value("${jwt.refresh-token-validity-in-seconds}") long tokenValidityInSeconds) {
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Authentication authentication, String ipAddress) {

        log.info("Refresh token 생성 시 ipAddress : {}", ipAddress);
        String cryptIpAddress=null;

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        try {
            cryptIpAddress = hmacAndBase64(secret, ipAddress, "HmacSHA512");
        } catch (NoSuchAlgorithmException|InvalidKeyException|UnsupportedEncodingException e) {
            log.debug("암호화 과정에 에러 발생");
            throw new RuntimeException("암호화 과정에 에러 발생");
        }


        long now = System.currentTimeMillis();

        String token = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(IP_ADDRESS_KEY, cryptIpAddress)
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(new Date(now + this.tokenValidityInMilliseconds))
                .compact();

        log.info("생성된 Refresh 토큰 : {}", token);

        return token;

    }

    public boolean validateToken(String token,String ipAddress) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            log.debug("Refresh claims : {}", claims.get(IP_ADDRESS_KEY));

            String cryptIpAddress = hmacAndBase64(secret, ipAddress, "HmacSHA512");

            if (!claims.get(IP_ADDRESS_KEY).toString().equals(cryptIpAddress)) {
                log.debug("Refresh 토큰의 ipAddress와 현재 접속한 ipAddress가 일치하지 않음");
                return false;
            }

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("잘못된 Refresh 토큰 서명입니다.");
            throw e;
        } catch (ExpiredJwtException e) {
            log.warn("만료된 Refresh 토큰입니다.");
            throw new ExpiredTokenException("만료된 Refresh 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 Refresh 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.warn("Refresh 토큰이 잘못되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("예외발생");
        }

        return false;
    }

    public static String hmacAndBase64(String secret, String ipAddress, String Algorithms)
            throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {

        //1. SecretKeySpec 클래스를 사용한 키 생성
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes("utf-8"), Algorithms);

        //2. 지정된  MAC 알고리즘을 구현하는 Mac 객체를 작성합니다.
        Mac hasher = Mac.getInstance(Algorithms);

        //3. 키를 사용해 이 Mac 객체를 초기화
        hasher.init(secretKey);

        //3. 암호화 하려는 데이터의 바이트의 배열을 처리해 MAC 조작을 종료
        byte[] hash = hasher.doFinal(ipAddress.getBytes());

        //4. Base 64 Encode to String
        return Base64.encodeBase64String(hash);
    }

}
