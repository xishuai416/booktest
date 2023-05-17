package com.wky.book.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 查询分类vo入参
 * 2022-04-13
 */
@Data
public class CategoryQueryReqVo {

    @NotNull(message = "收入支出类型不能为空")
    private Long type;


    @NotNull(message = "账本id不能为空")
    private Long bookId;


}
