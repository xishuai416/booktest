package com.wky.book.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.wky.book.enums.BookAuthEnum;
import com.wky.book.enums.DataStatusEnum;
import com.wky.book.ftp.FtpClientManager;
import com.wky.book.mapper.BookMapper;
import com.wky.book.response.CategorySelectList;
import com.wky.book.response.MeMoneyCount;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.spire.xls.Worksheet;
import com.wky.book.common.context.UserTokenContextHolder;
import com.wky.book.common.exception.BasicInfoException;
import com.wky.book.common.exception.BasicInfoStatusEnum;
import com.wky.book.common.response.BaseResult;
import com.wky.book.common.response.ResultUtils;
import com.wky.book.entity.Book;
import com.wky.book.entity.BookMoney;
import com.wky.book.entity.BookUser;
import com.wky.book.entity.Category;
import com.wky.book.enums.BookMoneyTypeEnum;
import com.wky.book.enums.EnumTemplate;
import com.wky.book.mapper.BookMoneyMapper;
import com.wky.book.request.BookMoneyQueryReqVo;
import com.wky.book.request.UserMoneyStatisticsRequestVo;
import com.wky.book.response.BookMoneyCountRes;
import com.wky.book.response.UserMoneyStatisticsRes;
import com.wky.book.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wky.book.util.MoneyConvertUtil;
import com.wky.book.vo.ResultPage;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 账本记账金额表 服务实现类
 * </p>
 *
 * @author wky
 * @since 2022-04-02
 */
@Service
public class BookMoneyServiceImpl extends ServiceImpl<BookMoneyMapper, BookMoney> implements BookMoneyService {


    private static final Logger logger = LoggerFactory.getLogger(BookMoneyServiceImpl.class);
    @Autowired
    UploadFileService uploadFileService;
    @Autowired
    CategoryService categoryService;

    @Autowired
    BookService bookService;

    @Autowired
    BookMoneyMapper bookMoneyMapper;


    @Autowired
    BookUserService bookUserService;


    /**
     * le:小于等于 <=
     * <p>
     * ge:大于等于 >=
     * <p>
     * String format = DateUtil.format(new Date(), "yyyy-MM-dd");
     *
     * @param bookMoneyQueryReqVo
     * @return
     */
    @Deprecated
    @Override
    public BaseResult<ResultPage<BookMoney>> findPage(BookMoneyQueryReqVo bookMoneyQueryReqVo) {
        PageHelper.startPage(bookMoneyQueryReqVo.getPageNumber(), bookMoneyQueryReqVo.getPageSize());
        List<BookMoney> bookMoneyList = this.list(queryWrapper(bookMoneyQueryReqVo));
        bookMoneyList.stream().forEach(x -> x.setBookTimeDesc(DateUtil.format(x.getBookTime(), "yyyy-MM-dd")));
        PageInfo pageInfo = new PageInfo(bookMoneyList);
        return ResultUtils.success(new ResultPage<>(pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getPageNum(), pageInfo.getPageSize(), bookMoneyList));
    }

    @Override
    public BookMoneyCountRes findList(BookMoneyQueryReqVo bookMoneyQueryReqVo) {
        // TODO: 2022/4/30 0030 这里要加上分页
        if (bookMoneyQueryReqVo.getBookId() == null) {
            logger.error("查询失败，id参数未传递,bookId:", bookMoneyQueryReqVo.getBookId());
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "请选择账本进行查询");
        }
        // 分页数据
        BaseResult<ResultPage<BookMoney>> page = findPage(bookMoneyQueryReqVo);
        ResultPage<BookMoney> data = page.getData();


        // count统计数据
//        List<BookMoney> bookMoneyList = this.list(queryWrapper(bookMoneyQueryReqVo));
        List<BookMoney> bookMoneyList = bookMoneyMapper.listMoney(bookMoneyQueryReqVo);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        bookMoneyList.stream().sorted(Comparator.comparing(BookMoney::getCreateTime).reversed()).forEach(x -> x.setBookTimeDesc(formatter.format(x.getBookTime())));
        Long enterSum = bookMoneyList.stream()
                .filter(x -> BookMoneyTypeEnum.EXPENDITURE.getValue().equals(x.getType()))
                .map(BookMoney::getMoney).filter(Objects::nonNull).reduce(0L, Long::sum);
        Long exSum = bookMoneyList.stream()
                .filter(x -> BookMoneyTypeEnum.INCOME.getValue().equals(x.getType()))
                .map(BookMoney::getMoney).filter(Objects::nonNull).reduce(0L, Long::sum);

