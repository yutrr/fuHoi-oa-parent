package com.fuHoi.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fuHoi.model.system.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author fuHoi
 * @since 2023-05-29
 */
@Mapper
@Repository
public interface SysUserMapper extends BaseMapper<SysUser> {

}
