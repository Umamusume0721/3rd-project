package com.teamcollab.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_permission")
public class Permission {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 权限点编码，规范：模块:动作 */
    private String code;

    private String name;

    /** MENU / ACTION */
    private String type;

    private Long parentId;

    private Integer sort;

    private LocalDateTime createdAt;
}
