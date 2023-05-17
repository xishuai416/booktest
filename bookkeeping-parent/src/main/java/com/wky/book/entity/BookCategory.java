package com.wky.book.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <p>
 * 账本分类表
 * </p>
 *
 * @author wky
 * @since 2022-04-02
 */
@Data
@TableName("tb_book_category")
@Accessors(chain = true)
@ApiModel(value = "BookCategory对象", description = "账本分类表")
public class BookCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("系统id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("账本外键")
    private Long bookId;

    @ApiModelProperty("分类外键")
    private Long categoryId;

    @ApiModelProperty("创建人")
    private Long createBy;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人")
    private Long updateBy;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("0-删除，1-正常，2-禁用")
    private String status;

    @Override
    public String toString() {
        return "BookCategory{" +
            "id=" + id +
            ", bookId=" + bookId +
            ", categoryId=" + categoryId +
            ", createBy=" + createBy +
            ", createTime=" + createTime +
            ", updateBy=" + updateBy +
            ", updateTime=" + updateTime +
        "}";
    }
}
