package com.fuHoi.auth;


import com.fuHoi.auth.mapper.SysRoleMapper;
import com.fuHoi.model.system.SysRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @title: TestMpDemo
 * @Author Xie
 * @Date: 2023/5/25 20:37
 * @Version 1.0
 */
@SpringBootTest
public class TestMpDemo {

    @Autowired
    private SysRoleMapper mapper;
    @Test
    public void getAll(){
        List<SysRole> list = mapper.selectList(null);
        System.out.println("list = " + list);
    }


    @Test
    public void testInsert(){
        SysRole sysRole = new SysRole();
        sysRole.setRoleName("角色管理员");
        sysRole.setRoleCode("role");
        sysRole.setDescription("角色管理员");

        int result = mapper.insert(sysRole);
        System.out.println(result); //影响的行数
        System.out.println(sysRole); //id自动回填
    }


    @Test
    public void test(){
        Map<String,Object> map=new HashMap<>();
        List<Map> list=new ArrayList<>();
        map.put("address","广东省广州市白云区马沥坑边庄街980号靠近广东工贸职业技术学院1区教学楼");
        map.put("lng","113.459691");
        map.put("lat","23.40176");
        list.add(map);
        map=new HashMap<>();
        map.put("address","广东省广州市白云区和龙路8号靠近广州市白云区和龙水库管理所");
        map.put("lng","113.3682762585525");
        map.put("lat","23.28413320595913");
        list.add(map);
        map=new HashMap<>();
        map.put("address","广东省广州市天河区S4华南快速1-14号靠近广州公路工程集团有限公司市政分公司");
        map.put("lng","113.36258");
        map.put("lat","23.220991");
        list.add(map);
        map=new HashMap<>();
        map.put("address","广东省广州市天河区天河路529号靠近壬丰大厦");
        map.put("lng","113.3354589520421");
        map.put("lat","23.133604542657807");
        list.add(map);
        map=new HashMap<>();
        map.put("address","广东省广州市天河区天河路362号靠近天环广场");
        map.put("lng","113.324193");
        map.put("lat","23.133194");
        list.add(map);

    }
}
