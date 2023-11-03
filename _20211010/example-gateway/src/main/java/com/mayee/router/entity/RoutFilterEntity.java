package com.mayee.router.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class RoutFilterEntity implements Serializable {
    private static final long serialVersionUID = -1877899804045021045L;
    private String name;
    private Map<String, String> args = new LinkedHashMap<>();
}
