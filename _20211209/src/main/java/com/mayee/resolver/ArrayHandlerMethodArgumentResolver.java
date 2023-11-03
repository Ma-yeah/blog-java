package com.mayee.resolver;

import com.alibaba.fastjson.JSON;
import com.mayee.annotions.ArrayResolver;
import com.mayee.utils.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.CollectionFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.*;

/**
 * @program: daimao
 * @description:
 * @author: Bobby.Ma
 * @create: 2021-12-16 11:14
 **/
public class ArrayHandlerMethodArgumentResolver extends AbstractCustomizeResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ArrayResolver.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object obj = BeanUtils.instantiateClass(parameter.getParameterType());
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(obj);
        Iterator<String> parameterNames = webRequest.getParameterNames();
        while (parameterNames.hasNext()) {
            // name 从 request 中获取，可能是下划线格式
            String name = parameterNames.next();
            // 下划线转驼峰
            String camelName = underLineToCamel(name);
            Class<?> propertyType = wrapper.getPropertyType(camelName);
            // 传参在对象中不存在时
            if (Objects.isNull(propertyType)) {
                continue;
            }
            // 属性值(从请求中获取)
            Object o = webRequest.getParameter(name);
            if (Objects.nonNull(o)) {
                // 数组
                if (propertyType.isArray()) {
                    wrapper.setPropertyValue(camelName, value2Array(o));
                } else if (Collection.class.isAssignableFrom(propertyType)) {// 集合
                    wrapper.setPropertyValue(camelName, array2Collection(propertyType, value2Array(o)));
                } else {//其他类型
                    wrapper.setPropertyValue(camelName, o);
                }
            }
        }
        // 参数校验
        valid(parameter, mavContainer, webRequest, binderFactory, obj);
        // 判断是否需要参数校验
//        if (parameter.hasParameterAnnotation(Valid.class) || parameter.hasParameterAnnotation(Validated.class)) {
//            valid(obj);
//        }
        return obj;
    }

    /**
     * @param o ["a","b","c"]  或  a,b,c 格式
     * @Description: 将字符串值格式化返回为数组
     * @return: java.lang.Object[]
     * @Author: Bobby.Ma
     * @Date: 2021/12/17 16:47
     */
    private Object[] value2Array(Object o) {
        assert o != null;
        // ["a","b","c"] 格式
        if (StringUtils.containsAny(o.toString(), "[", "]")) {
            return JSON.parseArray(o.toString()).toArray();
        } else {
            // a,b,c 格式
            return JSON.parseArray(Arrays.toString(o.toString().split(","))).toArray();
        }
    }

    /**
     * @param propertyType
     * @param array
     * @Description: 数组转集合
     * @return: java.util.Collection<java.lang.Object>
     * @Author: Bobby.Ma
     * @Date: 2021/12/17 17:09
     */
    private Collection<Object> array2Collection(Class<?> propertyType, Object[] array) {
        // 这里可以不用判断
        /*
        // 如果是接口则给默认实现类
        if (propertyType.isInterface()) {
            if (List.class.isAssignableFrom(propertyType)) {
                propertyType = ArrayList.class;
            } else if (Set.class.isAssignableFrom(propertyType)) {
                propertyType = HashSet.class;
            }
        }
         */
        Collection<Object> collection = CollectionFactory.createCollection(propertyType, array.length);
        Collections.addAll(collection, array);
        return collection;
    }

    /**
     * @param obj 参数对象
     * @Description: 由于参数解析后，原参数校验不生效了，故在此校验参数
     * @return: void
     * @Author: Bobby.Ma
     * @Date: 2021/12/17 0:26
     */
    private void valid(Object obj) {
        // 从容器中获取
        Validator validator = SpringUtil.getBean(Validator.class);
        Set<ConstraintViolation<Object>> validate = validator.validate(obj);
        if (validate.size() > 0) {
            throw new ConstraintViolationException(validate);
        }
    }
}


