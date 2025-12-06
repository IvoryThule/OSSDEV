package com.whu.pet.vo;

import lombok.Data;

/**
 * 统计数据VO
 */
@Data
public class StatisticsVO {

    /**
     * 待领养宠物数量
     */
    private Long availablePets;

    /**
     * 已领养宠物数量
     */
    private Long adoptedPets;

    /**
     * 待审核申请数量
     */
    private Long pendingApplications;

    /**
     * 用户总数
     */
    private Long totalUsers;
}
