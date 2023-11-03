package com.mayee.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class R {

    private final int code;
    private Object data;
    private boolean success;
    private String message;

    private R(int code) {
        this.code = code;
        this.success = code == 0;
        this.message = this.success ? "success" : "fail";
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

    public static R fail() {
        return fail("unknown error");
    }

    public static R fail(int code) {
        return new R(code);
    }

    public static R fail(String message) {
        return message(500, message);
    }

    public static R message(int code, String message) {
        R r = new R(code);
        r.setMessage(message);
        return r;
    }
}
