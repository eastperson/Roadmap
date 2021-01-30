package com.roadmap.repository;

import com.roadmap.model.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Member findByEmail(String emailOrNickname);

    Member findByNickname(String emailOrNickname);

    // LOAD는 엔티티 그래프에 명시한 연관관계는 Eager모드로 가져오고, 나머지 attribute는 기본 fetch 타입(one to one, many to one 은 eager, many로 끝나는 타입은 lazy로 가져온다.)
    @EntityGraph(value = "Member.withAll", type = EntityGraph.EntityGraphType.LOAD)
    Member findWithAllByNickname(String nickname);

    // JPA는 withTags라는 무의미한 단어이다.그래서 다른 엔티티 그래프를 사용한다.
    @EntityGraph(value = "Member.withLoc", type = EntityGraph.EntityGraphType.FETCH)
    Member findWithLocByNickname(String nickname);
}
