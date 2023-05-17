package com.wky.book.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wky.book.common.constant.CacheConstant;
import com.wky.book.common.constant.ConstantPool;
import com.wky.book.common.context.UserTokenContextHolder;
import com.wky.book.common.exception.BasicInfoException;
import com.wky.book.common.exception.BasicInfoStatusEnum;
import com.wky.book.dto.UserDTO;
import com.wky.book.dto.UserLoginDTO;
import com.wky.book.entity.BookUser;
import com.wky.book.entity.User;
import com.wky.book.mapper.UserMapper;
import com.wky.book.request.UserNameReqVo;
import com.wky.book.service.BookUserService;
import com.wky.book.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wky.book.request.UserLoginReqVO;
import com.wky.book.response.UserLoginRespVO;
import com.wky.book.vo.UserTokenVO;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户表（微信）表 服务实现类
 * </p>
 *
 * @author wky
 * @since 2022-04-02
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private WxMaService wxMaService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserService userService;

    @Autowired
    BookUserService bookUserService;

    @Override
    public UserLoginRespVO userLogin(UserLoginReqVO userLoginReqVO) {

        // 请求微信 auth.code2Session 接口，换取用户唯一标识 openid
        String openid = "";
        String sessionKey = "";
        try {
            WxMaJscode2SessionResult wxMaJscode2SessionResult = wxMaService.getUserService()
                    .getSessionInfo(userLoginReqVO.getCode());
            openid = wxMaJscode2SessionResult.getOpenid();
            sessionKey = wxMaJscode2SessionResult.getSessionKey();
        } catch (WxErrorException e) {
            log.error("请求微信接口code2Session，获取用户唯一标识 openid异常:{}", e.fillInStackTrace());
            throw new BasicInfoException(BasicInfoStatusEnum.THIRD_SERVICE_UNAVAILABLE.getCode(), "请求微信服务失败");
        }

        UserLoginDTO userLoginDTO = new UserLoginDTO().setOpenid(openid);

        // 校验和解密用户信息
        if (StrUtil.isNotBlank(userLoginReqVO.getRawData())
                && StrUtil.isNotBlank(userLoginReqVO.getSignature())
                && StrUtil.isNotBlank(userLoginReqVO.getIv())
                && StrUtil.isNotBlank(userLoginReqVO.getEncryptedData())) {


            // 用户信息校验
            Assert.isTrue(
                    wxMaService.getUserService().checkUserInfo(sessionKey, userLoginReqVO.getRawData(),
                            userLoginReqVO.getSignature()),
                    () -> new BasicInfoException(BasicInfoStatusEnum.PARAM_ERROR.getCode(), "用户信息校验失败"));

            // 解密用户信息
            WxMaUserInfo userInfo = wxMaService.getUserService().getUserInfo(sessionKey,
                    userLoginReqVO.getEncryptedData(), userLoginReqVO.getIv());

            userLoginDTO.setAvatarUrl(userInfo.getAvatarUrl()).setNickName(userInfo.getNickName())
                    .setGender(StrUtil.isNotBlank(userInfo.getGender()) ? Integer.valueOf(userInfo.getGender()) : 0);

        }

        UserDTO userDTO = userLogin(userLoginDTO);

        // 用户重新登录后，移除用户历史token和历史sessionKey
        String historyToken = (String) redisTemplate.opsForValue()
                .get(ConstantPool.getCachePrefix() + StrUtil.COLON + CacheConstant.OPENID_KEY + openid);
        if (StrUtil.isNotBlank(historyToken)) {
            redisTemplate.delete(ConstantPool.getCacheTokenName() + StrUtil.COLON + historyToken);
            redisTemplate.delete(
                    ConstantPool.getCachePrefix() + StrUtil.COLON + CacheConstant.USER_SESSION_KEY + historyToken);
        }
        redisTemplate.opsForValue().set(
                ConstantPool.getCachePrefix() + StrUtil.COLON + CacheConstant.OPENID_KEY + openid, userDTO.getToken(),
                30, TimeUnit.DAYS);

        // token 保存到缓存，有效时间30天
        UserTokenVO userTokenVO = new UserTokenVO().setUserId(userDTO.getId()).setOpenid(openid)
                .setToken(userDTO.getToken());
        String tokenCacheKey = ConstantPool.getCacheTokenName() + StrUtil.COLON + userTokenVO.getToken();
        redisTemplate.opsForValue().set(tokenCacheKey, JSONUtil.toJsonStr(userTokenVO), 30, TimeUnit.DAYS);

        // sessionKey 保存到缓存，有效时间3天
        String sessionKeyCacheKey = ConstantPool.getCachePrefix() + StrUtil.COLON + CacheConstant.USER_SESSION_KEY
                + userTokenVO.getToken();
        redisTemplate.opsForValue().set(sessionKeyCacheKey, sessionKey, 3, TimeUnit.DAYS);
        UserLoginRespVO userLoginRespVO = BeanUtil.copyProperties(userDTO, UserLoginRespVO.class);
        return userLoginRespVO;
    }

    public UserDTO userLogin(UserLoginDTO userLoginDTO) {
        User user = userService.getOne(new QueryWrapper<User>().lambda().eq(User::getOpenid, userLoginDTO.getOpenid()));
        boolean bool = false;
        if (null != user) {
            user.setAvatarUrl(StrUtil.isNotBlank(userLoginDTO.getAvatarUrl()) ? userLoginDTO.getAvatarUrl()
                    : user.getAvatarUrl());
            user.setGender(Objects.nonNull(userLoginDTO.getGender()) ? userLoginDTO.getGender() : user.getGender());
            user.setNickName(
                    StrUtil.isNotBlank(userLoginDTO.getNickName()) ? userLoginDTO.getNickName() : user.getNickName());
        } else {
            bool = true;
            user = new User();
            BeanUtil.copyProperties(userLoginDTO, user, false);
        }

        if (StrUtil.isNotBlank(user.getNickName())) {
            user.setNickName(user.getNickName().trim());
        }
        user.setCreateTime(new Date());
        if (bool) {
            userService.save(user);
        } else {
            userService.updateById(user);
        }
        String token = IdUtil.fastSimpleUUID() + RandomUtil.randomString(8);

        return BeanUtil.copyProperties(user, UserDTO.class).setToken(token);
    }


    /**
     * 修改用户别名,真实姓名
     *
     * @param userNameReqVo
     */
    @Override
    public void updateReallyNameAndUserName(UserNameReqVo userNameReqVo) {
        User byId = this.getById(userNameReqVo.getBookId());
        if (byId == null) {
            throw new BasicInfoException(BasicInfoStatusEnum.PARAM_ERROR.getCode(), "用户不存在");
        }

        UserTokenVO userTokenVOByToken = UserTokenContextHolder.getUserTokenVOByToken();

        // 是否是本人操作
        boolean isUser = userTokenVOByToken.getOpenid().equals(byId.getOpenid());

        // 操作人是否是管理员
        BookUser bookUser = bookUserService.getOne(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getBookId, userNameReqVo.getBookId())
                .eq(BookUser::getUserId, userNameReqVo.getUserId()));
        if(null==bookUser){
            throw new BasicInfoException(BasicInfoStatusEnum.PARAM_ERROR.getCode(), "账本不存在该用户");
        }
        // 是否是管理员权限
        boolean isRoot = "1".equals(bookUser.getAuth());

        if(isRoot||isUser){
            bookUser.setUpdateBy(byId.getId());
            bookUser.setReallyName(userNameReqVo.getReallyName());
            bookUser.setUserName(userNameReqVo.getUserName());
            bookUserService.updateById(bookUser);
        }

    }
}
