package com.mayee.model.extend;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mayee.annotions.MatchAny;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @program: blog-example
 * @description:
 * @author: Bobby.Ma
 * @create: 2021-12-14 15:50
 **/
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true, defaultImpl = Person.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Teacher.class, name = "teacher"),
        @JsonSubTypes.Type(value = Student.class, name = "student")
})
public class Person {

    @NotEmpty
    @MatchAny(strValues = {"teacher", "student"})
    private String type;

    public Integer getStuId() {
        return null;
    }

    public Integer getTeaId() {
        return null;
    }

    public String getName() {
        return null;
    }

    public Integer getAge() {
        return null;
    }

    public String getClassNo() {
        return null;
    }
}
