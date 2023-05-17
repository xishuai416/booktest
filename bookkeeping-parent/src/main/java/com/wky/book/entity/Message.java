package com.wky.book.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
@TableName("tb_message")
@ApiModel(value = "Message对象", description = "文案表")
public class Message {

    @ApiModelProperty("系统id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 消息
    private String message;

    // 1-首页轮播，2-底部技术支持 3-关于我们
    private Long type;

    // 启用状态（0-禁用 ，1-启用）
    private String status;

    // 转跳url
    private String url;



}
