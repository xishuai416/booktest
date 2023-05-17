package com.wky.book.config.wx;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import cn.binarywang.wx.miniapp.api.WxMaMsgService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaMsgServiceImpl;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import lombok.RequiredArgsConstructor;

/**
 * 微信相关配置
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(WxProperties.class)
public class WxConfiguration {

	private final WxProperties wxProperties;

	@Bean
	public WxMaService wxMaService() {
		WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
		config.setAppid(this.wxProperties.getMnp().getAppid());
		config.setSecret(this.wxProperties.getMnp().getSecret());

		WxMaService service = new WxMaServiceImpl();
		service.setWxMaConfig(config);
		return service;
	}

	@Bean
	public WxMaMsgService wxMaMsgService() {
		return new WxMaMsgServiceImpl(wxMaService());
	}


}
