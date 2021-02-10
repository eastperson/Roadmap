package com.roadmap.dto.roadmap.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

@Data
public class NodeModalForm {

    @NotBlank
    private Long id;

    @NotBlank
    private Long parentId;

    @NotBlank
    private Long parentType;

    @NotBlank
    private String nodeType;

    @NotBlank @Length(max = 25)
    private String title;

    @Length(max = 50)
    private String shortDescription;

    private String url;

    private boolean complete;

    private boolean read;

    private String text;
}
