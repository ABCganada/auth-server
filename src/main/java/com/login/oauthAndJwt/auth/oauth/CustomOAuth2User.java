package com.login.oauthAndJwt.auth.oauth;

import com.login.oauthAndJwt.domain.entity.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private Long id;
    private UserRole role;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes, String nameAttributeKey,
                            Long id, UserRole role) {
        super(authorities, attributes, nameAttributeKey);
        this.id = id;
        this.role = role;
    }
}
