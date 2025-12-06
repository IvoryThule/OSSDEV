package com.whu.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 宠物实体类
 */
@Data
@TableName("pet")
public class Pet {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Long categoryId;

    private String breed;

    private String age;

    /**
     * 性别: 0-未知, 1-公, 2-母
     */
    private Integer gender;

    private BigDecimal weight;

    private String color;

    private String healthStatus;

    /**
     * 是否已疫苗: 0-否, 1-是
     */
    private Integer isVaccinated;

    /**
     * 是否已绝育: 0-否, 1-是
     */
    private Integer isSterilized;

    private String description;

    /**
     * 图片URL，多个用逗号分隔
     */
    private String imageUrl;

    /**
     * 状态: 0-待领养, 1-申请中, 2-已领养, 3-已下架
     */
    private Integer status;

    private Long publisherId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 非数据库字段
    @TableField(exist = false)
    private String categoryName;

    @TableField(exist = false)
    private String publisherName;
}
