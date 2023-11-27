package com.fuHoi.auth.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.fuHoi.model.system.SysMenu;
import com.fuHoi.vo.system.AssginMenuVo;
import com.fuHoi.vo.system.RouterVo;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author fuHoi
 * @since 2023-06-01
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 菜单树形数据
     * @return
     */
    List<SysMenu> findNodes();

    void removeMenuById(Long id);

    /**
     * 根据角色获取授权权限数据
     * @return
     */
    List<SysMenu> findSysMenuByRoleId(Long roleId);

    /**
     * 保存角色权限
     * @param  assginMenuVo
     */
    void doAssign(AssginMenuVo assginMenuVo);

    /**
     * 获取用户菜单
     * @param userId
     * @return
     */
    List<RouterVo> findUserMenuList(Long userId);

    List<String> findUserPermsList(Long userId);
}
