package com.wky.book.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *成员加入账本是否需要审核（0-不需要 1-需要管理员审核）
 */
@Getter
@RequiredArgsConstructor
public enum BookAuditEnum implements EnumTemplate  {

    /**
     * 0-不需要
     */
    USER(0, "否"),

    /**
     * 1-需要管理员审核
     */
    ADMIN(1, "是");

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
