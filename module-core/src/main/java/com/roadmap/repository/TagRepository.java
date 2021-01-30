package com.roadmap.repository;

import com.roadmap.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag,Long> {
    boolean existsByTitle(String title);

    Tag findByTitle(String title);
}
