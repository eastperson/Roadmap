package com.roadmap.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.roadmap.dto.member.MemberDTO;
import lombok.*;
import org.apache.tomcat.jni.Local;
import org.hibernate.annotations.Fetch;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Lazy;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@NamedEntityGraph(name = "Member.withAll", attributeNodes = {
        @NamedAttributeNode("location"),
        @NamedAttributeNode("tags")
})
@NamedEntityGraph(name = "Member.withLoc", attributeNodes = {
        @NamedAttributeNode("location")
})
@NamedEntityGraph(name = "Member.withRole", attributeNodes = {
        @NamedAttributeNode("roleSet")
})
@NamedEntityGraph(name = "Member.withRoadmap", attributeNodes = {
        @NamedAttributeNode("roadmaps")
})
@Entity @ToString(exclude = {"roleSet","location","tags","likeRoadmaps","roadmaps","profileImage"})
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Member extends BaseEntity{

    @Id @GeneratedValue
    private Long id;

    private String bio;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private String emailCheckToken;

    private LocalDateTime emailCheckTokenGeneratedAt;

    private boolean emailVerified;

    private LocalDateTime joinedAt;

    private String occupation;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private String url;

    private boolean roadmapCreatedByEmail;

    private boolean roadmapCreatedByWeb = true;

    private boolean roadmapEnrollmentResultByEmail;

    private boolean roadmapEnrollmentResultByWeb = true;

    private boolean roadmapUpdatedByEmail;

    private boolean roadmapUpdatedByWeb = true;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Roadmap> likeRoadmaps;

    private boolean fromSocial;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "loc_id")
    private Location location;

    @ElementCollection
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Set<MemberRole> roleSet = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "members")
    private List<Roadmap> roadmaps = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    public void addMemberRole(MemberRole memberRole){
        roleSet.add(memberRole);
    }

    public void generateEmailCheckToken(){
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.getEmailCheckToken().equals(token);
    }

    public void completeSignUp() {
        this.setEmailVerified(true);
        this.getRoleSet().add(MemberRole.USER);
    }

    public boolean canSendConfirmEmail() {
        return this.getEmailCheckTokenGeneratedAt().isBefore(LocalDateTime.now().minusHours(1));
    }

    public MemberDTO entityToDto(ModelMapper modelMapper) {
        return modelMapper.map(this,MemberDTO.class);
    }
}
