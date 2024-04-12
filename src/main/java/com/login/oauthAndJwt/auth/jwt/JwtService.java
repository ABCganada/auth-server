package com.login.oauthAndJwt.auth.jwt;

import com.login.oauthAndJwt.auth.oauth.CustomOAuth2User;
import com.nimbusds.jwt.JWT;
import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Slf4j
@Getter
@Service
public class JwtService {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private long expiration;

    @Value("${jwt.access.header}")
    private String AccessTokenHeader;

    //Jwt 생성
    public String createToken(Long id) {
        Claims claims = Jwts.claims();
        claims.put("id", id);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    //Claim에서 id 꺼내기
    public Long extractIdFromToken(String token) {
        return ((Number) extractClaims(token).get("id")).longValue();
    }

    //발급된 Token 만료 체크
    public boolean isExpired(String token) {
        Date expiredDate = extractClaims(token).getExpiration();

        return expiredDate.before(new Date());
    }

    //Claim parsing
    private Claims extractClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    //쿠키에 토큰 전달
    public void sendCookie(HttpServletResponse response, CustomOAuth2User oAuthUser) {
        String accessToken = createToken(oAuthUser.getId());

        Cookie cookie = new Cookie("jwtToken", accessToken);
        cookie.setMaxAge(30 * 60);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setDomain("localhost");
        response.addCookie(cookie);
    }

    public boolean isValid(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("만료된 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("잘못된 토큰입니다.");
        }

        return false;
    }
}
