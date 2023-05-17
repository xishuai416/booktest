package com.wky.book.request;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 统计我的金额查询入参
 * <p>
 * 2022-05-02 22:12:50
 */
@Data
public class UserMoneyStatisticsRequestVo {

    private Long bookId;

    private String userName;

    private Integer type;


    @ApiModelProperty("0-image  1-xlsx")
    private Integer reportType=0;


    @ApiModelProperty("查询开始时间")
    private String startQueryTime;


    @ApiModelProperty("查询结束时间")
    private String endQueryTime;

}
