package com.wky.book.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 0-无，1-同意，2-拒绝
 */
@Getter
@RequiredArgsConstructor
public enum BookUserAuditStatusEnum implements EnumTemplate  {

    /**
     * 0-无
     */
    DEFAULT(0, "无"),

    /**
     * 1-需要管理员审核
     */
    ARGEE(1, "同意"),

    /**
     * 1-需要管理员审核
     */
    REJECT(2, "拒绝");

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
