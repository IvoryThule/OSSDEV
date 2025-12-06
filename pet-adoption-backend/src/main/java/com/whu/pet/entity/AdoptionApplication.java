package com.whu.pet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 领养申请实体类
 */
@Data
@TableName("adoption_application")
public class AdoptionApplication {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long petId;

    private Long applicantId;

    private String reason;

    private String livingCondition;

    private String experience;

    private String contactPhone;

    private String contactAddress;

    /**
     * 状态: 0-待审核, 1-已通过, 2-已拒绝, 3-已取消
     */
    private Integer status;

    /**
     * 管理员审核状态: 0-待审核, 1-已通过, 2-已拒绝
     */
    private Integer adminStatus;

    /**
     * 发布者审核状态: 0-待审核, 1-已通过, 2-已拒绝
     */
    private Integer publisherStatus;

    private String rejectReason;

    private LocalDateTime reviewTime;

    private Long reviewerId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 非数据库字段
    @TableField(exist = false)
    private String petName;

    @TableField(exist = false)
    private String petImageUrl;

    @TableField(exist = false)
    private String applicantName;

    @TableField(exist = false)
    private String reviewerName;
}
