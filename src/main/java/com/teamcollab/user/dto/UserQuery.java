package com.teamcollab.user.dto;

import lombok.Data;

@Data
public class UserQuery {

    private String keyword;

    private Integer status;

    private String roleCode;

    private long page = 1;

    private long size = 10;
}
