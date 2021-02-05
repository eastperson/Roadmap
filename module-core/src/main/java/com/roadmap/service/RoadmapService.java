package com.roadmap.service;

import com.roadmap.dto.roadmap.form.RoadmapDescriptionForm;
import com.roadmap.dto.roadmap.form.RoadmapForm;
import com.roadmap.model.Member;
import com.roadmap.model.Roadmap;
import com.roadmap.repository.MemberRepository;
import com.roadmap.repository.RoadmapRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final ModelMapper modelMapper;
    private final MemberRepository memberRepository;


    public Roadmap registerForm(Member member, RoadmapForm roadmapForm) {

        Roadmap roadmap = modelMapper.map(roadmapForm, Roadmap.class);
        Member updateMember = memberRepository.findWithRoadmapByNickname(member.getNickname());
        roadmap.setOwner(member);
        roadmap.getMembers().add(member);
        updateMember.getRoadmaps().add(roadmap);
        memberRepository.save(updateMember);
        return roadmapRepository.save(roadmap);

    }

    public void removeMember(Roadmap roadmap, Member member) {
        roadmap.removeMemeber(member);
    }

    public void addMember(Roadmap roadmap, Member member) {
        roadmap.addMember(member);
    }

    public Roadmap getRoadmapToUpdate(Member member, String path) {
        Roadmap roadmap = this.getRoadmap(path);
        checkIfOwner(member,roadmap);
        return roadmap;
    }

    private Roadmap getRoadmap(String path) {
        Roadmap roadmap = roadmapRepository.findByPath(path);
        if(roadmap == null) {
            throw new IllegalArgumentException(path + "에 해당하는 스터디가 없습니다.");
        }
        return roadmap;
    }

    private void checkIfOwner(Member member, Roadmap roadmap) {
        if(!roadmap.getOwner().equals(member)){
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }

    public void updateRoadmapDescription(Roadmap roadmap, RoadmapDescriptionForm roadmapDescriptionForm) {
        modelMapper.map(roadmapDescriptionForm,roadmap);
    }
}
