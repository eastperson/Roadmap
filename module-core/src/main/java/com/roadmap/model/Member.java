package com.roadmap.model;

import lombok.*;
import org.apache.tomcat.jni.Local;
import org.hibernate.annotations.Fetch;
import org.springframework.data.util.Lazy;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
@Entity @ToString(exclude = {"roleSet","location","tags"})
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
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

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    private boolean fromSocial;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "loc_id")
    private Location location;

    @ElementCollection
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Set<MemberRole> roleSet = new HashSet<>();

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
}
