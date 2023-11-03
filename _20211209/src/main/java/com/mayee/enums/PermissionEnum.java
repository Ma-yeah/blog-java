package com.mayee.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PermissionEnum implements ValidateAble {
    ADMIN("admin"),
    STAFF("staff"),
    BOSS("boss");

    private final String value;

    @Override
    public Object getValidateValue() {
        return this.value;
    }
}
