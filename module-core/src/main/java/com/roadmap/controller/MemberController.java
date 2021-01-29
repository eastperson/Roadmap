package com.roadmap.controller;

import com.roadmap.dto.member.CurrentUser;
import com.roadmap.dto.member.SignUpForm;
import com.roadmap.model.Member;
import com.roadmap.repository.MemberRepository;
import com.roadmap.service.MemberService;
import com.roadmap.validation.SignUpFormValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;


@Controller
@RequiredArgsConstructor
@Log4j2
public class MemberController {

    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final SignUpFormValidator signUpFormValidator;
    private final MemberService memberService;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/login")
    public String login(){

        return "/member/login";
    }

    @GetMapping("/sign-up")
    public String signUp(Model model){

        model.addAttribute(new SignUpForm());

        return "member/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpFormSubmit(@Valid SignUpForm signUpForm, Errors errors) {

        if(errors.hasErrors()){
            return "member/sign-up";
        }

        memberService.saveNewMember(signUpForm);

        return "redirect:/";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model) {
        Member member = memberRepository.findByEmail(email);
        if(member == null){
            model.addAttribute("error", "wrong.email");
            return "member/check-email-token";
        }

        if(!member.isValidToken(token)) {
            model.addAttribute("error","wrong.token");
        }

        memberService.completeSignUp(member);
        model.addAttribute("numberOfUser",memberRepository.count());
        model.addAttribute("nickname",member.getNickname());
        return "member/check-email-token";
    }

    @GetMapping("/check-email-send")
    public String sendEmail(@CurrentUser Member member, Model model){

        if(member != null) {
            model.addAttribute("email",member.getEmail());
        }

        return "member/check-email-send";
    }

    @GetMapping("/resend-confirm-email")
    public String resendConfirmEmail(@CurrentUser Member member, Model model) {
        if (!member.canSendConfirmEmail()) {
            model.addAttribute("error", "인증 이메일은 1시간에 한번만 전송할 수 있습니다.");
            model.addAttribute("email", member.getEmail());
            return "member/check-email-send";
        }

        memberService.sendSignUpConfirmEmail(member);
        return "redirect:/";
    }
}
