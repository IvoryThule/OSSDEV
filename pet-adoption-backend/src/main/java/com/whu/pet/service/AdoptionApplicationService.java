package com.whu.pet.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whu.pet.entity.AdoptionApplication;
import com.whu.pet.entity.Pet;
import com.whu.pet.mapper.AdoptionApplicationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 领养申请服务类
 */
@Service
public class AdoptionApplicationService extends ServiceImpl<AdoptionApplicationMapper, AdoptionApplication> {

    @Autowired
    private PetService petService;

    /**
     * 分页查询申请列表
     */
    public IPage<AdoptionApplication> getApplicationPage(int pageNum, int pageSize,
                                                         Integer status, Long applicantId, Long petId, Long publisherId) {
        Page<AdoptionApplication> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectApplicationPage(page, status, applicantId, petId, publisherId);
    }

    /**
     * 获取申请详情
     */
    public AdoptionApplication getApplicationDetail(Long id) {
        return baseMapper.selectApplicationDetail(id);
    }

    /**
     * 提交领养申请
     */
    @Transactional
    public boolean submitApplication(AdoptionApplication application) {
        // 检查宠物是否可领养
        Pet pet = petService.getById(application.getPetId());
        if (pet == null || pet.getStatus() != 0) {
            throw new RuntimeException("宠物不可领养");
        }

        // 1. 检查是否是自己发布的宠物
        if (pet.getPublisherId().equals(application.getApplicantId())) {
            throw new RuntimeException("不能申请领养自己发布的宠物");
        }

        // 检查是否已有待审核的申请
        long count = count(new QueryWrapper<AdoptionApplication>()
                .eq("pet_id", application.getPetId())
                .eq("applicant_id", application.getApplicantId())
                .in("status", 0, 1)); // 待审核或已通过
        if (count > 0) {
            throw new RuntimeException("您已提交过申请");
        }

        application.setStatus(0);
        application.setAdminStatus(0);
        application.setPublisherStatus(0);
        boolean result = save(application);

        // 更新宠物状态为"申请中"
        if (result) {
            petService.updateStatus(application.getPetId(), 1);
        }

        return result;
    }

    /**
     * 管理员审核
     */
    @Transactional
    public boolean adminReview(Long id, Integer status, String rejectReason, Long reviewerId) {
        AdoptionApplication application = getById(id);
        if (application == null) return false;

        application.setAdminStatus(status);
        if (status == 2) { // 拒绝
            application.setStatus(2);
            application.setRejectReason("管理员拒绝: " + rejectReason);
            application.setReviewerId(reviewerId);
            application.setReviewTime(LocalDateTime.now());
            
            // 恢复宠物状态为待领养
            petService.updateStatus(application.getPetId(), 0);
        } else if (status == 1) { // 通过
            // 检查发布者是否也通过
            if (application.getPublisherStatus() == 1) {
                application.setStatus(1);
                application.setReviewerId(reviewerId);
                application.setReviewTime(LocalDateTime.now());
                
                // 更新宠物状态为已领养
                petService.updateStatus(application.getPetId(), 2);
                // 拒绝其他申请
                rejectOtherApplications(application.getPetId(), id, reviewerId);
            }
        }
        return updateById(application);
    }

    /**
     * 发布者审核
     */
    @Transactional
    public boolean publisherReview(Long id, Integer status, String rejectReason) {
        AdoptionApplication application = getById(id);
        if (application == null) return false;

        application.setPublisherStatus(status);
        if (status == 2) { // 拒绝
            application.setStatus(2);
            application.setRejectReason("发布者拒绝: " + rejectReason);
            
            // 恢复宠物状态为待领养
            petService.updateStatus(application.getPetId(), 0);
        } else if (status == 1) { // 通过
            // 检查管理员是否也通过
            if (application.getAdminStatus() == 1) {
                application.setStatus(1);
                application.setReviewTime(LocalDateTime.now());
                
                // 更新宠物状态为已领养
                petService.updateStatus(application.getPetId(), 2);
                // 拒绝其他申请
                rejectOtherApplications(application.getPetId(), id, null);
            }
        }
        return updateById(application);
    }

    /**
     * 审核通过申请 (Deprecated, use adminReview or publisherReview)
     */
    @Transactional
    public boolean approveApplication(Long id, Long reviewerId) {
        return adminReview(id, 1, null, reviewerId);
    }

    /**
     * 拒绝申请
     */
    @Transactional
    public boolean rejectApplication(Long id, Long reviewerId, String rejectReason) {
        AdoptionApplication application = getById(id);
        if (application == null || application.getStatus() != 0) {
            return false;
        }

        // 更新申请状态
        application.setStatus(2);
        application.setReviewerId(reviewerId);
        application.setReviewTime(LocalDateTime.now());
        application.setRejectReason(rejectReason);
        boolean result = updateById(application);

        // 检查是否还有其他待审核申请，如果没有则将宠物状态改回"待领养"
        if (result) {
            long pendingCount = count(new QueryWrapper<AdoptionApplication>()
                    .eq("pet_id", application.getPetId())
                    .eq("status", 0));
            if (pendingCount == 0) {
                petService.updateStatus(application.getPetId(), 0);
            }
        }

        return result;
    }

    /**
     * 取消申请
     */
    @Transactional
    public boolean cancelApplication(Long id, Long applicantId) {
        AdoptionApplication application = getById(id);
        if (application == null || !application.getApplicantId().equals(applicantId) || application.getStatus() != 0) {
            return false;
        }

        // 物理删除申请记录
        boolean result = removeById(id);

        // 检查是否还有其他待审核申请
        if (result) {
            long pendingCount = count(new QueryWrapper<AdoptionApplication>()
                    .eq("pet_id", application.getPetId())
                    .eq("status", 0));
            if (pendingCount == 0) {
                petService.updateStatus(application.getPetId(), 0);
            }
        }

        return result;
    }

    /**
     * 拒绝该宠物的其他待审核申请
     */
    private void rejectOtherApplications(Long petId, Long approvedId, Long reviewerId) {
        update(new AdoptionApplication() {{
            setStatus(2);
            setReviewerId(reviewerId);
            setReviewTime(LocalDateTime.now());
            setRejectReason("该宠物已被其他申请人领养");
        }}, new QueryWrapper<AdoptionApplication>()
                .eq("pet_id", petId)
                .eq("status", 0)
                .ne("id", approvedId));
    }

    /**
     * 统计待审核申请数量
     */
    public long countPending() {
        return count(new QueryWrapper<AdoptionApplication>().eq("status", 0));
    }
}
