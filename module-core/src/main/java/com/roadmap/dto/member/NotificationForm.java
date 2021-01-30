package com.roadmap.dto.member;

import lombok.Data;

@Data
public class NotificationForm {

    private boolean roadmapCreatedByEmail;
    private boolean roadmapCreatedByWeb;
    private boolean roadmapEnrollmentResultByEmail;
    private boolean roadmapEnrollmentResultByWeb;
    private boolean roadmapUpdatedByEmail;
    private boolean roadmapUpdatedByWeb;
}
