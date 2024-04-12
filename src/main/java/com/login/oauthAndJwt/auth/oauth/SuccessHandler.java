package com.login.oauthAndJwt.auth.oauth;

import com.login.oauthAndJwt.auth.jwt.JwtService;
import com.login.oauthAndJwt.domain.entity.User;
import com.login.oauthAndJwt.domain.entity.UserRole;
import com.login.oauthAndJwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("onAuthenticationSuccess()");

        try {
            CustomOAuth2User oAuthUser = (CustomOAuth2User) authentication.getPrincipal();

            if (oAuthUser.getRole() == UserRole.GUEST) {

                String nickname = handleNicknameInput(request);

                if (nickname.isEmpty()) {
                    System.out.println(">> 입력받은 닉네임이 없습니다");

                    jwtService.sendCookie(response, oAuthUser);
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    response.sendRedirect("/auth/set-nickname");
                    return;
                }

                User user = userRepository.findById(oAuthUser.getId())
                        .orElseThrow(() -> new IllegalAccessException("해당하는 유저가 없습니다."));

                user.updateGuestToUser();
                user.updateNickname(nickname);
                userRepository.save(user);

                getRedirectStrategy().sendRedirect(request, response, "/auth/set-nickname");
            } else {
                loginSuccess(response, oAuthUser);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuthUser) throws IOException {
        System.out.println("loginSuccess()");

        response.setStatus(HttpServletResponse.SC_OK);
        jwtService.sendCookie(response, oAuthUser);
        response.sendRedirect("http://localhost:8000/main");
    }

    private String handleNicknameInput(HttpServletRequest request) {
        String nickname = request.getParameter("nickname");
        return (nickname != null) ? nickname : "";
    }
}
