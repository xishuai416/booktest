package com.wky.book.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wky.book.common.exception.BasicInfoException;
import com.wky.book.common.exception.BasicInfoStatusEnum;
import com.wky.book.entity.Book;
import com.wky.book.entity.BookMoney;
import com.wky.book.entity.Category;
import com.wky.book.enums.DataStatusEnum;
import com.wky.book.enums.ReportTypeEnum;
import com.wky.book.mapper.BookMapper;
import com.wky.book.mapper.BookMoneyMapper;
import com.wky.book.mapper.CategoryMapper;
import com.wky.book.request.ReportMoneyVo;
import com.wky.book.response.ReportCategoryRes;
import com.wky.book.response.ReportMoneyRes;
import com.wky.book.service.CategoryService;
import com.wky.book.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author weikaiyu
 * @date 2023/1/31 11:31
 */
@Service
//public class ReportServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ReportService {
public class ReportServiceImpl implements ReportService {

    private static final Logger logger = LoggerFactory.getLogger(BookUserServiceImpl.class);
    @Autowired
    BookMoneyMapper bookMoneyMapper;

    @Autowired
    BookMapper bookMapper;

    @Override
    public List<ReportMoneyRes> list(ReportMoneyVo reportMoneyVo) {

        List<ReportMoneyRes> reportCategoryResList = new ArrayList<>();
        List<BookMoney> bs = bookMoneyMapper.selectList(new QueryWrapper<BookMoney>().lambda()
                .ge(null != reportMoneyVo.getStartTime(), BookMoney::getBookTime, reportMoneyVo.getStartTime())
                .le(null != reportMoneyVo.getEndTime(), BookMoney::getBookTime, reportMoneyVo.getEndTime())
                .eq(BookMoney::getType, reportMoneyVo.getType())
                .eq(BookMoney::getStatus, DataStatusEnum.ENABLE.getValue())
                //  暂时用eq，改造成多选再用 in
                .eq(null != reportMoneyVo.getBookId(), BookMoney::getBookId, reportMoneyVo.getBookId())
        );

        Map<Long, List<BookMoney>> collect = bs.stream().collect(Collectors.groupingBy(BookMoney::getBookId));
        for (Map.Entry<Long, List<BookMoney>> longListEntry : collect.entrySet()) {
            // 账本id
            Long bookId = longListEntry.getKey();
            Book book = bookMapper.selectById(bookId);
            if (null == book) {
                logger.error("获取图表数据失败，账本不存在，id:{}", bookId);
                throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                        "账本不存在");
            }
            String bookName = book.getBookName();
            String colTitle = "";
            List<BookMoney> bookMoneyList = longListEntry.getValue();
            if (ReportTypeEnum.YEAR.getValue().equals(reportMoneyVo.getTimeType())) {
                // 统计年
                bookMoneyList.stream().forEach(x -> x.setBookTimeDesc(DateUtil.format(x.getBookTime(), "MM")));
                colTitle = ReportTypeEnum.YEAR.getTitle();
            } else if (ReportTypeEnum.MONTH.getValue().equals(reportMoneyVo.getTimeType())) {
                // 统计月，查出来的只有一个月的数据，只要格式化 ”天“ 就行
                bookMoneyList.stream().forEach(x -> x.setBookTimeDesc(DateUtil.format(x.getBookTime(), "dd")));
                colTitle = ReportTypeEnum.MONTH.getTitle();
            } else if (ReportTypeEnum.WEEK.getValue().equals(reportMoneyVo.getTimeType())) {
                colTitle = ReportTypeEnum.WEEK.getTitle();
                bookMoneyList.stream().forEach(x -> {
                    // 统计周
                    try {
                        String bookTimeDesc = dayOfWeek(new SimpleDateFormat("yyyy-MM-dd").parse(reportMoneyVo.getStartTime()));
                        x.setBookTimeDesc(bookTimeDesc);
                    } catch (ParseException e) {
                        logger.error("根据时间转换为星期错误");
                        throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                                "时间转换星期错误");
                    }
                });

            }
            Map<String, List<BookMoney>> bookTimeMap = bookMoneyList.stream().collect(Collectors.groupingBy(BookMoney::getBookTimeDesc));
            ReportMoneyRes reportMoneyRes = new ReportMoneyRes();
            // 设置x轴的行值
            reportMoneyRes.setCol(buildCol(colTitle, reportMoneyVo.getStartTime(), reportMoneyVo.getEndTime()));
            // 根据x轴行值统计数据
            List<String> col = reportMoneyRes.getCol();
            List<String> data = new ArrayList<>();
            // 生成数据值
            for (int i = 0; i < col.size(); i++) {
                List<BookMoney> monies = bookTimeMap.get(col.get(i));
                if (null != monies) {
                    LongSummaryStatistics longSummaryStatistics = monies.stream().mapToLong((s) -> s.getMoney()).summaryStatistics();
                    long sum = longSummaryStatistics.getSum();
                    data.add(new BigDecimal(sum).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                } else {
                    data.add("0");
                }

            }
            // 分类支出排行统计
            Map<String, List<BookMoney>> categoryNameCollect = bs.stream().collect(Collectors.groupingBy(BookMoney::getCategoryName));
            // 获取总数
            long sums = bs.stream().map(BookMoney::getMoney).mapToLong(s -> s).sum();
            List<ReportCategoryRes> reportCategoryRes = new ArrayList<>();
            for (Map.Entry<String, List<BookMoney>> categoryName : categoryNameCollect.entrySet()) {
                List<BookMoney> value = categoryName.getValue();
                if (null != value) {
                    LongSummaryStatistics longSummaryStatistics = value.stream().mapToLong((s) -> s.getMoney()).summaryStatistics();
                    long sum = longSummaryStatistics.getSum();
                    ReportCategoryRes categoryRes = new ReportCategoryRes();
                    categoryRes.setIcon(value.get(0).getAvatarUrl());
                    categoryRes.setName(categoryName.getKey());
                    categoryRes.setMoney(sum);
                    // 如果是0.00%,会帮你计算好百分比，比如 0.00： 50/100=0.50  0.00%： 50/100=0.50*100=50%
                    categoryRes.setRate(new DecimalFormat("0.00").format((float) sum / (float) sums));
                    reportCategoryRes.add(categoryRes);
                }
            }
            reportMoneyRes.setCategories(reportCategoryRes);
            reportMoneyRes.setBookName(bookName);
            // set数据值
            reportMoneyRes.setData(data);
            reportCategoryResList.add(reportMoneyRes);
        }

