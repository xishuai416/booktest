package com.wky.book.request;

import java.io.Serializable;

import lombok.Data;

/**
 * 用户登录接口，请求实体
 *
 */
@Data
public class UserLoginReqVO implements Serializable {

	private static final long serialVersionUID = 4044771749375371123L;

	/**
	 * 用户登录凭证
	 */
	private String code;

	/**
	 * 不包括敏感信息的原始数据字符串，用于计算签名
	 */
	private String rawData;

	/**
	 * 使用 sha1(rawData + sessionkey) 得到字符串，用于校验用户信息
	 */
	private String signature;

	/**
	 * 包括敏感数据在内的完整用户信息的加密数据
	 */
	private String encryptedData;

	/**
	 * 加密算法的初始向量
	 */
	private String iv;

}
