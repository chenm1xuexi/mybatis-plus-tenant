package com.feifei.mybatisplus.tenant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feifei.mybatisplus.tenant.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author shixiongfei
 * @date 2020-03-01
 * @since
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
