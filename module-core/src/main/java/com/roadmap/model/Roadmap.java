package com.roadmap.model;

import com.roadmap.dto.member.AuthMemberDTO;
import lombok.*;
import org.apache.tomcat.jni.User;
import org.springframework.security.core.parameters.P;

import javax.persistence.*;
import javax.validation.constraints.Null;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@NamedEntityGraph(name = "Roadmap.withMembers", attributeNodes = {
        @NamedAttributeNode("members")
})
@NamedEntityGraph(name = "Roadmap.withAll", attributeNodes = {
        @NamedAttributeNode("members"),
        @NamedAttributeNode("stageList"),
        @NamedAttributeNode("likeMembers")
})
@Entity @ToString(exclude = {"members","tags","likeMembers","stageList"})
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Roadmap extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false)
    private String path;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;

    private boolean published;
    private LocalDateTime publishedDateTime;

    private boolean opened;
    private LocalDateTime openDateTime;

    private boolean recruiting;
    private LocalDateTime recruitingUpdatedDateTime;

    @Column(nullable = false)
    private String title;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    private String shortDescription;
    private boolean useBanner;
    private Double progress;
    private boolean complete;
    private LocalDateTime milestone;

    @OneToOne(fetch = FetchType.EAGER)
    private Member owner;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Member> members = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY,mappedBy = "likeRoadmaps")
    private Set<Member> likeMembers = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Stage> stageList = new ArrayList<>();

    public boolean isJoinable(UserMember userMember){
        Member member = userMember.getMember();
        return this.isPublished() && this.isRecruiting() && !this.members.contains(member);
    }

    public boolean isJoinable(AuthMemberDTO authMemberDTO){
        Member member = authMemberDTO.getMember();
        return this.isPublished() && this.isRecruiting() && !this.members.contains(member);
    }

    public boolean isMember(UserMember userMember) {
        return this.members.contains(userMember.getMember());
    }

    public boolean isMember(AuthMemberDTO authMemberDTO) {
        return this.members.contains(authMemberDTO.getMember());
    }

    public boolean isOwner(UserMember userMember) {
        return this.owner.equals(userMember.getMember());
    }

    public boolean isOwner(AuthMemberDTO authMemberDTO) {
        return this.owner.equals(authMemberDTO.getMember());
    }

    public void addMember(Member member) {
        this.getMembers().add(member);
    }

    public String getImage(){
        return image != null ? image : "/images/logo.png";
    }

    public void publish(){
        if(this.opened && !this.published) {
            this.published = true;
            this.publishedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("로드맵를 공개할 수 없는 상태입니다. 로드맵를 이미 공개했거나 종료했습니다.");
        }
    }

    public void open(){
        if(this.published && !this.opened){
            opened = true;
            this.openDateTime = LocalDateTime.now();
        } else {
            throw  new RuntimeException("로드맵을 종료할 수 없습니다. 로드맵을 공개하지 않았거나 이미 종료된 로드맵입니다.");
        }
    }

    public void startRecruit() {
        if (canUpdateRecruiting()) {
            this.recruiting = true;
            this.recruitingUpdatedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("인원 모집을 시작할 수 없습니다. 로드맵를 공개하거나 한 시간 뒤 다시 시도하세요.");
        }
    }

    public void stopRecruit() {
        if (canUpdateRecruiting()) {
            this.recruiting = false;
            this.recruitingUpdatedDateTime = LocalDateTime.now();
        } else {
            throw new RuntimeException("인원 모집을 멈출 수 없습니다. 로드맵를 공개하거나 한 시간 뒤 다시 시도하세요.");
        }
    }

    private boolean canUpdateRecruiting() {
        return this.published && this.recruitingUpdatedDateTime == null || this.recruitingUpdatedDateTime.isBefore(LocalDateTime.now().minusHours(1));
    }

    public String getEncodedPath() {
        return URLEncoder.encode(this.path, StandardCharsets.UTF_8);
    }

    public void removeMemeber(Member member) {
        this.getMembers().remove(member);
    }

}
