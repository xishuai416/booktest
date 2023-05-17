package com.wky.book.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 0-删除，1-正常，2-禁用（待审核）
 */
@Getter
@RequiredArgsConstructor
public enum DataStatusEnum implements EnumTemplate  {

    /**
     * 0-删除
     */
    DELETE(0, "删除"),

    /**
     * 1-正常
     */
    ENABLE(1, "正常"),

    /**
     * 2-禁用（待审核）
     */
    DISABLE(2, "禁用"),

    /**
     * 3-拒绝
     */
    REJECT(3, "拒绝");

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
