package com.mayee.model.extend;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mayee.annotions.MatchAny;
import com.mayee.enums.LessonEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * @program: blog-example
 * @description:
 * @author: Bobby.Ma
 * @create: 2021-12-14 15:53
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Teacher extends Person {

    /**
     * 工号
     */
    private Integer teaId;

    private String name;

    @Min(28)
    private Integer age;

    /**
     * 教授课程
     */
    @MatchAny(enumClass = LessonEnum.class)
    private List<String> lessons;
}
