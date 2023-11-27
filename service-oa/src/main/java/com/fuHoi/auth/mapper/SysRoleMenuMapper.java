package com.fuHoi.auth.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fuHoi.model.system.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 角色菜单 Mapper 接口
 * </p>
 *
 * @author fuHoi
 * @since 2023-06-01
 */
@Mapper
@Repository
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

}
