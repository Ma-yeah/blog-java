package com.mayee.enums;

public enum EmptyValidateEnum implements ValidateAble {
    ;

    @Override
    public Object getValidateValue() {
        return null;
    }
}
