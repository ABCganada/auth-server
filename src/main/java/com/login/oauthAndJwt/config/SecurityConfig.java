package com.login.oauthAndJwt.config;

import com.login.oauthAndJwt.auth.CustomAccessDeniedHandler;
import com.login.oauthAndJwt.auth.CustomAuthenticationEntryPoint;
import com.login.oauthAndJwt.auth.jwt.JwtAuthenticationFilter;
import com.login.oauthAndJwt.auth.oauth.CustomLogoutHandler;
import com.login.oauthAndJwt.auth.oauth.CustomOauth2UserService;
import com.login.oauthAndJwt.auth.oauth.SuccessHandler;
import com.login.oauthAndJwt.domain.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig  {

    private final CustomLogoutHandler customLogoutHandler;
    private final SuccessHandler successHandler;
    private final CustomOauth2UserService customOauth2UserService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/auth/set-nickname").hasAuthority(UserRole.GUEST.name())
                //인증
                .antMatchers("/auth/info").authenticated()
                //인가
                .antMatchers("/auth/admin/**").hasAuthority(UserRole.ADMIN.name())
                .anyRequest().permitAll()

                .and()
                .logout()
                .logoutUrl("/auth/logout")
                .addLogoutHandler(customLogoutHandler)
                .invalidateHttpSession(true).deleteCookies("JSESSIONID", "jwtToken")

                //OAuth
                .and()
                .oauth2Login()
                .loginPage("/auth/login")
                .defaultSuccessUrl("http://localhost:8081/main")
                .successHandler(successHandler)
                .userInfoEndpoint().userService(customOauth2UserService);

        http
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler());

        return http.build();
    }
}
