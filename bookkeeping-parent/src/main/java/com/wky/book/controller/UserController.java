package com.wky.book.controller;


import com.wky.book.request.UserLoginReqVO;
import com.wky.book.request.UserNameReqVo;
import com.wky.book.response.UserLoginRespVO;
import com.wky.book.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 用户表（微信）表 前端控制器
 * </p>
 *
 * @author wky
 * @since 2022-04-02
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    /**
     * 用户登录
     *
     * @param reqVo
     * @return
     */
    @PostMapping("/login")
    public UserLoginRespVO login(@RequestBody UserLoginReqVO reqVo) {
        return userService.userLogin(reqVo);
    }

    /**
     * 修改用户别名，真实姓名
     *
     * @param userNameReqVo
     * @return
     */
    @GetMapping("/login")
    public void updateReallyNameAndUserName(@Valid @RequestBody  UserNameReqVo userNameReqVo) {
        userService.updateReallyNameAndUserName(userNameReqVo);
    }

}
