package com.login.oauthAndJwt.controller;

import com.login.oauthAndJwt.domain.dto.NicknameDto;
import com.login.oauthAndJwt.domain.entity.User;
import com.login.oauthAndJwt.domain.entity.UserRole;
import com.login.oauthAndJwt.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;

    @GetMapping(value = {"", "/"})
    public String home(Model model, Authentication auth) {
        model.addAttribute("loginType", "main");
        model.addAttribute("pageName", "Sandwich AI");

        System.out.println("Controller, 홈 화면");

        if (auth != null) {
            User user = userService.getUser(Long.valueOf(auth.getName()));
            if (user != null) {
                model.addAttribute("nickname", user.getNickname());
            }
        }

        return "home";
    }

    @GetMapping("/sign-up")
    public String signUpPage(Model model) {
        model.addAttribute("loginType", "main");
        model.addAttribute("pageName", "Sandwich AI");

        return "signUp";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginType", "main");
        model.addAttribute("pageName", "Sandwich AI");

        return "login";
    }

    @GetMapping("/set-nickname")
    public String setNicknamePage(Authentication auth, Model model) {
        if (auth == null || !auth.isAuthenticated()) {
            // 사용자가 로그인되지 않은 경우 로그인 페이지로 리디렉션
            return "redirect:/login";
        }

        User user = userService.getUser(Long.valueOf(auth.getName()));

        if (user.getRole() == UserRole.GUEST) {
            model.addAttribute("nicknameDto", new NicknameDto());
            return "setNickname";
        }

        return "redirect:http://localhost:8000/main";
    }

    @PostMapping("/set-nickname")
    public String setNickname(@ModelAttribute NicknameDto nicknameDto, Authentication auth, BindingResult bindingResult) {
        User user = userService.getUser(Long.valueOf(auth.getName()));

        if (userService.isDuplicated(nicknameDto.getNickname())) {
            bindingResult.addError(new FieldError("nicknameDto", "nickname", "존재하는 닉네임입니다."));
            return "setNickname";
        }

        if (user.getRole() == UserRole.GUEST) {
            userService.updateGuestToUser(user.getId(), nicknameDto.getNickname());
        }

        return "redirect:http://localhost:8000/main";
    }
}
