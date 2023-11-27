package com.fuHoi.auth.utils;

import com.fuHoi.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: MenuHelper
 * @Author Xie
 * @Date: 2023/6/1 20:51
 * @Version 1.0
 */
public class MenuHelper {
    /**
     * 使用递归方法建菜单
     *
     * @param sysMenuList
     * @return
     */
    public static List<SysMenu> builderTree(List<SysMenu> sysMenuList) {
        List<SysMenu> trees = new ArrayList<>();
        for (SysMenu sysMenu : sysMenuList) {
            if (sysMenu.getParentId().longValue() == 0) {
                trees.add(getChildren(sysMenu, sysMenuList));
            }
        }
        return trees;
    }
    /**
     * 递归查找子节点
     * @param sysMenuList
     * @return
     */
    private static SysMenu getChildren(SysMenu sysMenu, List<SysMenu> sysMenuList) {
        sysMenu.setChildren(new ArrayList<SysMenu>());
        for (SysMenu it : sysMenuList) {
            if (sysMenu.getId().longValue()==it.getParentId().longValue()){
                if (sysMenu.getChildren() == null) {
                    sysMenu.setChildren(new ArrayList<>());
                }
                sysMenu.getChildren().add(getChildren(it, sysMenuList));
            }
        }
        return sysMenu;
    }
}
