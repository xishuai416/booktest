package com.wky.book.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 是否允许对外搜索私密账本（0-私密不允许，1-公开允许）
 */
@Getter
@RequiredArgsConstructor
public enum BookSearchEnum implements EnumTemplate  {

    /**
     * 0-私密不允许
     */
    NO(0, "私密不允许"),

    /**
     * 1-公开允许
     */
    YES(1, "公开允许");

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
