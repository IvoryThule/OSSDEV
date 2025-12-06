package com.whu.pet.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 领养申请请求DTO
 */
@Data
public class AdoptionRequest {

    @NotNull(message = "宠物ID不能为空")
    private Long petId;

    @NotBlank(message = "领养理由不能为空")
    private String reason;

    private String livingCondition;

    private String experience;

    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    private String contactAddress;
}
