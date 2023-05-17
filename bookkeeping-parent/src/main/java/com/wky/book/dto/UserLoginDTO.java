package com.wky.book.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 用户登录，请求
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserLoginDTO implements Serializable {

	private static final long serialVersionUID = -6339752437518575690L;

	/**
	 * 用户编号
	 */
	private Long id;
	
	/**
	 * openid
	 */
	private String openid;

	/**
	 * 用户昵称
	 */
	private String nickName;

	/**
	 * 性别，0-未知、1-男性、2-女性
	 */
	private Integer gender;

	/**
	 * 头像地址
	 */
	private String avatarUrl;
	
	/**
	 * 手机号
	 */
	private String tel;
	
}
