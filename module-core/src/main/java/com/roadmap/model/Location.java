package com.roadmap.model;

import lombok.*;

import javax.persistence.*;


@Entity @ToString
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Location {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loc_id")
    Long id;

    private String addr;
    private String addrDetail;
    private String siNm;
    private String sggNm;
    private String emdNm;
    private Double lat;
    private Double lng;
}
