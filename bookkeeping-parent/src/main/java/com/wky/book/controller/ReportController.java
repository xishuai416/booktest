package com.wky.book.controller;

import com.wky.book.common.response.BaseResult;
import com.wky.book.common.response.ResultUtils;
import com.wky.book.request.ReportMoneyVo;
import com.wky.book.response.ReportCategoryRes;
import com.wky.book.response.ReportMoneyRes;
import com.wky.book.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 图表统计
 * @author weikaiyu
 * @date 2023/1/31 11:15
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {
    @Autowired
    ReportService reportService;
    /**
     * 图表统计返回
     *
     *
     * @return
     */
    @PostMapping("/list")
    public BaseResult list(@RequestBody ReportMoneyVo reportMoneyVo) {
        List<ReportMoneyRes> reportCategoryRes=reportService.list(reportMoneyVo);
        return ResultUtils.success(reportCategoryRes);
    }


}
