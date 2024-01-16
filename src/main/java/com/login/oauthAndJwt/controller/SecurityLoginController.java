package com.login.oauthAndJwt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecurityLoginController {

    @GetMapping("/security-login/authentication-fail")
    public String authenticationFail(Model model) {
        model.addAttribute("loginType", "security-login");
        model.addAttribute("pageName", "Security Token 화면 로그인");

        return "errorPage/authenticationFail";
    }

    @GetMapping("/security-login/authorization-fail")
    public String authorizationFail(Model model) {
        model.addAttribute("loginType", "security-login");
        model.addAttribute("pageName", "Security Token 화면 로그인");

        return "errorPage/authorizationFail";
    }
}
