package com.mayee.annotions;

import com.mayee.enums.EmptyValidateEnum;
import com.mayee.enums.ValidateAble;
import com.mayee.validator.MatchAnyValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {MatchAnyValidator.class})
@Repeatable(MatchAny.List.class)// 使注解支持重复定义
public @interface MatchAny {

    String message() default "not match any one";

    String[] strValues() default {};

    int[] intValues() default {};

    Class<? extends ValidateAble> enumClass() default EmptyValidateEnum.class;

    //分组
    Class<?>[] groups() default {};

    //负载
    Class<? extends Payload>[] payload() default {};

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        MatchAny[] value();
    }
}
