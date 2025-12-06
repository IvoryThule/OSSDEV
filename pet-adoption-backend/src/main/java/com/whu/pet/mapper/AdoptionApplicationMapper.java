package com.whu.pet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whu.pet.entity.AdoptionApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 领养申请Mapper接口
 */
@Mapper
public interface AdoptionApplicationMapper extends BaseMapper<AdoptionApplication> {

    /**
     * 分页查询申请列表
     */
    IPage<AdoptionApplication> selectApplicationPage(Page<AdoptionApplication> page,
                                                      @Param("status") Integer status,
                                                      @Param("applicantId") Long applicantId,
                                                      @Param("petId") Long petId,
                                                      @Param("publisherId") Long publisherId);

    /**
     * 查询申请详情
     */
    AdoptionApplication selectApplicationDetail(@Param("id") Long id);
}
