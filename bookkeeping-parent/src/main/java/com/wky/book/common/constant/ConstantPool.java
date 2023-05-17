package com.wky.book.common.constant;

import lombok.Data;

public class ConstantPool {

    public static String cacheTokenName = "bookCacheTokenName";

    public static String cachePrefix = "bookCachePrefix";

    public static String getCacheTokenName() {
        return cacheTokenName;
    }

    public static String getCachePrefix() {
        return cachePrefix;
    }
}
