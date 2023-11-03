package com.mayee;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    private static final long serialVersionUID = 4206232417572581858L;

    private Integer id;
    private String name;
    private Integer age;
}
