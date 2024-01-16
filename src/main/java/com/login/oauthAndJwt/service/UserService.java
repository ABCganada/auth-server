package com.login.oauthAndJwt.service;

import com.login.oauthAndJwt.domain.entity.User;
import com.login.oauthAndJwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class UserService {

    //UserRepository를 주입 받아 비즈니스 로직을 처리
    private final UserRepository userRepository;
    //Spring Security를 사용할 때, passwordEncoder를 사용한다.

    /**
     * loginId 중복 체크
     * userRepository는 JPA 구현체다.
     * existsBy 문법은 UserRepository 인터페이스에서 정의된 메서드이며,
     * JPA에서는 메서드 이름을 통해 자동으로 쿼리를 생성하도록 지원한다.
     * */
    public boolean checkLoginIdDuplicate(String loginId) {
        System.out.println(">> UserService.checkLoginIdDuplicate()");
        return userRepository.existsByLoginId(loginId);
    }

    /**
     * nickname 중복 체크
     */
    public boolean checkNicknameDuplicate(String nickname) {
        System.out.println(">> UserService.checkNicknameDuplicate()");
        return userRepository.existsByNickname(nickname);
    }

    /**
     * SignUpRequestDto의 toEntity() 메서드를 사용해 User 객체를 생성하고
     * UserRepository라는 JPA Repository로 엔티티를 데이터베이스에 저장한다.
     * */
//    public void signUp(SignUpRequestDto request) {
//        userRepository.save(request.toEntity());
//    }

    /**
     * 인코딩된 패스워드로 데이터베이스에 저장한다.
     * */
//    public void signUp2(SignUpRequestDto request) {
//        System.out.println(">> UserService.signUp2()");
//        userRepository.save(request.toEntity(encoder.encode(request.getPassword())));
//    }

    /**
     * LoginRequestDto로 login 시도한다.
     * 먼저 레포에서 findBy해서 optionalUser를 찾는다.
     * optinalUser가 없으면 null을 리턴하고,
     * 있다면 optionalUser에서 user를 찾는다.
     * user의 password가 실제 password와 다르다면 null을 리턴해 로그인을 막고,
     * 같다면 user를 반환한다.
     *
     * Optional을 사용하는 이유는 NullPointerException을 방지하기 위해서다.
     * 반환값이 '없음'을 명시할 필요가 있고 null로 반환했을 때 에러를 일으킬 가능성이
     * 높은 상황에서 반환 타입으로 Optional을 사용하는 것이 권장된다.
     * */
//    public User login(LoginRequestDto request) {
//        System.out.println(">> UserService.login()");
//        Optional<User> optionalUser = userRepository.findByLoginId(request.getLoginId());
//
//        //optinalUser가 '없음'이라면, null 반환
//        if (optionalUser.isEmpty()) {
//            System.out.println("유저 못 찾음");
//            return null;
//        }
//
//        User user = optionalUser.get();
//
//        if (!encoder.matches(request.getPassword(), user.getPassword())) {
//            System.out.println("user password: " + user.getPassword());
//            System.out.println("request password: " + encoder.encode(request.getPassword()));
//            System.out.println("비밀번호 일치하지 않음");
//            return null;
//        }
//
//        return user;
//    }

    /**
     * userId를 받아 User를 리턴
     * */
    public User getUser(Long id) {
        if (id == null) {
            return null;
        }

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return null;
        }

        return optionalUser.get();
    }

    /**
     * loginId를 받아 User를 리턴
     * 인증, 인가 시에 사용
     * */
    public User getLoginUser(String loginId) {
        if (loginId == null) {
            return null;
        }

        Optional<User> optionalUser = userRepository.findByLoginId(loginId);
        if (optionalUser.isEmpty()) {
            return null;
        }

        return optionalUser.get();
    }

    public void updateGuestToUser(Long id, String nickname) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser == null) {
            return;
        }

        User user = optionalUser.get();
        user.updateGuestToUser();
        user.updateNickname(nickname);
        userRepository.save(user);
    }

    public boolean isDuplicated(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}
