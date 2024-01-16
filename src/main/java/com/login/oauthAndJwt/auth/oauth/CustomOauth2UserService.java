package com.login.oauthAndJwt.auth.oauth;

import com.login.oauthAndJwt.auth.oauth.info.*;
import com.login.oauthAndJwt.domain.entity.User;
import com.login.oauthAndJwt.domain.entity.UserRole;
import com.login.oauthAndJwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
* 토큰 없으면 일로 넘어와서 OAuth 유저를 찾음
* */
@RequiredArgsConstructor
@Service
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("OAuthUserService.loadUser(): OAuth 로그인 요청 진입");
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = null;
        //oauth 제공 주체. ex) google
        String provider = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("**" + provider);

        if (provider.equals("google")) {
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (provider.equals("kakao")) {
            System.out.println("카카오 로그인 요청");
            oAuth2UserInfo = new KakaoUserInfo((Map) oAuth2User.getAttributes());
        } else if (provider.equals("naver")) {
            System.out.println("네이버 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes());
        } else if (provider.equals("facebook")) {
            System.out.println("페이스북 로그인 요청");
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
        }

        //유저 고유 id값
        String providerId = oAuth2UserInfo.getProviderId();
        String loginId = provider + "_" + providerId;
        //provider 제공 정보 중 username key값
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        Optional<User> optionalUser = userRepository.findByLoginId(loginId);
        User user;

        if (optionalUser.isEmpty()) {
            user = User.builder()   //회원가입 진행
                    .loginId(loginId)
                    .provider(provider)
                    .providerId(providerId)
                    .role(UserRole.GUEST)
                    .build();
            userRepository.save(user);
        } else {
            user = optionalUser.get();
        }

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())),
                attributes,
                userNameAttributeName,
                user.getId(),
                user.getRole()
        );
    }
}
