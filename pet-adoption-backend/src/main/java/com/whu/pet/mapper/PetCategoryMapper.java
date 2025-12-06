package com.whu.pet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.whu.pet.entity.PetCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 宠物分类Mapper接口
 */
@Mapper
public interface PetCategoryMapper extends BaseMapper<PetCategory> {
}
