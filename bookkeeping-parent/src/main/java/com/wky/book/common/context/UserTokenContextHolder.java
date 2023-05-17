package com.wky.book.common.context;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.wky.book.common.constant.ConstantPool;
import com.wky.book.common.exception.BasicInfoException;
import com.wky.book.common.exception.BasicInfoStatusEnum;
import com.wky.book.vo.UserTokenVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;

/**
 * @author weikaiyu
 * @version 1.0
 * @date 2022-04-02 17:44
 */
public abstract class UserTokenContextHolder {

    /**
     * 获取用户token
     *
     * @param token token
     * @return {@link UserTokenVO}
     */
    @SuppressWarnings("unchecked")
    public static UserTokenVO getUserTokenVOByToken(String token) {
        RedisTemplate<String, Object> redisTemplate = SpringUtil.getBean("redisTemplate", RedisTemplate.class);

        String tokenCacheKey = ConstantPool.getCacheTokenName() + StrUtil.COLON + token;
        Object userTokenVOObj = redisTemplate.opsForValue().get(tokenCacheKey);

        String userTokenStr = Optional.ofNullable(userTokenVOObj).map(Objects::toString)
                .orElseThrow(() -> new BasicInfoException(BasicInfoStatusEnum.TOKEN_AUTH.getCode(), BasicInfoStatusEnum.TOKEN_AUTH.getDesc()));

        return JSONUtil.toBean(userTokenStr, UserTokenVO.class);
    }

    /**
     * 获取用户token ，从request Authorization中获取
     *
     * @return
     */
    public static UserTokenVO getUserTokenVOByToken() {
        String token = getCurrentUserTokenReal();
        return getUserTokenVOByToken(token);
    }

    /**
     * 获取userId
     *
     * @param token
     * @return userId
     */
    public static Long getUserId(String token) {
        return getUserTokenVOByToken(token).getUserId();
    }

    /**
     * 获取openid
     *
     * @param token
     * @return openid
     */
    public static String getOpenid(String token) {
        return getUserTokenVOByToken(token).getOpenid();
    }


    /**
     * 获取当前请求下 请求头中的 Authorization 或者 请求参数中 token值
     *
     * @return
     */
    private static String getCurrentUserTokenReal() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String token;
        if (request != null) {
            String authToken = request.getHeader("Authorization");
            if (!StringUtils.isEmpty(authToken)) {
                return authToken;
            }
            token = request.getParameter("token");
            if (!StringUtils.isEmpty(token)) {
                return token;
            }
        }
        return null;
    }
}
