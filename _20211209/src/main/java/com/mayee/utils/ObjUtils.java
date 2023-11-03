package com.mayee.utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;

import java.util.Objects;

@UtilityClass
public class ObjUtils {

    private static final Gson GSON;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        // 下划线格式
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        GSON = gsonBuilder.serializeNulls().create();
    }

    public String toJsonStr(Object entity) {
        if (Objects.isNull(entity)) {
            return null;
        }
        return GSON.toJson(entity);
    }
}
