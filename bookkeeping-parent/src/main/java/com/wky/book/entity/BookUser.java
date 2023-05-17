package com.wky.book.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wky.sensitive.annotation.Replace;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Table;

/**
 * <p>
 * 账本用户表
 * </p>
 *
 * @author wky
 * @since 2022-04-02
 */
@Data
@Accessors(chain = true)
@TableName("tb_book_user")
@ApiModel(value = "BookUser对象", description = "账本用户表")
public class BookUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("系统id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("账本外键")
    private Long bookId;

    @ApiModelProperty("用户外键")
    private Long userId;

    @ApiModelProperty("真实姓名")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Replace
    private String userName;

    @ApiModelProperty("用户别名")
    @Replace
    private String reallyName;

    @ApiModelProperty("头像地址")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String avatarUrl;

    @ApiModelProperty("创建人")
    private Long createBy;


    @ApiModelProperty("是否管理员权限（0-否，1是）")
    private Integer defaultBook;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("更新人")
    private Long updateBy;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty("是否管理员权限（0-成员，1-管理员,2群主）")
    private Integer auth;


    @ApiModelProperty("数据状态（0-否，1是）")
    private Integer status;

    @ApiModelProperty("0-无，1-同意，2-拒绝")
    private Integer auditStatus;


    @ApiModelProperty("加入账本描述备注")
    private String remark;




    @ApiModelProperty("账本名称")
    @TableField(exist = false)
    private String bookName;


    @Override
    public String toString() {
        return "BookUser{" +
            "id=" + id +
            ", bookId=" + bookId +
            ", userId=" + userId +
            ", createBy=" + createBy +
            ", createTime=" + createTime +
            ", updateBy=" + updateBy +
            ", updateTime=" + updateTime +
        "}";
    }
}
