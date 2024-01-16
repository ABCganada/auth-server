package com.login.oauthAndJwt.auth.jwt;

import com.login.oauthAndJwt.domain.entity.User;
import com.login.oauthAndJwt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JwtAuthenticationFilter
 * 토큰 검증 클래스
 * 토큰 있으면 SecurityContextHolder에 저장
 * 토큰 없으면 authentication 저장 없음
 * */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("jwt filter operation");

        Cookie cookieToken = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("jwtToken"))
                .findFirst()
                .orElse(null);

        /**
         * Validation1
         * jwt token이 쿠키에 있는가?
         * */
        if (cookieToken == null) {
            System.out.println("토큰이 쿠키에 없음");
            filterChain.doFilter(request, response);
            return;
        }

        String token = cookieToken.getValue();
        /**
         * Validation2
         * 토큰이 유효한가?
         * */
        if (jwtService.isExpired(token)) {
            System.out.println(">> >> >> 토큰 만료!");
            filterChain.doFilter(request, response);
            return;
        }

        Long id = jwtService.extractIdFromToken(token);
        User user = userService.getUser(id);

        /**
         * Validation3
         * 토큰에 대한 유저가 있는가?
         * */
        if (user == null) {
            System.out.println(">> >> >> 토큰 유효, 유저는 없음!");
            filterChain.doFilter(request, response);
            return;
        }

        //login한 user 정보로 UsernamePasswordAuthenticationToken 발급
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user.getId(), null, List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        //권한 부여
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}
