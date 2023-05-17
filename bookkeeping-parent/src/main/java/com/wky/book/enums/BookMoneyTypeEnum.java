package com.wky.book.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 收入支出类型 0-支出，1-收入
 */
@Getter
@RequiredArgsConstructor
public enum BookMoneyTypeEnum implements EnumTemplate  {

    /**
     * 0-收入
     */
    EXPENDITURE(0, "收入"),

    /**
     * 1-支出
     */
    INCOME(1, "支出");

    private final Integer value;

    private final String title;

    public static String getValue(Integer type) {
        BookMoneyTypeEnum[] carTypeEnums = values();
        for (BookMoneyTypeEnum carTypeEnum : carTypeEnums) {
            if (carTypeEnum.getValue().equals(type)) {
                return carTypeEnum.getTitle();
            }
        }
        return null;
    }

    public static Integer getType(String desc) {
        BookMoneyTypeEnum[] carTypeEnums = values();
        for (BookMoneyTypeEnum carTypeEnum : carTypeEnums) {
            if (carTypeEnum.getTitle().equals(desc)) {
                return carTypeEnum.getValue();
            }
        }
        return null;
    }


    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String getTitle() {
        return this.title;
    }


}
