package com.login.oauthAndJwt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("auth")
@Controller
public class SecurityLoginController {

    @GetMapping("/authentication-fail")
    public String authenticationFail(Model model) {
        model.addAttribute("loginType", "auth");
        model.addAttribute("pageName", "Security Token 화면 로그인");

        return "errorPage/authenticationFail";
    }

    @GetMapping("/authorization-fail")
    public String authorizationFail(Model model) {
        model.addAttribute("loginType", "auth");
        model.addAttribute("pageName", "Security Token 화면 로그인");

        return "errorPage/authorizationFail";
    }
}
