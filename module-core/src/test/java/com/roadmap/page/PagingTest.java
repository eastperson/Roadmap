package com.roadmap.page;

import com.roadmap.dto.member.MemberDTO;
import com.roadmap.dto.member.form.SignUpForm;
import com.roadmap.dto.page.PageRequestDTO;
import com.roadmap.dto.page.PageResultDTO;
import com.roadmap.member.WithMember;
import com.roadmap.model.Member;
import com.roadmap.service.MemberService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@Log4j2 @TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PagingTest  {

    @Autowired PasswordEncoder passwordEncoder;

    @Autowired MemberService memberService;

//    @BeforeAll
//    void createMemberList(){
//        for(int i = 0; i < 200; i++) {
//            SignUpForm signUpForm = new SignUpForm();
//            signUpForm.setNickname("nickname" + i);
//            signUpForm.setEmail(i+"@email.com");
//            signUpForm.setPassword(passwordEncoder.encode("11111111"));
//            memberService.saveNewMember(signUpForm);
//        }
//    }

    @Test
    @DisplayName("페이징 처리 테스트 - 멤버")
    void pageigng_member(){

        PageResultDTO pageResultDTO = memberService.getList(PageRequestDTO.builder().size(10).page(1).build());
        log.info(pageResultDTO);
        assertTrue(pageResultDTO.getTotalPage() == 21);
        assertTrue(pageResultDTO.getSize() == 10);
        assertTrue(pageResultDTO.getEnd() == 10);
        assertTrue(pageResultDTO.getStart() == 1);
        assertTrue(pageResultDTO.isNext());
        assertFalse(pageResultDTO.isPrev());

    }

    @Test
    @DisplayName("검색 처리 테스트 - 멤버")
    void pageigng_search_member(){

        PageResultDTO pageResultDTO = memberService.getList(PageRequestDTO.builder()
                .size(10).page(1).type("e").keyword("3").build());
        log.info(pageResultDTO);
        assertTrue(pageResultDTO.getTotalPage() == 4);
        assertTrue(pageResultDTO.getSize() == 10);
        assertTrue(pageResultDTO.getEnd() == 4);
        assertTrue(pageResultDTO.getStart() == 1);
        assertFalse(pageResultDTO.isNext());
        assertFalse(pageResultDTO.isPrev());
        assertTrue(pageResultDTO.getDtoList().size() == 10);
        pageResultDTO.getDtoList().stream().forEach(dto -> {
            MemberDTO member = (MemberDTO) dto;
            assertTrue(member.getEmail().contains("3"));
        });

    }


}
