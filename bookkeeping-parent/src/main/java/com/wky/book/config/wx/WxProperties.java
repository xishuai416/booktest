package com.wky.book.config.wx;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * 微信相关配置属性
 */
@Data
@ConfigurationProperties(prefix = "wx")
public class WxProperties {

	/**
	 * 小程序
	 */
	private Mnp mnp;

	/**
	 * 支付
	 */
	private Pay pay;

	@Data
	public static class Mnp {

		/**
		 * 微信小程序的appid
		 */
		private String appid;

		/**
		 * 微信小程序的Secret
		 */
		private String secret;

	}

	@Data
	public static class Pay {

		/**
		 * 微信小程序的appid
		 */
		private String appid;

		/**
		 * 微信支付商户号
		 */
		private String mchId;

		/**
		 * 微信支付商户号密钥
		 */
		private String mchKey;
		
		/**
		 * 微信支付p12证书文件路径
		 */
		private String keyPath;

	}

}
