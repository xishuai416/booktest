package com.wky.book.response;


import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * 统计我的金额查询响应
 * <p>
 * 2022-05-02 22:12:50
 */
@Data
public class UserMoneyStatisticsRes {


    /**
     * 捐款人头像
     */
    private String avatarUrl;

    /**
     * 账本名称
     */
    @Excel(name = "项目名称", height = 10, width = 25)
    private String bookName;

    /**
     * 捐款人姓名
     */
    @Excel(name = "姓名", height = 10, width = 20)
    private String userName;

    /**
     * 总共收入（支出）
     */
    private Long totalMoney;

    @Excel(name = "捐款金额（元）", height = 10, width = 20)
    private String totalMoneyStr;
}
