package com.fuHoi.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuHoi.model.system.SysRole;
import com.fuHoi.vo.system.AssginRoleVo;

import java.util.Map;

public interface SysRoleService extends IService<SysRole> {
    /**
     * 根据用户获取角色数据
     * @param userId
     * @return
     */
    Map<String, Object> findRoleByUserId(Long userId);

    /**
     * 分配角色
     * @param assginRoleVo
     */
    void doAssign(AssginRoleVo assginRoleVo);
}
