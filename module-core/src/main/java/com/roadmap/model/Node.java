package com.roadmap.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @ToString(exclude = {"post","noteList","stage","parent"})
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Node extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Stage stage;

    @OneToOne(cascade = CascadeType.ALL)
    private Node parent;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Node> childs = new ArrayList<>();

    private String nodeType = NodeType.TEXT.toString();

    private String title;

    private String shortDescription;

    private String url;

    private boolean complete;

    private boolean read;

    private String text;

}
