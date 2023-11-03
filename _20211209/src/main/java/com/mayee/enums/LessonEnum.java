package com.mayee.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @program: blog-example
 * @description:
 * @author: Bobby.Ma
 * @create: 2021-12-15 14:56
 **/
@Getter
@AllArgsConstructor
public enum LessonEnum implements ValidateAble {
    LANGUAGE("language"),
    MATH("math"),
    ENGLISH("english");

    private final String value;

    @Override
    public Object getValidateValue() {
        return this.value;
    }
}
