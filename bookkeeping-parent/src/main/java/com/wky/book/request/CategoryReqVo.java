package com.wky.book.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 新建分类入参
 */
@Data
@ApiModel(value = "新建分类入参", description = "新建分类入参")
public class CategoryReqVo {

    @NotNull(message = "分类名称不能为空")
    @ApiModelProperty("分类名称")
    private String name;

    @NotNull(message = "icon不能为空")
    @ApiModelProperty("icon")
    private String icon;


    @NotNull(message = "收入支出类型不能为空")
    @ApiModelProperty("类型")
    private Long type;

    @NotNull(message = "账本id不能为空")
    @ApiModelProperty("账本id")
    private Long  bookId;

}
