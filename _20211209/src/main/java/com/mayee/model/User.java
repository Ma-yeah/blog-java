package com.mayee.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mayee.annotions.MatchAny;
import com.mayee.enums.PermissionEnum;
import com.mayee.validator.UserGroupSequenceProvider;
import com.mayee.validator.groups.ValidAddGroup;
import com.mayee.validator.groups.ValidLearningGroup;
import com.mayee.validator.groups.ValidUpdateGroup;
import com.mayee.validator.groups.ValidWorkingGroup;
import com.mayee.validator.sequence.EmailSeq;
import com.mayee.validator.sequence.PhoneSeq;
import lombok.Data;
import org.hibernate.validator.group.GroupSequenceProvider;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.Serializable;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@GroupSequenceProvider(UserGroupSequenceProvider.class)
public class User implements Serializable {
    private static final long serialVersionUID = -7242397021777229674L;

    @Null(groups = {ValidAddGroup.class})
    @NotNull(groups = {ValidUpdateGroup.class})
    private Integer id;

    @NotEmpty(groups = {ValidAddGroup.class, ValidUpdateGroup.class})
    private String name;
    @Min(1)
    private Integer age;
    @Email(groups = {EmailSeq.class})
    private String email;
    @Pattern(regexp = "[1](([3][0-9])|([4][5-9])|([5][0-3,5-9])|([6][5,6])|([7][0-8])|([8][0-9])|([9][1,8,9]))[0-9]{8}", groups = {PhoneSeq.class})
    private String phone;

    @MatchAny(enumClass = PermissionEnum.class, groups = {ValidAddGroup.class, ValidUpdateGroup.class})
    private String permission;

    @MatchAny(strValues = {"language", "math", "english"}, groups = {ValidLearningGroup.class})
    private String learning;

    @MatchAny(strValues = {"screw", "brick", "coding"}, groups = {ValidWorkingGroup.class})
    private String working;

    @Valid
    private Information information;
}
