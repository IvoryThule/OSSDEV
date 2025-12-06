package com.whu.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 宠物分类实体类
 */
@Data
@TableName("pet_category")
public class PetCategory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
