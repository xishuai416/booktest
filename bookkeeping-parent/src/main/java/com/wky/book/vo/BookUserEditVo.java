package com.wky.book.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class BookUserEditVo {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("系统id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("账本外键")
    private Long bookId;

    @ApiModelProperty("用户外键")
    private Long userId;

    @ApiModelProperty("姓名-(三表，三弄)")
    private String userName;

    @ApiModelProperty("用户别名")
    private String reallyName;

    @ApiModelProperty("头像地址")
    private String avatarUrl;

    @ApiModelProperty("创建人")
    private Long createBy;


    @ApiModelProperty("是否管理员权限（0-否，1是）")
    private Long defaultBook;


}
