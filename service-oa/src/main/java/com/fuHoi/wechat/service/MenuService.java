package com.fuHoi.wechat.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.fuHoi.model.wechat.Menu;
import com.fuHoi.vo.wechat.MenuVo;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author fuHoi
 * @since 2023-07-01
 */
public interface MenuService extends IService<Menu> {
    List<MenuVo> findMenuInfo();

    void syncMenu();

    //删除菜单
    void removeMenu();
}
