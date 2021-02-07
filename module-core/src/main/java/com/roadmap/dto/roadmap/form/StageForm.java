package com.roadmap.dto.roadmap.form;

import com.roadmap.model.Node;
import com.roadmap.model.Roadmap;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
public class StageForm {

    @Length(max = 25)
    @NotBlank
    private String title;

}
