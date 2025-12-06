package com.whu.pet.controller;

import com.whu.pet.common.Result;
import com.whu.pet.service.AdoptionApplicationService;
import com.whu.pet.service.PetService;
import com.whu.pet.service.UserService;
import com.whu.pet.vo.StatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计控制器
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private PetService petService;

    @Autowired
    private AdoptionApplicationService applicationService;

    @Autowired
    private UserService userService;

    /**
     * 获取统计数据
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SHELTER')")
    public Result<StatisticsVO> getStatistics() {
        StatisticsVO statistics = new StatisticsVO();
        statistics.setAvailablePets(petService.countAvailable());
        statistics.setAdoptedPets(petService.countAdopted());
        statistics.setPendingApplications(applicationService.countPending());
        statistics.setTotalUsers(userService.count());
        return Result.success(statistics);
    }
}
