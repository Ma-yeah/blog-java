package com.mayee.config;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.web.bind.ServletRequestDataBinder;

import javax.servlet.ServletRequest;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomServletRequestDataBinder extends ServletRequestDataBinder {
    public CustomServletRequestDataBinder(Object target) {
        super(target);
    }

    private final static Pattern UNDERLINE_PATTERN = Pattern.compile("_(\\w)");

    /**
     * 遍历请求参数对象 把请求参数的名转换成驼峰体
     * 重写addBindValues绑定数值的方法
     *
     * @param mpvs    请求参数列表
     * @param request 请求
     */
    @Override
    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
        List<PropertyValue> pvs = mpvs.getPropertyValueList();
        List<PropertyValue> adds = new LinkedList<>();
        for (PropertyValue pv : pvs) {
            String name = pv.getName();
            String camel = this.underLineToCamel(name);
            if (!name.equals(camel)) {
                adds.add(new PropertyValue(camel, pv.getValue()));
            }
        }
        pvs.addAll(adds);
    }

    /**
     * 下划线转驼峰
     *
     * @param value 要转换的下划线字符串
     * @return 驼峰体字符串
     */
    private String underLineToCamel(final String value) {
        final StringBuffer sb = new StringBuffer();
        Matcher m = UNDERLINE_PATTERN.matcher(value);
        while (m.find()) {
            m.appendReplacement(sb, m.group(1).toUpperCase());
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
