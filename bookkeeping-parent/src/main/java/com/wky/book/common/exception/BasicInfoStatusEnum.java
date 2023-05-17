package com.wky.book.common.exception;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务错误状态枚举
 */
public enum BasicInfoStatusEnum {

    /**
     * 成功
     */
    SUCCESS(200, "成功"),

    /**
     * 客户端请求参数非法，用于请求参数逻辑检验不通过情况
     */
    PARAM_ERROR(400, "协议或者参数非法"),

    /**
     * 客户端请求签名验证失败，客户端需按服务端要求签名
     */
    SIGN_ERROR(401, "请求签名验证失败"),

    /**
     * 客户端所请求资源权限不足，要求客户端先授权
     */
    NO_AUTH(403, "请求权限不足"),

    /**
     * 客户端请求资源不存在，请求参数错误或服务端资源尚未准备好
     */
    NOT_FOUND(404, "请求资源不存在"),

    /**
     * 客户端请求不予支持，适用于在请求业务时不符合规则所对应的响应码
     */
    UNSUPPORTED(415, "请求不予支持"),

    /**
     * 请求超过规定频率限制，服务端对应接口有频率限制，客户频繁请求超过了规定限制
     */
    RATE_LIMIT_EXCEEDED(429, "请求超过规定频率限制"),

    /**
     * 客户端请求需要授权token
     */
    TOKEN_AUTH(433, "TOKEN无效"),

    /**
     * 服务端系统错误，一般服务端编码错误或服务端异常情况
     */
    SYSTEM_ERROR(500, "系统错误"),

    /**
     * 服务端请求第三方服务不可用，请求第三方服务下线或第三方服务异常情况
     */
    THIRD_SERVICE_UNAVAILABLE(502, "第三方服务不可用"),

    /**
     * 服务端不可用，
     */
    SERVICE_UNAVAILABLE(503, "服务不可用"),

    /**
     * 未知错误
     */
    UNKNOWN_ERROR(510, "未知错误"),
    /**
     * 文件太大异常
     */
    FILE_MAN_ERROR(550, "文件超过2M");

    private Integer code;

    private String desc;

    private BasicInfoStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    private static Map<Integer, String> enumMap;

    static {
        enumMap = new HashMap<>();
        for (BasicInfoStatusEnum vo : BasicInfoStatusEnum.values()) {
            enumMap.put(vo.code, vo.desc);
        }
    }

    public static boolean isExist(Integer key) {
        return enumMap.containsKey(key);
    }

    public static String getEnumByKey(Integer key) {
        if (null == key) {
            return null;
        }
        return enumMap.get(key);
    }


}
