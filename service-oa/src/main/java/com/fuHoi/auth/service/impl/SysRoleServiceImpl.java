package com.fuHoi.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuHoi.auth.mapper.SysRoleMapper;
import com.fuHoi.auth.mapper.SysUserRoleMapper;
import com.fuHoi.auth.service.SysRoleService;
import com.fuHoi.auth.service.SysUserRoleService;
import com.fuHoi.model.system.SysRole;
import com.fuHoi.model.system.SysUserRole;
import com.fuHoi.vo.system.AssginRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @title: SysRoleServiceImpl
 * @Author Xie
 * @Date: 2023/5/25 20:57
 * @Version 1.0
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Override
    public Map<String, Object> findRoleByUserId(Long userId) {
        //查询所有的角色
        List<SysRole> allRolesList = this.list();

        //根据userid查询角色用户关系表，查询userid对应所有角色id
        List<SysUserRole> existUserRoleList
                = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>
                ().eq(SysUserRole::getUserId, userId).select(SysUserRole::getRoleId));
        List<Long> existRolIdList = existUserRoleList.stream().map(item -> item.getRoleId()).collect(Collectors.toList());

        //根据查询所有角色id，找到对应角色信息
        List<SysRole> assginRoleList = new ArrayList<>();
        for (SysRole sysRole : allRolesList) {
            //比较
            if(existRolIdList.contains(sysRole.getId())){
                assginRoleList.add(sysRole);
            }
        }

        //把得到的两部分数据封装map集合，返回
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("assginRoleList", assginRoleList);
        roleMap.put("allRolesList", allRolesList);
        return roleMap;
    }

    @Transactional
    @Override
    public void doAssign(AssginRoleVo assginRoleVo) {
        //把用户之前分配的角色数据删除
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId,assginRoleVo.getUserId()));
        //重新进行分配
        for(Long roleId:assginRoleVo.getRoleIdList()){
            if (StringUtils.isEmpty(roleId)) continue;
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(assginRoleVo.getUserId());
            userRole.setRoleId(roleId);
            sysUserRoleMapper.insert(userRole);
        }

    }
}
