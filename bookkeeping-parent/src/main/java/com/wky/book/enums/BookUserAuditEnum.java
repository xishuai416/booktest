package com.wky.book.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 用户加入账本表 tb_book_user   0-无，1-同意，2-拒绝
 */
@Getter
@RequiredArgsConstructor
public enum BookUserAuditEnum implements EnumTemplate  {

    /**
     * 0-无
     */
    NO(0, "无"),

    /**
     * 1-同意
     */
    AGREE(1, "同意"),

    /**
     * 2-拒绝
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
