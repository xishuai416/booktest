package com.wky.book.request;

import java.io.Serializable;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 获取用户手机号接口，请求实体
		 *
		 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class GetUserTelReqVO implements Serializable {

	private static final long serialVersionUID = -1763578073884403086L;

	/**
	 * 动态令牌。可通过动态令牌换取用户手机号
	 */
	private String code;

	/**
	 * 包括敏感数据在内的完整用户信息的加密数据
	 */
	private String encryptedData;

	/**
	 * 加密算法的初始向量
	 */
	private String iv;

}
