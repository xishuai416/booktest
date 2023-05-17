package com.wky.book.request;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 图表统计入参
 *
 * @author weikaiyu
 * @date 2023/1/31 11:17
 */
@Data
public class ReportMoneyVo {


    @ApiModelProperty("查询起始时间")
    private String startTime;

    @ApiModelProperty("查询结束时间，时间范围时使用")
    private String endTime;

    @NotNull(message = "查询类型不能为空")
    @ApiModelProperty("查询类型，（0-周 1-月 2-年）")
    private Integer timeType;

    @NotNull(message = "收入支出类型不能为空")
    @ApiModelProperty("1-支出，0-收入")
    private Integer type;

    @ApiModelProperty("账本id")
//    private List<String> bookId;
    // 暂时单选
    private String bookId;

}
