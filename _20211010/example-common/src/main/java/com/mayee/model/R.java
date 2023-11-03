package com.mayee.model;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class R {

    private final int code;
    private Object data;
    private boolean success;
    private String message;

    private R(int code) {
        this.code = code;
        this.success = code == 0;
    }

    public static R data(int code, Object data) {
        R r = new R(code);
        r.setData(data);
        return r;
    }

    public static R data(Object data) {
        R r = new R(0);
        r.setData(data);
        return r;
    }

    public static R success() {
        return new R(0);
    }

    public static R fail(int code) {
        return new R(code);
    }

    public static R message(int code, String message) {
        R r = new R(code);
        r.setMessage(message);
        return r;
    }

    private int getCode() {
        return this.code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        return JSON.toJSONString(this);
    }
}
