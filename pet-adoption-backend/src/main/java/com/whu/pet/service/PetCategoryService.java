package com.whu.pet.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whu.pet.entity.PetCategory;
import com.whu.pet.mapper.PetCategoryMapper;
import org.springframework.stereotype.Service;

/**
 * 宠物分类服务类
 */
@Service
public class PetCategoryService extends ServiceImpl<PetCategoryMapper, PetCategory> {
}
