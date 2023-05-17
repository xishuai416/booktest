package com.wky.book.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 用户加入账本入参实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserApplyBookReqVo {
    private Long bookId;

    private Integer userId;

    private String remark;

}
