package com.wky.book.response;

import lombok.Data;

import java.util.List;

/**
 * 返回图表统计数据
 *
 * @author weikaiyu
 * @date 2023/1/31 11:21
 */
@Data
public class ReportMoneyRes {

    private String bookName;

    // x轴数据（月1-31，周一至周七 ，年1-12）
    private List<String> col;


    // 对应categories的统计求和数据
    private List<String> data;


    // 支出分类排行
    private List<ReportCategoryRes> categories;

    // 成员支出，暂不开发


}
