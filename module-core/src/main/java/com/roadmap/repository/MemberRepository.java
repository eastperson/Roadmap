package com.roadmap.repository;

import com.roadmap.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Member findByEmail(String emailOrNickname);

    Member findByNickname(String emailOrNickname);
}
