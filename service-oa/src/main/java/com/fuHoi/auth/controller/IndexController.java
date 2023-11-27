package com.fuHoi.auth.controller;

/**
 * @title: IndexController
 * @Author Xie
 * @Date: 2023/5/27 16:28
 * @Version 1.0
 */

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fuHoi.auth.service.SysUserService;
import com.fuHoi.common.config.execption.FuHoiException;
import com.fuHoi.common.result.Result;
import com.fuHoi.common.jwt.JwtHelper;
import com.fuHoi.common.utils.MD5;
import com.fuHoi.model.system.SysUser;
import com.fuHoi.vo.system.LoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 后台登录登出
 * </p>
 */
@Api(tags = "后台登录管理")
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 登录
     * @return
     */
    @ApiOperation(value = "登录")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo) {
        //1 获取输入用户名和密码
        //2 根据用户名查询数据库
        String username = loginVo.getUsername();
        SysUser sysUser = sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (sysUser==null){
            throw new FuHoiException(201,"用户不存在");
        }
        if (!MD5.encrypt(loginVo.getPassword()).equals(sysUser.getPassword())){
            throw new FuHoiException(201,"密码错误");
        }
        if(sysUser.getStatus().intValue() == 0) {
            throw new FuHoiException(201,"密码错误");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("token", JwtHelper.createToken(sysUser.getId(), sysUser.getUsername()));
        return Result.ok(map);
    }
    /**
     * 获取用户信息
     * @return
     */
    @ApiOperation(value = "获取用户信息")
    @GetMapping("info")
    public Result info(HttpServletRequest request) {
        //1.从请求头获取用户信息（获取请求头token字符串）
        String username = JwtHelper.getUsername(request.getHeader("token"));
        //2.从token字符串获取userId或者用户名称
        Map<String,Object> map=sysUserService.getUserInfo(username);

        //3.根据用户id查询数据库，把用户信息查询出来

        //4.根据用户id获取用户可以操作菜单列表
        //查询数据库动态构建路由结构，进行显示

        //5根据用户id获取用户可以操作菜单列表

        //6.返回相应的列表
        //Map<String, Object> map = new HashMap<>();
        //map.put("roles","[admin]");
        //map.put("name","admin");
        //map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        //TODO 返回用户可以操作的菜单
        //map.put("routers","");
        //TODO 返回用户可以操作的按钮
        //map.put("buttons","");
        return Result.ok(map);
    }
    /**
     * 退出
     * @return
     */
    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }

}
