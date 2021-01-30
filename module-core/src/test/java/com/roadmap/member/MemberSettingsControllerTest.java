package com.roadmap.member;

import com.roadmap.model.Member;
import com.roadmap.repository.MemberRepository;
import com.roadmap.service.EmailService;
import com.roadmap.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class MemberSettingsControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private MemberRepository memberRepository;
    @Autowired private MemberService memberService;
    @Autowired private ModelMapper modelMapper;


    @DisplayName("프로필 화면")
    @WithMember("epepep")
    @Test
    void profileView() throws Exception {
        Member member = memberRepository.findByNickname("epepep");

        mockMvc.perform(get("/profile/" + member.getNickname()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("isOwner"))
                .andExpect(model().attributeExists("member"))
                .andExpect(view().name("member/profile"));

    }

    @DisplayName("프로필 화면 - 에러(없는 회원)")
    @WithMember("epepep")
    @Test
    void profileView_error() throws Exception {
        Member member = memberRepository.findByNickname("epepep");

        mockMvc.perform(get("/profile/" + member.getNickname() + "error"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("member/profile"));

    }

    @DisplayName("프로필 수정 화면")
    @WithMember("epepep")
    @Test
    void profileUpdateView() throws Exception {

        mockMvc.perform(get("/settings/profile"))
                .andExpect(model().attributeExists("profileForm"))
                .andExpect(model().attributeExists("member"))
                .andExpect(view().name("member/settings/profile"));

    }

    @DisplayName("프로필 수정 - 입력값 정상")
    @WithMember("epepep")
    @Test
    void profileUpdate_submit_correct() throws Exception {
        mockMvc.perform(post("/settings/profile")
                .param("bio","자기소개")
                .param("profileImage","hththththththt")
                .param("occupation","백엔드개발자")
                .param("url","htttp://dsadasdasda")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attributeExists("message"));

        Member member = memberRepository.findWithLocByNickname("epepep");
        assertTrue(member.getBio().equals("자기소개"));
        assertTrue(member.getProfileImage().equals("hththththththt"));
        assertTrue(member.getOccupation().equals("백엔드개발자"));
        assertTrue(member.getUrl().equals("htttp://dsadasdasda"));

    }

    @DisplayName("프로필 수정 - 입력값 에러")
    @WithMember("epepep")
    @Test
    void profileUpdate_submit_error() throws Exception {
        mockMvc.perform(post("/settings/profile")
                .param("bio","자기소개dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd")
                .param("profileImage","hththththththt")
                .param("occupation","백엔드개발자")
                .param("url","htttp://dsadasdasda")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("member"))
                .andExpect(model().attributeExists("profileForm"))
                .andExpect(model().hasErrors())
                .andExpect(view().name("member/settings/profile"));

        Member member = memberRepository.findByNickname("epepep");
        assertFalse("자기소개".equals(member.getBio()));
        assertFalse("hththththththt".equals(member.getProfileImage()));
        assertFalse("백엔드개발자".equals(member.getOccupation()));
        assertFalse("htttp://dsadasdasda".equals(member.getUrl()));

    }

}
