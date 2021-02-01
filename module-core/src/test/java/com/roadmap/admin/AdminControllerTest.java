package com.roadmap.admin;

import com.roadmap.member.WithMember;
import com.roadmap.model.Member;
import com.roadmap.model.MemberRole;
import com.roadmap.repository.MemberRepository;
import com.roadmap.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private MemberRepository memberRepository;
    @Autowired private MemberService memberService;

    private final static String NICKNAME = "epepep";

    @DisplayName("어드민 대시보드")
    @Test
    @WithMember(NICKNAME)
    @Transactional
    void dashboard() throws Exception {
        mockMvc.perform(get("/admin").with(user("epepep").roles("ADMIN")))
                .andExpect(model().attributeExists("count"))
                .andExpect(view().name("admin/index"))
                .andExpect(authenticated());
    }

    @DisplayName("어드민 회원 관리 리스트 화면")
    @Test
    @WithMember(NICKNAME)
    @Transactional
    void admin_memberListView() throws Exception {
        mockMvc.perform(get("/admin/member").with(user("epepep").roles("ADMIN")))
                .andExpect(model().attributeExists("memberList"))
                .andExpect(view().name("admin/member/list"))
                .andExpect(authenticated());
    }

    @DisplayName("어드민 회원 관리 정보 화면")
    @Test
    @WithMember(NICKNAME)
    @Transactional
    void admin_memberView() throws Exception {

        Member member = memberRepository.findByNickname("epepep");

        mockMvc.perform(get("/admin/member/" + member.getId()).with(user("epepep").roles("ADMIN")))
                .andExpect(model().attributeExists("member"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attributeExists("whitelist"))
                .andExpect(view().name("admin/member/view"))
                .andExpect(authenticated());
    }

}
