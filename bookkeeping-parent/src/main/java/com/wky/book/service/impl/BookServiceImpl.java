package com.wky.book.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.cglib.CglibUtil;
import cn.hutool.json.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wky.book.common.context.UserTokenContextHolder;
import com.wky.book.common.exception.BasicInfoException;
import com.wky.book.common.exception.BasicInfoStatusEnum;
import com.wky.book.common.response.BaseResult;
import com.wky.book.common.response.ResultUtils;
import com.wky.book.entity.*;
import com.wky.book.enums.*;
import com.wky.book.mapper.BookMapper;
import com.wky.book.mapper.BookUserMapper;
import com.wky.book.response.BookUserReqVo;
import com.wky.book.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wky.sensitive.annotation.SensitiveReplace;
import com.wky.sensitive.enums.DataTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 账本表 服务实现类
 * </p>
 *
 * @author wky
 * @since 2022-04-02
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);
    @Autowired
    BookUserService bookUserService;


    @Autowired
    UserService userService;


    @Autowired
    CategoryService categoryService;


    @Autowired
    BookCategoryService bookCategoryService;

    @Autowired
    CategoryBaseService categoryBaseService;

    @Autowired
    BookMapper bookMapper;

    @Autowired
    BookUserMapper bookUserMapper;

    @Autowired
    BookMoneyService bookMoneyService;

    @Override
    @Transactional
    public void add(Book book) {
        Long userId = UserTokenContextHolder.getUserTokenVOByToken().getUserId();
        if (userId == null) {
            throw new BasicInfoException(BasicInfoStatusEnum.NO_AUTH.getCode(),
                    "请登录");
        }
        User userInfo = userService.getById(userId);
        if (userInfo == null) {
            throw new BasicInfoException(BasicInfoStatusEnum.NO_AUTH.getCode(),
                    "请登录");
        }
        // 设置创建时间+创建人
        book.setCreateTime(new Date());
        book.setCreateBy(userId);
        book.setStatus(DataStatusEnum.ENABLE.getValue());
        this.save(book);
        // 如果是默认账本
        if (book.getDefaultBook().intValue() == 1) {
            List<BookUser> list = bookUserService.list(new QueryWrapper<BookUser>().lambda().eq(BookUser::getUserId, userId));
            // 将该用户下的账本全部置为非默认账本
            list.stream().forEach(x -> x.setDefaultBook(BookAuthEnum.USER.getValue()));
            bookUserService.updateBatchById(list);
        }
        // 添加自身账本用户外键关系
        bookUserService.save(new BookUser()
                .setCreateBy(userId)
                .setCreateTime(new Date())
                .setAuth(BookAuthEnum.ROOT.getValue())
                .setStatus(DataStatusEnum.ENABLE.getValue())
                .setAuditStatus(BookUserAuditStatusEnum.DEFAULT.getValue())
                .setDefaultBook(book.getDefaultBook())
                .setUserId(userId)
                .setBookId(book.getId())
                .setAvatarUrl(StringUtils.isNotBlank(userInfo.getAvatarUrl()) ? userInfo.getAvatarUrl() : "")
                .setUserName(userInfo.getNickName())
                .setReallyName(userInfo.getNickName()));

        // 新增默认分类
        List<CategoryBase> list = categoryBaseService.list(new QueryWrapper<CategoryBase>().lambda().eq(CategoryBase::getStatus, 1L));
        List<Category> collect = list.stream().map(x -> {
            Category category = new Category();
            BeanUtils.copyProperties(x, category, "id");
            category.setBookId(book.getId());
            category.setCreateBy(userId);
            category.setCreateTime(new Date());
            return category;
        }).collect(Collectors.toList());
        categoryService.saveBatch(collect);
    }

    @Override
    public void update(Book book, Long userId) {
        Book dbBook = this.getById(book.getId());
        // 判断是否是账本管理员
        isBookAuthor(book.getId());
        BeanUtils.copyProperties(book, dbBook, "created_time", "created_by", "status");
        book.setCreateBy(dbBook.getCreateBy());
        book.setCreateTime(dbBook.getCreateTime());
        this.updateById(dbBook);
    }

    /**
     * 判断用户是否有该账本权限
     *
     * @param bookId 账本id
     */

    @Override
    public void isBookAuthor(Long bookId) {
        Long userId = UserTokenContextHolder.getUserTokenVOByToken().getUserId();
        BookUser one = bookUserService.getOne(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getBookId, bookId)
                .eq(BookUser::getUserId, userId)
                .ne(BookUser::getAuth, 0));
        if (null == one) {
            logger.error("用户：{}无权操作账本", userId);
            throw new BasicInfoException(BasicInfoStatusEnum.NO_AUTH.getCode(),
                    "您无账本权限");
        }
    }

    @Override
    public List<Book> getBook() {
        Long userId = UserTokenContextHolder.getUserTokenVOByToken().getUserId();
        List<Long> bookIds = bookUserService.list(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue())
                .eq(BookUser::getUserId, userId).orderByDesc(BookUser::getDefaultBook)).stream().map(BookUser::getBookId).collect(Collectors.toList()
        );
        if (bookIds.size() == 0) {
            return null;
        }
        List<Book> bookList = this.list(new QueryWrapper<Book>().lambda()
                .eq(Book::getStatus, DataStatusEnum.ENABLE.getValue())
                .in(Book::getId, bookIds)
                .orderByDesc(Book::getCreateTime));
        // sql排了序，第一条肯定是默认账本
        Long defaultBookId = bookIds.get(0);
        // 设置默认值（因为没有左连接查询（懒得写sql了）bookUser表的 default_book字段）
        bookList.stream().forEach(x -> {
            if (x.getId().intValue() == defaultBookId.intValue()) {
                x.setDefaultBook(BookAuthEnum.ADMIN.getValue());
            } else {
                x.setDefaultBook(BookAuthEnum.USER.getValue());
            }
        });
        return bookList.stream().sorted(Comparator.comparing(Book::getDefaultBook).reversed()).collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void delete(Long bookId) {
        // 判断是否是群主
        Long userId = UserTokenContextHolder.getUserTokenVOByToken().getUserId();
        BookUser one = bookUserService.getOne(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getBookId, bookId)
                .eq(BookUser::getUserId, userId)
                .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue())
                .eq(BookUser::getAuth, BookAuthEnum.ROOT.getValue()));
        if (null == one) {
            logger.error("用户：{}无权操作账本", userId);
            throw new BasicInfoException(BasicInfoStatusEnum.NO_AUTH.getCode(),
                    "您无账本权限");
        }
        Book book = this.getById(bookId);
        // 逻辑删除
        book.setStatus(DataStatusEnum.DELETE.getValue());
        this.updateById(book);

        BookUser bu = new BookUser();
        bu.setStatus(DataStatusEnum.DELETE.getValue());
        // 删除用户
        bookUserService.update(bu, new QueryWrapper<BookUser>().lambda().eq(BookUser::getBookId, bookId));
        // 删除金额
        BookMoney bookMoney = new BookMoney();
        bookMoney.setStatus(DataStatusEnum.DELETE.getValue());
        bookMoneyService.update(bookMoney, new QueryWrapper<BookMoney>().lambda().eq(BookMoney::getBookId, bookId));
    }


    /**
     * 用户加入账本
     *
     * @param bookId
     */
    @Override
    public BaseResult userAddBook(Long bookId, String remark) {
        Book book = bookMapper.selectById(bookId);
        if (null == book) {
            logger.error("账本不存在，账本id：{}", bookId);
            throw new BasicInfoException(BasicInfoStatusEnum.NO_AUTH.getCode(),
                    "账本不存在");
        }
        // 操作人id,就是用户id
        Long userId = null;
        try {
            userId = UserTokenContextHolder.getUserTokenVOByToken().getUserId();
        } catch (Exception e) {
            logger.error("加入账本失败，请先登录,账本id:{},描述：{}", bookId,remark);
            return ResultUtils.success("请先登录");
        }
        if(null==userId){
            logger.error("token没有缓存该用户：",userId);
        }
        // 默认真是姓名是微信名称
        User user = userService.getOne(new QueryWrapper<User>().lambda()
                .eq(User::getId,userId));
        if (null == user) {
            logger.error("加入账本失败，用户：{} 不存在",userId);
            throw new BasicInfoException(BasicInfoStatusEnum.NO_AUTH.getCode(),
                    "用户不存在");
        }
        BookUser ifBookUser = bookUserService.getOne(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue())
                .eq(BookUser::getBookId, bookId)
                .eq(BookUser::getUserId, userId));
        if (ifBookUser != null) {
            logger.warn("请勿重复加入,用户id：{}",userId);
            return ResultUtils.success("请勿重复加入！");
        }
        // 为什么不能加 .eq(BookUser::getStatus,DataStatusEnum.ENABLE.getValue())条件，因为待审核的用户也需要调用该接口
        BookUser bookUser = bookUserService.getOne(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getBookId, bookId)
                .eq(BookUser::getUserId, userId));
        // 用户存在默认账本 则为非默认账本，否则为默认账本
        List<BookUser> list = bookUserService.list(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getDefaultBook, 1L)
                .eq(BookUser::getUserId, userId));
        if (bookUser == null) {
            bookUserService.save(new BookUser()
                    .setBookId(bookId)
                    .setUserId(userId)
                    .setRemark(remark)
                    .setAuditStatus(BookUserAuditStatusEnum.DEFAULT.getValue())
                    .setStatus(BookAuditEnum.ADMIN.getValue().equals(book.getUserAudit()) ? 2 : 1)
                    .setAuth(BookAuthEnum.USER.getValue())
                    .setDefaultBook(list.size() > BookAuthEnum.USER.getValue() ? BookAuthEnum.USER.getValue() : BookAuthEnum.ADMIN.getValue())
                    .setUserName(user.getNickName())
                    .setReallyName(user.getNickName())
                    .setAvatarUrl(user.getAvatarUrl())
                    .setCreateBy(userId)
                    .setCreateTime(new Date()));
            //  需要审核
            if (BookAuditEnum.ADMIN.getValue().equals(book.getUserAudit())) {

                logger.warn("用户申请加入账本,用户id：{}",userId);
                return ResultUtils.success(202, "申请提交成功");
            }
            return ResultUtils.success();
        }

        bookUser.setAuditStatus(BookUserAuditStatusEnum.DEFAULT.getValue());
        bookUser.setStatus(BookAuditEnum.ADMIN.getValue().equals(book.getUserAudit()) ? 2 : 1);
        bookUser.setDefaultBook(list.size() > BookAuthEnum.USER.getValue() ? BookAuthEnum.USER.getValue() : BookAuthEnum.ADMIN.getValue());
        bookUser.setUpdateBy(userId);
        bookUser.setRemark(remark);
        bookUserService.updateById(bookUser);
        //  需要审核
        if (BookAuditEnum.ADMIN.getValue().equals(book.getUserAudit())) {
            logger.warn("用户申请加入账本,用户id：{}",userId);
            return ResultUtils.success(202, "申请提交成功");
        }
        return ResultUtils.success();
    }

    /**
     * 否是默认账本(0-否，1是）
     *
     * @param bookId
     * @param defaultBook
     * @return
     */
    @Override
    @Transactional
    public BaseResult defaultBook(Long bookId, Long defaultBook) {
        Long userId = UserTokenContextHolder.getUserTokenVOByToken().getUserId();
        // 设置默认账本
        if (defaultBook.equals(1l)) {
            BookUser bookUser = bookUserService.getOne(new QueryWrapper<BookUser>().lambda()
                    .eq(BookUser::getStatus, 1)
                    .eq(BookUser::getDefaultBook, defaultBook)
                    .eq(BookUser::getUserId, userId)
                    .ne(BookUser::getBookId, bookId));
            if (null != bookUser) {
                // 把默认账本设置为非默认账本
                bookUser.setUpdateBy(userId);
                bookUser.setDefaultBook(BookAuthEnum.USER.getValue());
                bookUserService.updateById(bookUser);
            }

            // 查询要设置默认账本的账本
            BookUser settingBook = findByIdBook(userId, bookId);
            settingBook.setUpdateBy(userId);
            settingBook.setDefaultBook(BookAuthEnum.ADMIN.getValue());
            bookUserService.updateById(settingBook);
        } else {
            // 取消默认账本
            // 查询是否存在默认账本
            List<BookUser> bookUserList = bookUserService.list(new QueryWrapper<BookUser>().lambda()
                    .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue())
                    .eq(BookUser::getDefaultBook, BookAuthEnum.ADMIN.getValue())
                    .eq(BookUser::getUserId, userId)
                    .ne(BookUser::getBookId, bookId)

            );
            if (bookUserList.size() == 0) {
                logger.warn("保留一个默认账本,用户id：{}，账本id：{}",userId,bookId);
                throw new BasicInfoException(BasicInfoStatusEnum.NO_AUTH.getCode(),
                        "保留一个默认账本");
            }
//            bookUserList.stream().forEach(bookUser -> bookUser.setDefaultBook(0l));
            // 查询要设置默认账本的账本
            BookUser settingBook = findByIdBook(userId, bookId);
            settingBook.setUpdateBy(userId);
            settingBook.setDefaultBook(BookAuthEnum.USER.getValue());
            bookUserService.updateById(settingBook);
        }
        return ResultUtils.success();
    }

    private BookUser findByIdBook(Long userId, Long bookId) {
        BookUser one = bookUserService.getOne(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue())
                .eq(BookUser::getUserId, userId)
                .eq(BookUser::getBookId, bookId));
        if (one == null) {
            logger.error("查询失败，账本不存在,账本id：{}", bookId);
            throw new BasicInfoException(BasicInfoStatusEnum.NO_AUTH.getCode(),
                    "账本不存在");
        }
        return one;
    }


    @Override
    public List<Book> searchBook(String bookName) {
        // TODO: 2022/5/7 0007 这里考虑是否整合elasticsearch。或者还是like搜索
        return this.list(new QueryWrapper<Book>().lambda()
                .eq(Book::getStatus, DataStatusEnum.ENABLE.getValue())
                .eq(Book::getShowSearch, BookSearchEnum.YES.getValue())
                .like(Book::getBookName, bookName)
                .orderByDesc(Book::getCreateTime));
    }


    @Override
    public BaseResult isBookMember(Long bookId) {
        Long aLong = bookUserMapper.selectCount((new QueryWrapper<BookUser>()).lambda()
                .eq(BookUser::getUserId, UserTokenContextHolder.getUserTokenVOByToken().getUserId())
                .eq(BookUser::getBookId, bookId)
                .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue()));
        return ResultUtils.success(aLong > 0);
    }
}
