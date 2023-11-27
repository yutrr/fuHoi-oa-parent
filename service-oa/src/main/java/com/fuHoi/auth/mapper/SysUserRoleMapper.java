package com.fuHoi.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fuHoi.model.system.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 用户角色 Mapper 接口
 * </p>
 *
 * @author fuHoi
 * @since 2023-05-29
 */
@Mapper
@Repository
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

}
