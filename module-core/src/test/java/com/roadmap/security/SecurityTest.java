package com.roadmap.security;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc
public class SecurityTest {

    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private MockMvc mockMvc;

    @DisplayName("패스워드 인코딩 테스트")
    @Test
    void passwordTest(){

        String password = "1111";

        String encoded = passwordEncoder.encode(password);

        assertFalse(encoded.equals(password));

        assertTrue(passwordEncoder.matches(password,encoded));

        log.info("encoded password : " + encoded);

    }

    @DisplayName("메인 페이지 화면 테스트 - 비회원")
    @Test
    void mainGetTest() throws Exception{
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(unauthenticated());
    }

    @DisplayName("로그인 페이지 화면 테스트 - 비회원")
    @Test
    void loginGetTest() throws Exception{
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(unauthenticated());
    }

}
