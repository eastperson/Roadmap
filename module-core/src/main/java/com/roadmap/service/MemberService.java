package com.roadmap.service;

import com.roadmap.config.AppProperties;
import com.roadmap.dto.email.EmailMessage;
import com.roadmap.dto.member.*;
import com.roadmap.model.Location;
import com.roadmap.model.Member;
import com.roadmap.model.Tag;
import com.roadmap.model.UserMember;
import com.roadmap.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final TemplateEngine templateEngine;
    private final EmailService emailService;
    private final AppProperties appProperties;


    public void login(Member member) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserMember(member),
                member.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(token);

    }

    public Member saveNewMember(SignUpForm signUpForm) {
        signUpForm.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        Member member = modelMapper.map(signUpForm,Member.class);
        member.setJoinedAt(LocalDateTime.now());
        login(member);
        sendSignUpConfirmEmail(member);
        return memberRepository.save(member);
    }

    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(emailOrNickname);
        if(member == null) {
            member = memberRepository.findByNickname(emailOrNickname);
        }
        if(member == null){
            throw new UsernameNotFoundException(emailOrNickname);
        }

        return new UserMember(member);
    }

    public void sendSignUpConfirmEmail(Member member) {
        member.generateEmailCheckToken();
        Context context = new Context();
        context.setVariable("link", "/check-email-token?token=" + member.getEmailCheckToken() +
                "&email=" + member.getEmail());
        context.setVariable("nickname", member.getNickname());
        context.setVariable("linkName", "로드맵 이메일 인증하기");
        context.setVariable("message", "로그인 하려면 아래 링크를 클릭하세요.");
        context.setVariable("host",appProperties.getHost());
        String message = templateEngine.process("mail/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(member.getEmail())
                .subject("로드맵, 로그인 링크")
                .message(message)
                .build();
        //TODO 나중에 추가 emailService.sendEmail(emailMessage);
        log.info("email send signup confirm Email link : "+context.getVariable("link"));
    }

    public void completeSignUp(Member member) {
        member.completeSignUp();
        login(member);
    }

    public void updateProfile(Member member, ProfileForm profileForm) {
        modelMapper.map(profileForm,member);
        memberRepository.save(member);
    }

    public void updatePassword(Member member, String password) {
        member.setPassword(passwordEncoder.encode(password));
        memberRepository.save(member);
    }

    public void updateNotification(Member member, NotificationForm notificationForm) {
        modelMapper.map(notificationForm,member);
        memberRepository.save(member);
    }

    public Set<Tag> getTags(Member member) {
        Optional<Member> byId = memberRepository.findById(member.getId());
        return byId.orElseThrow().getTags();
    }

    public void removeTag(Member member, Tag tag){
        Optional<Member> byId = memberRepository.findById(member.getId());
        byId.ifPresent(a -> a.getTags().remove(tag));
    }
    public void addTag(Member member, Tag tag){
        Optional<Member> byId = memberRepository.findById(member.getId());
        byId.ifPresent(a -> a.getTags().add(tag));
    }

    public void updateLocation(Member member, LocationForm locationForm){
        Member withLoc = memberRepository.findWithLocByNickname(member.getNickname());
        withLoc.setLocation(modelMapper.map(locationForm,Location.class));
        memberRepository.save(withLoc);
    }
}
