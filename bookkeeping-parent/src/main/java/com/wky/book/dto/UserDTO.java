package com.wky.book.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 用户登录，响应
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserDTO implements Serializable {

	private static final long serialVersionUID = 8856290926994864376L;

	/**
	 * 用户id
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
	 * 手机号码
	 */
	private String tel;

	/**
	 * 已登录凭证
	 */
	private String token;

}