        // 获取分类集合下拉选
        List<BookMoney> bookMonies = bookMoneyMapper.selectList(new QueryWrapper<BookMoney>().lambda()
                .eq(BookMoney::getStatus, DataStatusEnum.ENABLE.getValue())
                .eq(null != bookMoneyQueryReqVo.getBookId(), BookMoney::getBookId, bookMoneyQueryReqVo.getBookId())
        );
        // 构造分类集合下拉选
        List<CategorySelectList> categorySelectLists = bookMonies.stream().map(x ->
                new CategorySelectList(x.getCategoryName(), x.getCategoryId())
        ).distinct().collect(Collectors.toList());
        Long selectIncome = 0L;
        Long selectExpenditure = 0L;
        // 根据分类过滤的支出
        List<BookMoney> monies = this.list(queryWrapper(bookMoneyQueryReqVo));
        for (int i = 0; i < monies.size(); i++) {
            if (monies.get(i).getType().intValue() == 1 && null != monies.get(i).getMoney()) {
                selectIncome += monies.get(i).getMoney();
            }
            if (monies.get(i).getType() == 0 && monies.get(i).getMoney() != null) {
                selectExpenditure += monies.get(i).getMoney();
            }
        }
        monies.stream().forEach(x -> {

        });
        return new BookMoneyCountRes(exSum, enterSum, categorySelectLists, data, selectIncome, selectExpenditure);
    }

    private Wrapper<BookMoney> queryWrapper(BookMoneyQueryReqVo bookMoneyQueryReqVo) {
        return new QueryWrapper<BookMoney>().lambda()
                .select(BookMoney.class, info -> !info.getColumn().equals("detail_desc"))
                .eq(null != bookMoneyQueryReqVo.getBookId(), BookMoney::getBookId, bookMoneyQueryReqVo.getBookId())
                .in(null != bookMoneyQueryReqVo.getCategoryIds() && bookMoneyQueryReqVo.getCategoryIds().size() != 0, BookMoney::getCategoryId, bookMoneyQueryReqVo.getCategoryIds())
                .eq(BookMoney::getStatus, DataStatusEnum.ENABLE.getValue())
                .eq(null != bookMoneyQueryReqVo.getType() && bookMoneyQueryReqVo.getType().intValue() != -1, BookMoney::getType, bookMoneyQueryReqVo.getType())
                .le(null != bookMoneyQueryReqVo.getEndQueryTime(), BookMoney::getBookTime, bookMoneyQueryReqVo.getEndQueryTime())
                .ge(null != bookMoneyQueryReqVo.getStartQueryTime(), BookMoney::getBookTime, bookMoneyQueryReqVo.getStartQueryTime()
                ).orderByDesc(BookMoney::getBookTime);

    }


    private void warp(List<BookMoney> bookMoneyList) {
        bookMoneyList.stream().forEach(x -> {
            x.setMoneyStr(MoneyConvertUtil.fenToYuan(String.valueOf(x.getMoney())));
            x.setTypeStr(BookMoneyTypeEnum.getValue(x.getType()));
        });
    }

    /**
     * 记一笔
     *
     * @param bookMoney
     */
    @Override
    public void addSave(BookMoney bookMoney) {
        Category category = IFNull(bookMoney.getBookId(), bookMoney.getCategoryId());
        bookMoney.setCategoryName(category.getName());  // 分类名称
        bookMoney.setAvatarUrl(category.getIcon());// 分类图标
        bookMoney.setCreateTime(new Date());
        bookMoney.setStatus(DataStatusEnum.ENABLE.getValue());
        bookMoney.setCreateBy(UserTokenContextHolder.getUserTokenVOByToken().getUserId());
        IFType(bookMoney.getType());
        // 如果富文本值为空，设置0
        if (StringUtils.isEmpty(bookMoney.getDetailDesc())) {
            bookMoney.setDetailDescFlag("0");
        } else {
            bookMoney.setDetailDescFlag("1");
        }
        this.save(bookMoney);
    }

    /**
     * 检验账本，分类是否存在
     *
     * @param bookId
     * @param categoryId
     */
    private Category IFNull(Long bookId, Long categoryId) {
        Book book = bookService.getById(bookId);
        if (null == book) {
            logger.error("记账失败，账本不存在,账本id:{}", bookId);
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "记账失败，账本不存在");
        }
        Category category = categoryService.getById(categoryId);
        if (null == category) {
            logger.error("记账失败，分类不存在，分类id:{}", categoryId);
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "记账失败，分类不存在");
        }
        return category;
    }

    @Override
    public BaseResult update(BookMoney bookMoney) {
//         TODO: 2022/4/22 判断是否是管理员才能修改，否则需要返回权限提示
        BookMoney bookMoneys = this.getById(bookMoney.getId());
        // 判断是否是管理员才能修改，否则需要返回权限提示
        this.isBookAuthor(bookMoneys, "仅管理员修改");
        Category category = IFNull(bookMoney.getBookId(), bookMoney.getCategoryId());
        IFType(bookMoney.getType());
        // TODO: 2022/4/5 0005 账本id目前是不可变
        BeanUtil.copyProperties(bookMoney, bookMoneys, "id", "createTime", "createBy", "book_id");
        bookMoneys.setCategoryName(category.getName());  // 分类名称
        bookMoneys.setAvatarUrl(category.getIcon());// 分类图标
        bookMoney.setUpdateBy(UserTokenContextHolder.getUserTokenVOByToken().getUserId()); // 更新人
        // 如果富文本值为空，设置0，否则设置1
        if (StringUtils.isEmpty(bookMoneys.getDetailDesc())) {
            bookMoneys.setDetailDescFlag("0");
        } else {
            bookMoneys.setDetailDescFlag("1");
        }
        this.updateById(bookMoneys);
        return ResultUtils.success();
    }


    /**
     * 校验收入类型是否合法
     *
     * @param type
     */
    public void IFType(Integer type) {
        if (!EnumTemplate.existsByValue(BookMoneyTypeEnum.values(), Integer.valueOf(type))) {
            logger.error("参数:type: {} 不合法,只能传递0,1", type);
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "参数:type:" + type + "不合法,只能传递0,1");
        }
    }


    @Override
    public void delete(Long moneyId) {
        BookMoney bookMoney = this.getById(moneyId);
        if (null == bookMoney) {
            logger.error("账本流水（账本金额）不存在，book_money的id：{}", moneyId);
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "流水不存在");
        }
        Long userId = UserTokenContextHolder.getUserTokenVOByToken().getUserId();
        // 流水创建人或者管理员才可删除
