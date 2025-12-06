package com.whu.pet.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.whu.pet.common.Result;
import com.whu.pet.entity.Pet;
import com.whu.pet.security.JwtUtils;
import com.whu.pet.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 宠物管理控制器
 */
@RestController
@RequestMapping("/api/pets")
public class PetController {

    @Autowired
    private PetService petService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 分页查询宠物列表（公开）
     */
    @GetMapping
    public Result<IPage<Pet>> getPetList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        IPage<Pet> page = petService.getPetPage(pageNum, pageSize, categoryId, status, keyword);
        return Result.success(page);
    }

    /**
     * 获取宠物详情（公开）
     */
    @GetMapping("/{id}")
    public Result<Pet> getPetDetail(@PathVariable Long id) {
        Pet pet = petService.getPetDetail(id);
        return Result.success(pet);
    }

    /**
     * 发布宠物信息（救助站/管理员/普通用户）
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SHELTER', 'USER')")
    public Result<Void> addPet(@RequestBody Pet pet, @RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(token);
        pet.setPublisherId(userId);
        pet.setStatus(0); // 默认待领养

        if (petService.save(pet)) {
            return Result.success();
        }
        return Result.error("发布失败");
    }

    /**
     * 更新宠物信息
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SHELTER', 'USER')")
    public Result<Void> updatePet(@PathVariable Long id, @RequestBody Pet pet, @RequestHeader("Authorization") String authorization) {
        // 校验权限：只有发布者或管理员可以修改
        Pet existingPet = petService.getById(id);
        if (existingPet == null) return Result.error("宠物不存在");
        
        String token = authorization.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(token);
        String role = jwtUtils.getClaimsFromToken(token).get("role", String.class);
        
        if (!"ADMIN".equals(role) && !existingPet.getPublisherId().equals(userId)) {
            return Result.error("无权修改");
        }

        pet.setId(id);
        // 不允许修改发布者
        pet.setPublisherId(null); 
        if (petService.updateById(pet)) {
            return Result.success();
        }
        return Result.error("更新失败");
    }

    /**
     * 删除宠物信息
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SHELTER', 'USER')")
    public Result<Void> deletePet(@PathVariable Long id, @RequestHeader("Authorization") String authorization) {
        // 校验权限
        Pet existingPet = petService.getById(id);
        if (existingPet == null) return Result.error("宠物不存在");

        String token = authorization.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(token);
        String role = jwtUtils.getClaimsFromToken(token).get("role", String.class);

        if (!"ADMIN".equals(role) && !existingPet.getPublisherId().equals(userId)) {
            return Result.error("无权删除");
        }

        if (petService.removeById(id)) {
            return Result.success();
        }
        return Result.error("删除失败");
    }

    /**
     * 更新宠物状态
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SHELTER', 'USER')")
    public Result<Void> updatePetStatus(@PathVariable Long id, @RequestParam Integer status, 
                                         @RequestHeader("Authorization") String authorization) {
        // 校验权限：只有发布者或管理员可以修改状态
        Pet existingPet = petService.getById(id);
        if (existingPet == null) return Result.error("宠物不存在");
        
        String token = authorization.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(token);
        String role = jwtUtils.getClaimsFromToken(token).get("role", String.class);
        
        // 普通用户只能取消自己发布的宠物（将状态改为3-已取消）
        if (!"ADMIN".equals(role) && !"SHELTER".equals(role)) {
            if (!existingPet.getPublisherId().equals(userId)) {
                return Result.error("无权操作");
            }
            // 普通用户只能取消，不能改成其他状态
            if (status != 3) {
                return Result.error("无权操作");
            }
        }
        
        if (petService.updateStatus(id, status)) {
            return Result.success();
        }
        return Result.error("操作失败");
    }

    /**
     * 获取我发布的宠物
     */
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('ADMIN', 'SHELTER', 'USER')")
    public Result<IPage<Pet>> getMyPets(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(token);
        IPage<Pet> page = petService.getMyPets(pageNum, pageSize, userId);
        return Result.success(page);
    }
}
