package com.wky.book.response;

import lombok.Data;

/**
 * 我的总支出与收入
 */
@Data
public class MeMoneyCount {
    private String income;
    private String expenditure;
}
