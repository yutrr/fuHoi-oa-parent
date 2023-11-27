package com.fuHoi.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fuHoi.model.system.SysUser;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author fuHoi
 * @since 2023-05-29
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 更改状态
     * @param id
     * @param status
     */
    void updateStatus(Long id, Integer status);

    SysUser getByUserName(String username);

    /**
     * 根据用户名获取用户登录信息
     * @param
     * @return
     */
    Map<String, Object> getUserInfo(String username);

    Map<String, Object> getCurrentUser();
}
