package com.roadmap.repository;

import com.roadmap.model.Location;
import com.roadmap.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
