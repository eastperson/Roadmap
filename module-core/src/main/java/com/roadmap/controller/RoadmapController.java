package com.roadmap.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roadmap.config.AppProperties;
import com.roadmap.dto.member.CurrentUser;
import com.roadmap.dto.member.form.TagForm;
import com.roadmap.dto.roadmap.form.PostForm;
import com.roadmap.dto.roadmap.form.RoadmapForm;
import com.roadmap.model.Member;
import com.roadmap.model.Node;
import com.roadmap.model.Post;
import com.roadmap.model.Roadmap;
import com.roadmap.model.Stage;
import com.roadmap.model.Tag;
import com.roadmap.repository.NodeRepository;
import com.roadmap.repository.PostRepository;
import com.roadmap.repository.RoadmapRepository;
import com.roadmap.repository.TagRepository;
import com.roadmap.service.PostService;
import com.roadmap.service.RoadmapService;
import com.roadmap.service.TagService;
import com.roadmap.validation.RoadmapFormValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Log4j2
public class RoadmapController {

    private final ModelMapper modelMapper;
    private final RoadmapService roadmapService;
    private final RoadmapRepository roadmapRepository;
    private final RoadmapFormValidator roadmapFormValidator;
    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;
    private final TagRepository tagRepository;
    private final TagService tagService;
    private final NodeRepository nodeRepository;
    private final PostService postService;
    private final PostRepository postRepository;

    @InitBinder("roadmapForm")
    public void roadmapInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(roadmapFormValidator);
    }

    @GetMapping("/new-roadmap")
    public String newRoadmapForm(@CurrentUser Member member, Model model) {

        model.addAttribute(member);
        model.addAttribute(new RoadmapForm());

        return "roadmap/form";
    }

    @PostMapping("/new-roadmap")
    public String newRoadmapSubmit(@CurrentUser Member member, @Valid RoadmapForm roadmapForm, Errors errors, Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {

            model.addAttribute(member);
            return "roadmap/form";
        }

        Roadmap roadmap = roadmapService.registerForm(member, roadmapForm);
        attributes.addFlashAttribute("message", roadmap.getTitle() + " 로드맵이 등록되었습니다.");

        return "redirect:/roadmap/" + URLEncoder.encode(roadmap.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/roadmap/{path}")
    public String viewRoadmap(@CurrentUser Member member, @PathVariable String path, Model model) {
        Roadmap roadmap = roadmapRepository.findByPath(path);
        model.addAttribute(member);
        model.addAttribute(roadmap);
        return "roadmap/view";

    }

    @GetMapping("/roadmap/{path}/members")
    public String viewMember(@CurrentUser Member member, @PathVariable String path, Model model) {
        Roadmap roadmap = roadmapRepository.findWithMembersByPath(path);
        model.addAttribute(member);
        model.addAttribute(roadmap);

        return "roadmap/member";
    }

    @GetMapping({"/roadmap/{path}/map"})
    public String viewMap(@CurrentUser Member member, @PathVariable String path, Model model) throws JsonProcessingException {
        Roadmap roadmap = roadmapRepository.findWithAllByPath(path);
        model.addAttribute(roadmap);
        // TODO Quertdsl로 튜닝 필요
        roadmap.getStageList().stream().sorted((s1, s2) -> (int) (s2.getOrd() - s1.getOrd()))
                .collect(Collectors.toList())
                .forEach(stage -> {
                    stage.setRoadmap(null);
                    stage.getNodeList().stream().forEach(node -> {
                        node.setStage(null);
                        node.setParent(null);
                        recursionNode(node);
                    });
                });

        model.addAttribute(member);
        model.addAttribute("stageList", objectMapper.writeValueAsString(roadmap.getStageList()));
        model.addAttribute("roadmapAppKey", appProperties.getRoadmapApiKey());
        model.addAttribute("host", appProperties.getHost());

        return "roadmap/map";
    }

    public static void recursionNode(Node node) {
        if (node.getChilds() != null) {
            node.getChilds().stream().forEach(n -> {
                n.setParent(null);
                recursionNode(n);
            });
        }
        return;
    }

    @GetMapping("/roadmap/{path}/join")
    public String joinRoadmap(@CurrentUser Member member, @PathVariable String path) {
        Roadmap roadmap = roadmapRepository.findWithMembersByPath(path);
        roadmapService.addMember(roadmap, member);
        return "redirect:/roadmap/" + roadmap.getEncodedPath() + "/members";

    }

    @GetMapping("/roadmap/{path}/leave")
    public String leaveRoadmap(@CurrentUser Member member, @PathVariable String path) {
        Roadmap roadmap = roadmapRepository.findWithMembersByPath(path);
        roadmapService.removeMember(roadmap, member);
        return "redirect:/roadmap/" + roadmap.getEncodedPath() + "/members";

    }

    @GetMapping("/new-post/{nodeId}")
    public String newPostForm(@CurrentUser Member member, Model model, @PathVariable("nodeId") Long nodeId) throws JsonProcessingException {
        Post post = postRepository.findByNodeId(nodeId);
        if (post == null) {
            postService.createNewPost(nodeId);
        }

        model.addAttribute("nodeId", nodeId);
        model.addAttribute(member);
        model.addAttribute(new PostForm());
        List<String> allTags = tagRepository.findAll().stream().map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTags));

        return "roadmap/post/form";
    }

    @PostMapping("/new-post/{nodeId}")
    public String newPostFormSubmit(@CurrentUser Member member, @Valid PostForm postForm, Errors errors, Model model, @PathVariable("nodeId") Long nodeId) throws JsonProcessingException {

        model.addAttribute("nodeId", nodeId);
        model.addAttribute(member);
        if (errors.hasErrors()) {
            model.addAttribute(postForm);
            return "roadmap/post/form";
        }
        Post post = postRepository.findByNodeId(nodeId);
        Post newPost = postService.update(post, postForm);

        return "redirect:/post/" + newPost.getId();
    }

    @PostMapping("/new-post/{nodeId}/tags/add")
    public ResponseEntity RoadmapAddTag(@CurrentUser Member member, @PathVariable String path, @RequestBody TagForm tagForm, Model model, Long nodeId) {
        String title = tagForm.getTagTitle();

        Tag tag = tagService.findOrCreateNew(title);

        Post post = postRepository.findByNodeId(nodeId);

        postService.addTag(post, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/new-post/{nodeId}/tags/remove")
    public ResponseEntity roadmapRemoveTag(@CurrentUser Member member, @PathVariable String path, @RequestBody TagForm tagForm, Model model, Long nodeId) {

        String title = tagForm.getTagTitle();

        Tag tag = tagRepository.findByTitle(title);
        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }

        Post post = postRepository.findByNodeId(nodeId);

        postService.removeTag(post, tag);
        return ResponseEntity.ok().build();


    }
}
