package com.roadmap.dto.admin.member;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class MemberInfoForm {

    @Length(max = 35)
    private String bio;

    @Length(max = 50)
    private String url;

    @Length(max = 50)
    private String occupation;

    private String profileImage;

    private boolean roadmapCreatedByEmail;
    private boolean roadmapCreatedByWeb;
    private boolean roadmapEnrollmentResultByEmail;
    private boolean roadmapEnrollmentResultByWeb;
    private boolean roadmapUpdatedByEmail;
    private boolean roadmapUpdatedByWeb;

    private String addr;
    private String addrDetail;
    private String siNm;
    private String sggNm;
    private String emdNm;
    private Double lat;
    private Double lng;

}
