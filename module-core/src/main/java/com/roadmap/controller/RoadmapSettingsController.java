package com.roadmap.controller;

import com.roadmap.dto.member.CurrentUser;
import com.roadmap.dto.roadmap.form.RoadmapDescriptionForm;
import com.roadmap.dto.roadmap.form.RoadmapForm;
import com.roadmap.model.Member;
import com.roadmap.model.Roadmap;
import com.roadmap.service.RoadmapService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/roadmap/{path}/settings")
@Log4j2
public class RoadmapSettingsController {

    private final RoadmapService roadmapService;
    private final ModelMapper modelMapper;



    @GetMapping("/description")
    public String updateDescription(@CurrentUser Member member, @PathVariable String path, Model model) {

        Roadmap roadmap = roadmapService.getRoadmapToUpdate(member,path);
        model.addAttribute(member);
        model.addAttribute(roadmap);
        model.addAttribute(modelMapper.map(roadmap, RoadmapDescriptionForm.class));

        return "roadmap/settings/description";
    }

    @PostMapping("/description")
    public String updateDescriptionSubmit(@CurrentUser Member member, @PathVariable String path, @Valid RoadmapDescriptionForm roadmapDescriptionForm, Errors errors, Model model, RedirectAttributes attributes) {
        if(errors.hasErrors()){
            model.addAttribute(member);
            return "roadmap/settings/description";
        }
        Roadmap roadmap = roadmapService.getRoadmapToUpdate(member,path);
        roadmapService.updateRoadmapDescription(roadmap, roadmapDescriptionForm);
        attributes.addFlashAttribute("message","로드맵 소개를 수정했습니다.");

        return "redirect:/roadmap/"+roadmap.getEncodedPath()+"/settings/description";
    }

}
