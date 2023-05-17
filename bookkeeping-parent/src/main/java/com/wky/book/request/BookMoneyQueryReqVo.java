package com.wky.book.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 查询金额统计
 */
@Data
@ApiModel(value = "查询金额统计入参", description = "账本查询金额统计入参")
public class BookMoneyQueryReqVo {

    @ApiModelProperty("账本id")
    @NotNull(message = "账本id不能为空")
    private String bookId;


    @ApiModelProperty("查询开始时间")
    private String startQueryTime;


    @ApiModelProperty("查询结束时间")
    private String endQueryTime;

    @ApiModelProperty("收入类型 -1 全部  0 支出 1 收入")
    private Integer type;



    @ApiModelProperty("分类id")
    private List<Integer> categoryIds;


    @ApiModelProperty("0-image  1-xlsx")
    private Integer reportType=0;

    private Integer pageNumber = 1;

    private Integer pageSize = 10;

}