//        boolean isUser = userId.equals(bookMoney.getCreateBy());
//        bookService.isBookAuthor(userId);
        BookMoney bookMoneys = this.getById(bookMoney.getId());
        this.isBookAuthor(bookMoneys, "仅管理员删除");
        bookMoney.setUpdateBy(userId);
        bookMoney.setStatus(DataStatusEnum.DELETE.getValue());
        this.updateById(bookMoney);
    }

    private void isBookAuthor(BookMoney bookMoneys, String errMeg) {
        Long userId = UserTokenContextHolder.getUserTokenVOByToken().getUserId();
        // 获取到bookId,
        Long bookId = bookMoneys.getBookId();
        BookUser bookUser = bookUserService.getOne(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getUserId, userId)
                .eq(BookUser::getBookId, bookId)
                .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue())
        );
        // 管理员字段为null
        if (null == bookUser.getAuth()) {
            logger.error("用户：{}，在账本：{}的权限值为null", userId, bookId);
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    errMeg);
        }
        // 如果创建人是当前操作人，允许删除
        if (bookMoneys.getCreateBy().equals(userId)) {
            return;
        }
        // 是成员
        if (bookUser.getAuth().equals(BookAuthEnum.USER.getValue())) {
            logger.error("用户：{}，在账本：{}的权限为：{}，{}", userId, bookUser.getAuth(), BookAuthEnum.USER.getTitle());
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    errMeg);
        }
    }

    /**
     * 账本明细导出
     *
     * @param bookMoneyQueryReqVo
     * @param request
     * @return
     */
    @Override
    public List<String> findDetailedStatisticsReport(BookMoneyQueryReqVo bookMoneyQueryReqVo, HttpServletRequest request) {
        Book book = bookService.getById(bookMoneyQueryReqVo.getBookId());
        List<BookMoney> bookMoneyList = bookMoneyMapper.listMoney(bookMoneyQueryReqVo);
        if (bookMoneyList.size() > 0) {
            // 根据支出类型设置为负数
            bookMoneyList.stream().forEach(x -> {
                if (BookMoneyTypeEnum.INCOME.getValue().equals(x.getType())) x.setMoney(-x.getMoney());
            });
//            用户名称列为总计
            BookMoney bookMoney = new BookMoney();
            bookMoney.setUserName("总计");
            bookMoney.setMoney(bookMoneyList.stream().map(BookMoney::getMoney).filter(Objects::nonNull).mapToLong(Long::longValue).sum());
            bookMoneyList.add(bookMoney);
        }
        warp(bookMoneyList);
        try {
            if (bookMoneyQueryReqVo.getReportType().equals(1)) {
                return uploadFileService.exportExcelXls(book.getBookName() + "_账本明细", "_账本明细", "/report", bookMoneyList, BookMoney.class, book.getId().toString());
            } else {
                return uploadFileService.exportExcelImage(book.getBookName() + "_账本明细", "_账本明细", "/report", bookMoneyList, BookMoney.class, book.getId().toString());
            }
        } catch (IOException e) {
            logger.error("用户：{}，导出账本失败,异常：{}", UserTokenContextHolder.getUserTokenVOByToken().getUserId(), e.getMessage());
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "账本明细导出失败");
        }

    }

    /**
     * 根据用户分组导出
     *
     * @param userMoneyStatisticsRequestVo
     * @param request
     * @return
     * @throws IOException
     */
    @Override
    public List<String> findUserStatistics(UserMoneyStatisticsRequestVo userMoneyStatisticsRequestVo, HttpServletRequest request) throws IOException {
        List<UserMoneyStatisticsRes> userStatistics = baseMapper.findUserStatistics(userMoneyStatisticsRequestVo);
        //根据捐款金额排序 && 分转元
        userStatistics.sort(Comparator.comparing(UserMoneyStatisticsRes::getTotalMoney).reversed());
        if (userStatistics.size() > 0) {
//            用户名称列为总计
            UserMoneyStatisticsRes userMoneyStatisticsRes = new UserMoneyStatisticsRes();
            userMoneyStatisticsRes.setUserName("总计");

            userMoneyStatisticsRes.setTotalMoney(userStatistics.stream().map(UserMoneyStatisticsRes::getTotalMoney).filter(Objects::nonNull).mapToLong(Long::longValue).sum());
            userStatistics.add(userMoneyStatisticsRes);
        }
        String title = "项目捐款人汇总";
        if (userStatistics.size() != 0) {
            title = userStatistics.get(0).getBookName();
        }
        userStatistics.stream().forEach(x -> x.setTotalMoneyStr(MoneyConvertUtil.fenToYuan(String.valueOf(x.getTotalMoney()))));
        if (userMoneyStatisticsRequestVo.getReportType().equals(1)) {
            return uploadFileService.exportExcelXls(title + "_汇总", "项目捐款", "/report", userStatistics, UserMoneyStatisticsRes.class, userMoneyStatisticsRequestVo.getBookId().toString());
        } else {
            return uploadFileService.exportExcelImage(title + "_汇总", "项目捐款", "/report", userStatistics, UserMoneyStatisticsRes.class, userMoneyStatisticsRequestVo.getBookId().toString());
        }
    }


    @Override
    public Object myMoneyCount() {
        // 获取用户id
        Long userId = UserTokenContextHolder.getUserTokenVOByToken().getUserId();
        List<Long> bookUserId = bookUserService.list(new QueryWrapper<BookUser>().select("id").lambda().eq(BookUser::getUserId, userId)
                .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue())
        ).stream().map(x -> x.getId()).collect(Collectors.toList());
        List<BookMoney> bookMonies = baseMapper.selectList(new QueryWrapper<BookMoney>().select("id", "money", "type").lambda()
                .in(BookMoney::getUserId, bookUserId)
                .eq(BookMoney::getStatus, DataStatusEnum.ENABLE.getValue())
        );
        // 收入
        long income = bookMonies.stream()
                .filter(x -> BookMoneyTypeEnum.EXPENDITURE.getValue().equals(x.getType())).mapToLong(t -> t.getMoney()).sum();
        // 支出
        long expenditure = bookMonies.stream()
                .filter(x -> BookMoneyTypeEnum.INCOME.getValue().equals(x.getType())).mapToLong(t -> t.getMoney()).sum();
        MeMoneyCount meMoneyCount = new MeMoneyCount();
        meMoneyCount.setIncome(MoneyConvertUtil.fenToYuan(String.valueOf(income)));
        meMoneyCount.setExpenditure(MoneyConvertUtil.fenToYuan(String.valueOf(expenditure)));
        return meMoneyCount;
    }

    @Override
    public Object getDetailDesc(Long moneyId) {
        BookMoney bookMoney = this.baseMapper.selectById(moneyId);
        // 管理员字段为null
        if (null == bookMoney) {
            logger.error("改条记录已经被删除，moneyId：{}", moneyId);
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "改记账已经被删除");
        }

        // 非账本人员不能查询
        Long userId = UserTokenContextHolder.getUserTokenVOByToken().getUserId();
        // 获取到bookId,
        Long bookId = bookMoney.getBookId();
        BookUser bookUser = bookUserService.getOne(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getUserId, userId)
                .eq(BookUser::getBookId, bookId)
                .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue())
        );
        if (null == bookUser) {
            logger.error("非账本人员，权限不足，用户id：{}", userId);
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "权限不足");
        }
        return bookMoney.getDetailDesc();
    }
}
