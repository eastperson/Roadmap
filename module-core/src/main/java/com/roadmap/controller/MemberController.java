package com.roadmap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {

    @GetMapping("/")
    public String index(){

        return "index";
    }

    @GetMapping("/login")
    public String login(){

        return "/member/login";
    }
}
