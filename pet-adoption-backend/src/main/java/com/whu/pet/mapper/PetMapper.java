package com.whu.pet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whu.pet.entity.Pet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 宠物Mapper接口
 */
@Mapper
public interface PetMapper extends BaseMapper<Pet> {

    /**
     * 分页查询宠物列表（包含分类名称和发布者信息）
     */
    IPage<Pet> selectPetPage(Page<Pet> page,
                              @Param("categoryId") Long categoryId,
                              @Param("status") Integer status,
                              @Param("keyword") String keyword);

    /**
     * 根据ID查询宠物详情
     */
    Pet selectPetDetail(@Param("id") Long id);
}
