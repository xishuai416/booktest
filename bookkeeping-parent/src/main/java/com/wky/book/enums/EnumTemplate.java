package com.wky.book.enums;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 枚举接口
 *
 */
public interface EnumTemplate {

    /**
     * value
     *
     * @return
     */
    Integer getValue();

    /**
     * title
     *
     * @return
     */
    String getTitle();

    /**
     * 查找，并返回{@link Optional<EnumTemplate>}
     *
     * @param array 枚举所有值
     * @param value 查找值
     * @return {@link Optional<EnumTemplate>}
     */
    static Optional<EnumTemplate> match(EnumTemplate[] array, Integer value) {
        return Stream.of(array).filter(t -> t.getValue() == value).findFirst();
    }

    /**
     * 查找，找到返回{@link EnumTemplate}，否则抛出{@link IllegalArgumentException}异常
     *
     * @param array 枚举所有值
     * @param value 查找值
     * @return {@link EnumTemplate}
     */
    static EnumTemplate getByValue(EnumTemplate[] array, Integer value) {
        return match(array, value).orElseThrow(() -> new IllegalArgumentException("[" + value + "]未定义"));
    }

    /**
     * 查找，找到返回true，否则返回false
     *
     * @param array 枚举所有值
     * @param value 查找值
     * @return true-存在；false-不存在
     */
    static boolean existsByValue(EnumTemplate[] array, Integer value) {
        return match(array, value).isPresent();
    }

    /**
     * 查找，找到返回{@link EnumTemplate}，否则抛出X类型异常
     *
     * @param array    枚举所有值
     * @param value    查找值
     * @param supplier {@link Supplier}
     * @param <X>      返回异常类型
     * @return {@link EnumTemplate}
     */
    static <X extends RuntimeException> EnumTemplate getByValue(EnumTemplate[] array, Integer value, Supplier<? extends X> supplier) {
        return match(array, value).orElseThrow(supplier);
    }
}
