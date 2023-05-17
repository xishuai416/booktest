package com.wky.book.controller;


import com.wky.book.common.exception.BasicInfoException;
import com.wky.book.common.exception.BasicInfoStatusEnum;
import com.wky.book.common.response.BaseResult;
import com.wky.book.common.response.ResultUtils;
import com.wky.book.entity.BookMoney;
import com.wky.book.request.BookMoneyQueryReqVo;
import com.wky.book.request.UserMoneyStatisticsRequestVo;
import com.wky.book.response.BookMoneyCountRes;
import com.wky.book.service.BookMoneyService;
import com.wky.book.vo.ResultPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.*;
import java.util.List;
/**
 * <p>
 * 账本记账金额表 前端控制器
 * </p>
 *
 * @author wky
 * @since 2022-04-02
 */
@RestController
@RequestMapping("/api/money")
public class BookMoneyController {

    @Autowired
    BookMoneyService bookMoneyService;

    /**
     * 查询统计
     */
    // TODO: 2022/4/5 0005  是否分页，待考虑 
    @PostMapping("/findPage")
    public BaseResult<ResultPage<BookMoney>> findPage(@Valid @RequestBody BookMoneyQueryReqVo bookMoneyQueryReqVo) {
        return bookMoneyService.findPage(bookMoneyQueryReqVo);

    }

    /**
     * 查询统计/user/send/apply
     */
    // TODO: 2022/4/5 0005  是否分页，待考虑
    @PostMapping("/findList")
    public BaseResult<BookMoneyCountRes> findList(@Valid @RequestBody BookMoneyQueryReqVo bookMoneyQueryReqVo) {
        return ResultUtils.success(bookMoneyService.findList(bookMoneyQueryReqVo));

    }


    /**
     * 分组统计账本下用户的收入
     */
    @PostMapping("/findUserStatisticsReport")
    public BaseResult<List<String>> findUserStatistics(@Valid @RequestBody UserMoneyStatisticsRequestVo userMoneyStatisticsRequestVo, HttpServletRequest request) {
        try {
            return ResultUtils.success(bookMoneyService.findUserStatistics(userMoneyStatisticsRequestVo, request));
        } catch (IOException e) {
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "文件上传失败");
        }
    }


    /**
     * 账本明细导出
     */
    @PostMapping("/findDetailedStatisticsReport")
    public BaseResult<List<String>> findDetailedStatisticsReport(@Valid @RequestBody BookMoneyQueryReqVo bookMoneyQueryReqVo, HttpServletRequest request) {
        return ResultUtils.success(bookMoneyService.findDetailedStatisticsReport(bookMoneyQueryReqVo, request));
    }



    /**
     * 记一笔
     */
    @PostMapping("/save")
    public void userAddBook(@Valid @RequestBody BookMoney bookMoney) {
        bookMoneyService.addSave(bookMoney);
        ResultUtils.success();
    }

    /**
     * 修改一笔，算错数的时候，会修改金额
     */
    @PostMapping("/update")
    public BaseResult update(@Valid @RequestBody BookMoney bookMoney) {
        return bookMoneyService.update(bookMoney);

    }

    /**
     * 删除一笔
     */
    @GetMapping("/delete")
    public void delete(@RequestParam("moneyId") Long moneyId) {
        bookMoneyService.delete(moneyId);
        ResultUtils.success();
    }

    /**
     * 我个人的所有支出与收入
     */
    @GetMapping("/my/money/count")
    public Object myMoneyCount() {
        return ResultUtils.success(bookMoneyService.myMoneyCount());
    }

    /**
     * 富文本详情
     */
    @GetMapping("/my/money/detailDesc")
    public Object getDetailDesc(@RequestParam("moneyId") Long moneyId) {
        return ResultUtils.success(bookMoneyService.getDetailDesc(moneyId));
    }




}
