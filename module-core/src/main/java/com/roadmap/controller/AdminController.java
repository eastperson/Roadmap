package com.roadmap.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roadmap.model.Member;
import com.roadmap.model.Tag;
import com.roadmap.repository.MemberRepository;
import com.roadmap.repository.TagRepository;
import com.roadmap.service.MemberService;
import com.roadmap.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.model.IModel;

import javax.persistence.Column;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Log4j2
public class AdminController {

    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final MemberService memberService;

    @GetMapping("")
    public String index(Model model){

        Long count = memberRepository.count();
        model.addAttribute("count",count);

        return "admin/index";
    }

    @GetMapping("/member")
    public String memberList(Model model){

        List<Member> memberList = memberRepository.findAll();
        model.addAttribute("memberList",memberList);

        return "admin/member/list";
    }

    @GetMapping("/member/{id}")
    public String memberView(@PathVariable Long id, Model model) throws JsonProcessingException {
        Optional<Member> result = memberRepository.findById(id);

        if(result.isPresent()) {
            Member member = result.get();

            model.addAttribute(member);

            Set<Tag> tags = memberService.getTags(member);
            model.addAttribute("tags",tags.stream().map(Tag::getTitle).collect(Collectors.toList()));
            List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
            model.addAttribute("whitelist",objectMapper.writeValueAsString(allTags));
        }

        return "admin/member/view";
    }

    @GetMapping("/member_admin")
    public String adminMemberList(Model model){

        List<Member> memberList = memberRepository.findAll();
        model.addAttribute("memberList",memberList);

        return "admin/member/list";
    }


}
