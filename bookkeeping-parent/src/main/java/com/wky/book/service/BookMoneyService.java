package com.wky.book.service;

import com.wky.book.common.response.BaseResult;
import com.wky.book.entity.BookMoney;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wky.book.request.BookMoneyQueryReqVo;
import com.wky.book.request.UserMoneyStatisticsRequestVo;
import com.wky.book.response.BookMoneyCountRes;
import com.wky.book.response.UserMoneyStatisticsRes;
import com.wky.book.vo.ResultPage;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 账本记账金额表 服务类
 * </p>
 *
 * @author wky
 * @since 2022-04-02
 */
public interface BookMoneyService extends IService<BookMoney> {

    /**
     * 记一笔
     * @param bookMoney
     */
    void addSave(BookMoney bookMoney);

    /**
     * 删除一笔
     * @param moneyId
     */
    void delete(Long moneyId);

    /**
     * 修改一笔
     * @param bookMoney
     */
    BaseResult update(BookMoney bookMoney);

    /**
     * 查询统计
     * @param bookMoneyQueryReqVo
     * @return
     */
    BaseResult<ResultPage<BookMoney>> findPage(BookMoneyQueryReqVo bookMoneyQueryReqVo);

    BookMoneyCountRes findList(BookMoneyQueryReqVo bookMoneyQueryReqVo);

    /**
     * 分组统计账本下用户的收入
     * @param userMoneyStatisticsRequestVo
     * @return
     */
    List<String> findUserStatistics(UserMoneyStatisticsRequestVo userMoneyStatisticsRequestVo, HttpServletRequest request) throws IOException;


    /**
     * 账本导出收入支出明细
     * @param bookMoneyQueryReqVo
     * @param request
     * @return
     */
    List<String> findDetailedStatisticsReport(BookMoneyQueryReqVo bookMoneyQueryReqVo, HttpServletRequest request);

    /**
     * 我个人的所有支出与收入
     * @return
     */
    Object myMoneyCount();

    /**
     * 富文本详情
     * @param moneyId
     * @return
     */
    Object getDetailDesc(Long moneyId);
}
