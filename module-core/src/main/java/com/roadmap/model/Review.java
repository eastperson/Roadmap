package com.roadmap.model;

import lombok.*;

import javax.persistence.*;

@Entity @ToString(exclude = {"roadmap","member"})
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Review extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Roadmap roadmap;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private int rate;
    private String text;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;
}
