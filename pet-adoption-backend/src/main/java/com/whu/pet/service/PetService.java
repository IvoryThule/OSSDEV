package com.whu.pet.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whu.pet.entity.AdoptionApplication;
import com.whu.pet.entity.Pet;
import com.whu.pet.mapper.AdoptionApplicationMapper;
import com.whu.pet.mapper.PetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * 宠物服务类
 */
@Service
public class PetService extends ServiceImpl<PetMapper, Pet> {

    @Autowired
    private AdoptionApplicationMapper adoptionApplicationMapper;

    /**
     * 分页查询宠物列表
     */
    public IPage<Pet> getPetPage(int pageNum, int pageSize, Long categoryId, Integer status, String keyword) {
        Page<Pet> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectPetPage(page, categoryId, status, keyword);
    }

    /**
     * 获取宠物详情
     */
    public Pet getPetDetail(Long id) {
        return baseMapper.selectPetDetail(id);
    }

    /**
     * 更新宠物状态
     */
    public boolean updateStatus(Long id, Integer status) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setStatus(status);
        return updateById(pet);
    }

    /**
     * 查询某用户发布的宠物列表
     */
    public IPage<Pet> getMyPets(int pageNum, int pageSize, Long publisherId) {
        Page<Pet> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Pet> wrapper = new QueryWrapper<>();
        wrapper.eq("publisher_id", publisherId);
        wrapper.orderByDesc("create_time");
        return page(page, wrapper);
    }

    /**
     * 统计待领养宠物数量
     */
    public long countAvailable() {
        return count(new QueryWrapper<Pet>().eq("status", 0));
    }

    /**
     * 统计已领养宠物数量
     */
    public long countAdopted() {
        return count(new QueryWrapper<Pet>().eq("status", 2));
    }

    /**
     * 删除宠物（级联删除领养申请）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        // 删除关联的领养申请
        adoptionApplicationMapper.delete(new QueryWrapper<AdoptionApplication>().eq("pet_id", id));
        // 删除宠物
        return super.removeById(id);
    }
}
