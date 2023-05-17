package com.wky.book.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 查询类型，（0-周 1-月 2-年）
 *
 * @author weikaiyu
 * @date 2023/1/31 11:39
 */
@Getter
@RequiredArgsConstructor
public enum ReportTypeEnum implements EnumTemplate {
    /**
     * 0-周
     */
    WEEK(0, "周"),

    /**
     * 1-月
     */
    MONTH(1, "月"),

    /**
     * 2-年
     */
    YEAR(2, "年");

    private final Integer value;

    private final String title;

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String getTitle() {
        return this.title;
    }
}
