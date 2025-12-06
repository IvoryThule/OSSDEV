package com.whu.pet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.whu.pet.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
