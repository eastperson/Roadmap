package com.roadmap.controller;

import com.roadmap.dto.member.CurrentUser;
import com.roadmap.model.Member;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class IndexController {

    @GetMapping("/hello")
    @ResponseBody
    public String hello(){
        return "hello world. It is core";
    }

    @GetMapping("/")
    public String index(@CurrentUser Member member, Model model){
        if(member != null) {
            model.addAttribute(member);
        }

        return "index";
    }
}