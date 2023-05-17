package com.wky.book.response;

import lombok.Data;

/**
 * 支出分类排行
 *
 * @author weikaiyu
 * @date 2023/1/31 11:26
 */
@Data
public class ReportCategoryRes {
    // 分类图标
    private String icon;

    // 分类名称
    private String name;

    // 占比
    private String rate;

    // 统计金额
    private Long money;
}
