package com.wky.book.service;

import com.wky.book.request.ReportMoneyVo;
import com.wky.book.response.ReportCategoryRes;
import com.wky.book.response.ReportMoneyRes;

import java.text.ParseException;
import java.util.List;

/**
 * @author weikaiyu
 * @date 2023/1/31 11:30
 */
public interface ReportService {
    List<ReportMoneyRes> list(ReportMoneyVo reportMoneyVo) ;
}
