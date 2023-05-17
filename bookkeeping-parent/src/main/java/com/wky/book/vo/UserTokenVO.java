package com.wky.book.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 缓存 token 保存的信息VO
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserTokenVO implements Serializable {

	private static final long serialVersionUID = 481538785435633986L;

	/**
	 * 用户id
	 */
	private Long userId;

	/**
	 * openid
	 */
	private String openid;

	/**
	 * token
	 */
	private String token;

}
