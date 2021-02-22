package com.roadmap.service;

import com.roadmap.dto.roadmap.form.PostForm;
import com.roadmap.model.Node;
import com.roadmap.model.Post;
import com.roadmap.model.Roadmap;
import com.roadmap.model.Tag;
import com.roadmap.repository.NodeRepository;
import com.roadmap.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final NodeRepository nodeRepository;
    private final ModelMapper modelMapper;

    public void addTag(Post post, Tag tag) {
        post.getTags().add(tag);
    }

    public void removeTag(Post post, Tag tag) {
        post.getTags().remove(tag);
    }

    public Post createNewPost(Long nodeId) {
        Post post = new Post();
        Node node = nodeRepository.findById(nodeId).orElseThrow();
        post.setNode(node);
        return postRepository.save(post);
    }

    public Post update(Post post, PostForm postForm) {
        modelMapper.map(postForm,post);
        return post;
    }
}