        // 构造一条全是0的数据
        if (reportCategoryResList.size() == 0) {
            ReportMoneyRes reportMoneyRes = new ReportMoneyRes();
            String colTitle = "year";
            if (ReportTypeEnum.YEAR.getValue().equals(reportMoneyVo.getTimeType())) {
                // 统计年
                colTitle = ReportTypeEnum.YEAR.getTitle();
                List<String> data = new ArrayList<>();
                for (int i = 0; i < 12; i++) {
                    data.add("0");
                }
                reportMoneyRes.setData(data);
            } else if (ReportTypeEnum.MONTH.getValue().equals(reportMoneyVo.getTimeType())) {
                colTitle = ReportTypeEnum.MONTH.getTitle();
                List<String> data = new ArrayList<>();
                int daysOfMonth = 0;
                try {
                    daysOfMonth = getDaysOfMonth(new SimpleDateFormat("yyyy-MM-dd").parse(reportMoneyVo.getStartTime()));
                } catch (ParseException e) {
                    logger.error("根据月份获取天数错误");
                    throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                            "处理月份错误");
                }
                for (int i = 0; i < daysOfMonth; i++) {
                    data.add("0");
                }
                reportMoneyRes.setData(data);
            } else if (ReportTypeEnum.WEEK.getValue().equals(reportMoneyVo.getTimeType())) {
                //
                colTitle = ReportTypeEnum.WEEK.getTitle();
            }
            reportMoneyRes.setCol(buildCol(colTitle, reportMoneyVo.getStartTime(), reportMoneyVo.getEndTime()));
            Book book = bookMapper.selectById(reportMoneyVo.getBookId());
            if (null == book) {
                logger.error("获取图表数据失败，账本不存在，id:{}", reportMoneyVo.getBookId());
                throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                        "账本不存在");
            }
            reportMoneyRes.setBookName(book.getBookName());
            reportMoneyRes.setCategories(new ArrayList<>());
            reportCategoryResList.add(reportMoneyRes);
        }
        return reportCategoryResList;
    }

    private List<String> buildCol(String text, String startTime, String endTime) {
        List<String> cols = new ArrayList<>();
        if (text.equals(ReportTypeEnum.YEAR.getTitle())) {
            for (int i = 1; i <= 12; i++) {
                if (i < 10)
                    cols.add("0" + i);
                else
                    cols.add(String.valueOf(i));
            }
        } else if (text.equals(ReportTypeEnum.MONTH.getTitle())) {
            try {
                int daysOfMonth = getDaysOfMonth(new SimpleDateFormat("yyyy-MM-dd").parse(startTime));
                for (int i = 1; i <= daysOfMonth; i++) {
                    if (i < 10)
                        cols.add("0" + i);
                    else
                        cols.add(String.valueOf(i));
                }
            } catch (ParseException e) {
                logger.error("根据月份获取天数错误");
                throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                        "处理月份错误");
            }
        } else if (text.equals(ReportTypeEnum.WEEK.getTitle())) {
            Collections.addAll(cols, "周一", "周二", "周三", "周四", "周五", "周六", "周天");
        }
        return cols;
    }

    /**
     * 返回星期几
     *
     * @param date
     * @return
     */
    public static String dayOfWeek(Date date) {
        int day = DateUtil.dayOfWeek(date);
        switch (day) {
            case 1:
                return "周天";
            case 2:
                return "周一";
            case 3:
                return "周二";
            case 4:
                return "周三";
            case 5:
                return "周四";
            case 6:
                return "周五";
            case 7:
                return "周六";
            default:
                return "";
        }
    }

    /**
     * 获取天数
     *
     * @param date 输入 2015-02-2
     * @return 输出n天
     */
    public static int getDaysOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
}
