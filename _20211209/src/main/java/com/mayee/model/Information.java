package com.mayee.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class Information implements Serializable {
    private static final long serialVersionUID = -3128793162502773246L;

    @NotEmpty
    private String address;

    private String telPhone;
}
