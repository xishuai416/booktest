package com.wky.book.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * ————————————————
 * 版权声明：本文为CSDN博主「万米高空」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
 * 原文链接：https://blog.csdn.net/zhanglf02/article/details/89916420
 *
 * @param <T>
 */
@Data
@AllArgsConstructor
public class ResultPage<T> {

    /**
     * 总条数
     */
    private Long totalCount;

    /**
     * 总页数
     */
    private Integer totalPage;

    /**
     * 当前页
     */
    private Integer pageNumber;

    private Integer pageSize;

    /**
     * 分页数据
     */
    private List<T> list;
}