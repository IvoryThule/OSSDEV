package com.whu.pet.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.whu.pet.common.Result;
import com.whu.pet.dto.AdoptionRequest;
import com.whu.pet.entity.AdoptionApplication;
import com.whu.pet.security.JwtUtils;
import com.whu.pet.service.AdoptionApplicationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 领养申请控制器
 */
@RestController
@RequestMapping("/api/applications")
public class AdoptionApplicationController {

    @Autowired
    private AdoptionApplicationService applicationService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 提交领养申请
     */
    @PostMapping
    public Result<Void> submitApplication(@Validated @RequestBody AdoptionRequest request,
                                          @RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(token);

        AdoptionApplication application = new AdoptionApplication();
        BeanUtils.copyProperties(request, application);
        application.setApplicantId(userId);

        if (applicationService.submitApplication(application)) {
            return Result.success("申请提交成功", null);
        }
        return Result.error("申请失败，该宠物暂不可申请或您已有待审核的申请");
    }

    /**
     * 获取申请列表（管理员/救助站）
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SHELTER')")
    public Result<IPage<AdoptionApplication>> getApplicationList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long petId) {
        IPage<AdoptionApplication> page = applicationService.getApplicationPage(pageNum, pageSize, status, null, petId, null);
        return Result.success(page);
    }

    /**
     * 获取我的申请列表
     */
    @GetMapping("/my")
    public Result<IPage<AdoptionApplication>> getMyApplications(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(token);
        IPage<AdoptionApplication> page = applicationService.getApplicationPage(pageNum, pageSize, status, userId, null, null);
        return Result.success(page);
    }

    /**
     * 获取我收到的申请列表（作为送养人）
     */
    @GetMapping("/received")
    public Result<IPage<AdoptionApplication>> getReceivedApplications(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            @RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(token);
        IPage<AdoptionApplication> page = applicationService.getApplicationPage(pageNum, pageSize, status, null, null, userId);
        return Result.success(page);
    }

    /**
     * 获取申请详情
     */
    @GetMapping("/{id}")
    public Result<AdoptionApplication> getApplicationDetail(@PathVariable Long id) {
        AdoptionApplication application = applicationService.getApplicationDetail(id);
        return Result.success(application);
    }

    /**
     * 管理员审核
     */
    @PutMapping("/{id}/admin-review")
    @PreAuthorize("hasAnyRole('ADMIN', 'SHELTER')")
    public Result<Void> adminReview(@PathVariable Long id,
                                    @RequestParam Integer status,
                                    @RequestParam(required = false) String rejectReason,
                                    @RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        Long reviewerId = jwtUtils.getUserIdFromToken(token);

        if (applicationService.adminReview(id, status, rejectReason, reviewerId)) {
            return Result.success("操作成功", null);
        }
        return Result.error("操作失败");
    }

    /**
     * 发布者审核
     */
    @PutMapping("/{id}/publisher-review")
    public Result<Void> publisherReview(@PathVariable Long id,
                                        @RequestParam Integer status,
                                        @RequestParam(required = false) String rejectReason) {
        // TODO: 应该校验当前用户是否是该申请对应宠物的发布者，这里简化处理
        if (applicationService.publisherReview(id, status, rejectReason)) {
            return Result.success("操作成功", null);
        }
        return Result.error("操作失败");
    }

    /**
     * 审核通过 (Deprecated)
     */
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'SHELTER')")
    public Result<Void> approveApplication(@PathVariable Long id,
                                           @RequestHeader("Authorization") String authorization) {
        return adminReview(id, 1, null, authorization);
    }

    /**
     * 审核拒绝
     */
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'SHELTER')")
    public Result<Void> rejectApplication(@PathVariable Long id,
                                          @RequestParam(required = false) String reason,
                                          @RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        Long reviewerId = jwtUtils.getUserIdFromToken(token);

        if (applicationService.rejectApplication(id, reviewerId, reason)) {
            return Result.success("已拒绝", null);
        }
        return Result.error("操作失败");
    }

    /**
     * 取消申请
     */
    @PutMapping("/{id}/cancel")
    public Result<Void> cancelApplication(@PathVariable Long id,
                                          @RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(token);

        if (applicationService.cancelApplication(id, userId)) {
            return Result.success("已取消", null);
        }
        return Result.error("操作失败，只能取消自己的待审核申请");
    }
}
