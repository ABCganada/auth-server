package com.login.oauthAndJwt.controller;

import com.login.oauthAndJwt.domain.dto.NicknameDto;
import com.login.oauthAndJwt.domain.entity.User;
import com.login.oauthAndJwt.domain.entity.UserRole;
import com.login.oauthAndJwt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("/security-login")
public class UserController {

    private final UserService userService;

    @GetMapping(value = {"", "/"})
    public String home(Model model, Authentication auth) {
        model.addAttribute("loginType", "security-login");
        model.addAttribute("pageName", "Security Token 화면 로그인");

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
        model.addAttribute("loginType", "security-login");
        model.addAttribute("pageName", "Security 로그인");

        return "signUp";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginType", "security-login");
        model.addAttribute("pageName", "Security 로그인");

        return "login";
    }

    @GetMapping("/set-nickname")
    public String setNicknamePage(Authentication auth, Model model) {
        User user = userService.getUser(Long.valueOf(auth.getName()));

        if (user.getRole() == UserRole.GUEST) {
            model.addAttribute("nicknameDto", new NicknameDto());
            return "setNickname";
        }

        return "redirect:/security-login";
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

        return "redirect:/security-login";
    }

    @GetMapping("/info")
    public String userInfo(Model model, Authentication auth) {
        model.addAttribute("loginType", "security-login");
        model.addAttribute("pageName", "Security 로그인");

        User user = userService.getUser(Long.parseLong(auth.getName()));
        System.out.println(">> " + user);

        if (user == null) {
            return "redirect:/security-login/login";
        }

        model.addAttribute("user", user);

        return "info";
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {
        model.addAttribute("loginType", "security-login");
        model.addAttribute("pageName", "Security 로그인");

        return "admin";
    }
}
