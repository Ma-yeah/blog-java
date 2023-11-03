package com.mayee.web.result;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Description: 响应模型
 * @Author: Bobby.Ma
 * @Date: 2021/10/7 5:27
 */
@Data
@AllArgsConstructor
public class R<T> {
    // 响应码
    private final int code;
    // 是否成功
    private final boolean success;
    // 响应信息
    private String msg;
    // 响应数据
    private T data;

    public static <T> R<T> data(T data) {
        return new R<>(0, true, "success", data);
    }

    public static <T> R<T> success() {
        return new R<>(0, true, "success", null);
    }
}
