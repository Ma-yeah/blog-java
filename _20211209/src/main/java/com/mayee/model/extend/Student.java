package com.mayee.model.extend;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;

/**
 * @program: blog-example
 * @description:
 * @author: Bobby.Ma
 * @create: 2021-12-14 15:52
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Student extends Person {

    /**
     * 学号
     */
    private Integer stuId;

    private String name;

    @Min(7)
    private Integer age;

    /**
     * 班号
     */
    private String classNo;
}
