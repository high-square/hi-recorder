package highsquare.hirecoder.config;

import highsquare.hirecoder.security.CustomWebSecurityExpressionHandler;
import highsquare.hirecoder.security.jwt.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static highsquare.hirecoder.constant.CookieConstant.AUTHORIZATION_HEADER;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                                           JwtAccessDeniedHandler jwtAccessDeniedHandler,
                                           TokenProvider tokenProvider,
                                           RefreshTokenProvider refreshTokenProvider) throws Exception {
        http
                .csrf().disable()

                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and()
                .apply(new JwtSecurityConfig(tokenProvider,refreshTokenProvider))

                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)



                .and()
                .authorizeRequests()
                .mvcMatchers("/login").permitAll()
                .mvcMatchers("/signup").permitAll()
                .expressionHandler(new CustomWebSecurityExpressionHandler())
                .mvcMatchers("/study/{studyId}/admin").access("isMemberManager(#studyId)")
                .anyRequest().authenticated()



                .and()
                .logout()
                .deleteCookies(AUTHORIZATION_HEADER);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        // TODO: 2023-03-15 다양한 종류의 passwordEncoder가 있다.
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> {
            web.ignoring().mvcMatchers(
                    "/h2-console/**",
                    "/favicon.ico",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/admin/**"
            );
        };
    }
}
