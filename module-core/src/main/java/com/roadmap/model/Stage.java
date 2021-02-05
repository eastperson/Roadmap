package com.roadmap.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @ToString(exclude = {"roadmap","nodeList"})
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Stage extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Roadmap roadmap;

    @Column(nullable = false)
    private String title;

    private int ord;

    private boolean complete;

    private boolean head;

    private boolean tail;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Node> nodeList = new ArrayList<>();

}
