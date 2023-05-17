package com.wky.book.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 是否管理员权限（0-成员，1-管理员  2-群主创建人）
 */
@Getter
@RequiredArgsConstructor
public enum BookAuthEnum implements EnumTemplate  {

    /**
     * 0-成员
     */
    USER(0, "成员"),

    /**
     * 1-管理员
     */
    ADMIN(1, "管理员"),
    /**
     * 0-成员
     */
    ROOT(2, "群主");

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
