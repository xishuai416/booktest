package com.wky.book.service;

import com.wky.book.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wky.book.request.UserLoginReqVO;
import com.wky.book.request.UserNameReqVo;
import com.wky.book.response.UserLoginRespVO;

/**
 * <p>
 * 用户表（微信）表 服务类
 * </p>
 *
 * @author wky
 * @since 2022-04-02
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param userLoginReqVO
     * @return
     */
    UserLoginRespVO userLogin(UserLoginReqVO userLoginReqVO);

    /**
     * 修改用户别名,真实姓名
     * @param userNameReqVo
     * @return
     */
    void updateReallyNameAndUserName( UserNameReqVo userNameReqVo);


}
