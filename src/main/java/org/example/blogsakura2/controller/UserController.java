package org.example.blogsakura2.controller;

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.common.aop.Log;
import org.example.blogsakura2.pojo.Result;
import org.example.blogsakura2.pojo.User;
import org.example.blogsakura2.service.UserService;
import org.springframework.web.bind.annotation.*;

@Slf4j
//@RestController
public class UserController {

    //    @Autowired
    private UserService userService;

    //    @PostMapping("/backend/login")
    @Log
    public Result login(@RequestBody User user) {
        log.info("获取登录信息:{}", user);
        User user_result = userService.getUserByMobile(user);
        if (user_result == null) {
            return Result.error("用户名不存在");
        }
        if (!user_result.getCode().equals(user.getCode())) {
            return Result.error("验证码错误");
        }

        StpUtil.login(user.getMobile());
        return Result.success();
    }

    //    @GetMapping("/backend/logout")
    public Result logout() {
        StpUtil.logout();
        log.info("token查询", StpUtil.getTokenValue());
        return Result.success();
    }

    //    @GetMapping("/backend/islogin")
    public Result isLogin() {
        if (StpUtil.isLogin()) {
            return Result.success();
        } else {
            return Result.error("尚未登录");
        }
    }
}
