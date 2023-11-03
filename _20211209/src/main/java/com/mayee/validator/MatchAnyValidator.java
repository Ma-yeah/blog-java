package com.mayee.validator;

import com.mayee.annotions.MatchAny;
import com.mayee.enums.EmptyValidateEnum;
import com.mayee.enums.ValidateAble;
import org.apache.commons.lang3.ArrayUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Description: 自定义注解校验逻辑
 * @Author: Bobby.Ma
 * @Date: 2021/12/11 1:41
 */
// 该类在 Spring 容器启动时即加载，无需添加 @Component 注解
public class MatchAnyValidator implements ConstraintValidator<MatchAny, Object> {
    private String[] strValues;
    private int[] intValues;
    private Class<? extends ValidateAble> clazz;

    @Override
    public void initialize(MatchAny constraintAnnotation) {
        strValues = constraintAnnotation.strValues();
        intValues = constraintAnnotation.intValues();
        clazz = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) {
            return true;
        }
        boolean pass;
        // 集合
        if (value instanceof Collection) {
            pass = ((Collection<?>) value).stream().allMatch(this::validValue);
            // 数组
        } else if (value.getClass().isArray()) {
            pass = Arrays.stream(((Object[]) value)).allMatch(this::validValue);
            // 单值
        } else {
            pass = validValue(value);
        }
        if (!pass) {
            // 禁用默认的错误提示
            context.disableDefaultConstraintViolation();
            String valueStr = getValueStr();
            context.buildConstraintViolationWithTemplate(String.format("must be one of [%s]", valueStr)).addConstraintViolation();
            return false;
        }
        return true;
    }

    /**
     * @param value
     * @Description: 校验单个值，值可能是数字或字符串
     * @return: boolean
     * @Author: Bobby.Ma
     * @Date: 2021/12/15 15:06
     */
    private boolean validValue(Object value) {
        if (clazz != EmptyValidateEnum.class) {
            return Arrays.stream(clazz.getEnumConstants()).map(ValidateAble::getValidateValue).anyMatch(o -> Objects.equals(o, value));
        } else if (value instanceof String) {
            return Arrays.asList(strValues).contains(value);
        } else if (value instanceof Integer) {
            return IntStream.of(intValues).anyMatch(i -> Objects.equals(i, value));
        } else {
            // 只支持校验 数字或字符串
            return false;
        }
    }

    private String getValueStr() {
        if (clazz.isEnum() && clazz != EmptyValidateEnum.class) {
            ValidateAble[] validateAbles = clazz.getEnumConstants();
            return Arrays.stream(validateAbles).map(ValidateAble::getValidateValue).map(Object::toString).collect(Collectors.joining(","));
        } else {
            if (ArrayUtils.isNotEmpty(strValues)) {
                return String.join(",", strValues);
            } else {
                return Arrays.stream(intValues).mapToObj(Objects::toString).collect(Collectors.joining(","));
            }
        }
    }
}
