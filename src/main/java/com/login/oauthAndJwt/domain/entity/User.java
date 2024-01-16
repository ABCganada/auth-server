package com.login.oauthAndJwt.domain.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId;
    private String nickname;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String provider;    //google
    private String providerId;  //구글로 로그인한 유저 고유 아이디

    public void updateGuestToUser() {
        this.role = UserRole.USER;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
