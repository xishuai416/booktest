package com.wky.book.common.constant;

public class XxlConfConstant {

	/**
	 * 项目-预支付服务平台
	 */
	public static final String PROJECT_PREPAID = "prepaid.";

	/**
	 * 微信小程序相关
	 *
	 */
	public static class WxMnp {

		/**
		 * 微信小程序 appId
		 */
		public static final String WXMNP_APPID = PROJECT_PREPAID + "wxmnp.appid";

		/**
		 * 微信小程序 appSecret
		 */
		public static final String WXMNP_SECRET = PROJECT_PREPAID + "wxmnp.secret";
	}

	/**
	 * 微信支付相关
	 *
	 */
	public static class WxPay {

		/**
		 * 微信支付，商户号
		 */
		public static final String WXPAY_MCHID = PROJECT_PREPAID + "wxpay.mchid";

		/**
		 * 微信支付，密钥
		 */
		public static final String WXPAY_KEY = PROJECT_PREPAID + "wxpay.key";

		/**
		 * 微信支付，支付结果通知回调地址
		 */
		public static final String WXPAY_NOTIFY_URL = PROJECT_PREPAID + "wxpay.notify.url";

		/**
		 * 微信支付，退款结果通知回调地址
		 */
		public static final String WXPAY_REFUND_NOTIFY_URL = PROJECT_PREPAID + "wxpay.refund.notify.url";

	}

}
