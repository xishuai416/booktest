package com.wky.book.vo;

import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 查询我的审核，我的申请mapper.xml层入参
 */
@Data
@AllArgsConstructor
public class BookUserVo {


    private List<Long> bookIds;

    private Integer status;


    private Long userId;

    public BookUserVo() {
    }

    public BookUserVo(List<Long> bookIds, Integer status) {
        this.bookIds = bookIds;
        this.status = status;
    }
}
