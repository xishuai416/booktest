package com.wky.book.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 账本记账金额表
 * </p>
 *
 * @author wky
 * @since 2022-04-02
 */
@Data
@TableName("tb_book_money")
@ApiModel(value = "BookMoney对象", description = "账本记账金额表")
public class BookMoney implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty("记账时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "记账时间", height = 10, width = 30, databaseFormat = "yyyyMMddHHmmss", format = "yyyy-MM-dd")
    private Date bookTime;

    @ApiModelProperty("系统id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("账本id")
    @NotNull(message = "账本id不能为空")
    private Long bookId;

    @ApiModelProperty("分类外键id")
    @NotNull(message = "分类id不能为空")
    private Long categoryId;

    @ApiModelProperty("分类名")
    private String categoryName;

    @ApiModelProperty("名称，优先取book_money的userName，那边的userName可能会更改，更改后取那边的可以在首页展示")
    @Excel(name = "姓名", height = 10, width = 20)
    private String userName;

    @ApiModelProperty("捐款人book_money表id")
    private Long userId;

    @ApiModelProperty("分类图标url")
//    @Excel(name = "图标", type = 2 ,width = 40 , height = 20,imageType = 1)
    private String avatarUrl;


    @ApiModelProperty("金额")
    @NotNull(message = "金额不能为空")

    private Long money;
    @Excel(name = "捐款金额", height = 10, width = 20)
    @TableField(exist = false)
    private String moneyStr;

    private Integer status;

    @Excel(name = "收入/支出", height = 10, width = 20)
    @TableField(exist = false)
    private String typeStr;

    @ApiModelProperty("备注")
    @Excel(name = "备注", height = 10, width = 20)
    private String remark;


    @ApiModelProperty("0-支出，1-收入")
    @NotNull(message = "支出方式不能为空")
    private Integer type;


    @ApiModelProperty("创建人")
    private Long createBy;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("更新人")
    private Long updateBy;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty("富文本详情")
    private String detailDesc;

    @ApiModelProperty("记账时间 （转换成 yyyy-MM-dd 或者 yyyy 或者 yyyy-MM格式进行分组 ）")
    @TableField(exist = false)
    private String bookTimeDesc;


    // 是否显示详情
    @ApiModelProperty("是否显示详情 0-否 1-是")
    private String detailDescFlag;


}
