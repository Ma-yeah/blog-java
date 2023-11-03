package com.mayee.bean;

import lombok.Data;

@Data
public class R {
    private int code;
    private Object data;
    private String msg;

    private R() {
    }

    private R(int code, Object data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static R success() {
        return new R();
    }

    public static R fail(String msg) {
        return new R(0, null, msg);
    }

    public static R data(Object data) {
        return new R(0, data, null);
    }
}
