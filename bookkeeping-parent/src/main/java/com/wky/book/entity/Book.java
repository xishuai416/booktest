package com.wky.book.entity;

import javax.validation.constraints.NotNull;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wky.sensitive.annotation.Replace;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 账本表
 * </p>
 *
 * @author wky
 * @since 2022-04-02
 */
@Data
@TableName("tb_book")
@ApiModel(value = "Book对象", description = "账本表")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("系统id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("账本名称")
    @NotNull(message = "账本名称不能为空")
    @Replace
    private String bookName;

    @ApiModelProperty("账本图片")
    private String bookAvatar;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    @ApiModelProperty("成员加入账本是否需要审核（0-不需要 1-需要管理员审核）")
    private Integer userAudit;


    @ApiModelProperty("是否允许对外搜索私密账本（0-私密不允许，1-公开允许）")
    private Integer showSearch;

    // 是否默认账本
    @TableField(exist = false)
    private Integer defaultBook;

    @ApiModelProperty("创建人")
    private Long createBy;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @ApiModelProperty("更新人")
    private Long updateBy;


    @ApiModelProperty("0-删除，1-正常，2-禁用")
    private Integer status;


    @Override
    public String toString() {
        return "Book{" +
            "id=" + id +
            ", bookName=" + bookName +
            ", bookAvatar=" + bookAvatar +
            ", createTime=" + createTime +
            ", createBy=" + createBy +
            ", updateTime=" + updateTime +
            ", updateBy=" + updateBy +
        "}";
    }
}
