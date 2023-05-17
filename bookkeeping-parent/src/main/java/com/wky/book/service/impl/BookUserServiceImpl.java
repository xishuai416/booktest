package com.wky.book.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wky.book.BookkeepingParentApplication;
import com.wky.book.common.context.ApplicationContextUtil;
import com.wky.book.common.context.UserTokenContextHolder;
import com.wky.book.common.exception.BasicInfoException;
import com.wky.book.common.exception.BasicInfoStatusEnum;
import com.wky.book.common.response.BaseResult;
import com.wky.book.common.response.ResultUtils;
import com.wky.book.entity.Book;
import com.wky.book.entity.BookUser;
import com.wky.book.enums.BookAuthEnum;
import com.wky.book.enums.BookUserAuditEnum;
import com.wky.book.enums.BookUserAuditStatusEnum;
import com.wky.book.enums.DataStatusEnum;
import com.wky.book.mapper.BookMapper;
import com.wky.book.mapper.BookUserMapper;
import com.wky.book.request.UserApplyBookReqVo;
import com.wky.book.response.BookUserReqVo;
import com.wky.book.service.BookService;
import com.wky.book.service.BookUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wky.book.service.UserService;
import com.wky.book.vo.BookUserEditVo;
import com.wky.book.vo.BookUserVo;
import com.wky.sensitive.annotation.SensitiveReplace;
import com.wky.sensitive.enums.DataTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 账本用户表 服务实现类
 * </p>
 *
 * @author wky
 * @since 2022-04-02
 */
@Service
public class BookUserServiceImpl extends ServiceImpl<BookUserMapper, BookUser> implements BookUserService {

    private static final Logger logger = LoggerFactory.getLogger(BookUserServiceImpl.class);
    @Autowired
    BookService bookService;

    @Autowired
    UserService userService;

    @Autowired
    BookUserMapper bookUserMapper;

    @Autowired
    ReplaceDataApi replaceDataApi;

    @Autowired
    BookMapper bookMapper;

    @Override
    public BookUserReqVo getBookUser(Long bookId) {
        Book book = bookService.getById(bookId);
        if (null == book) {
            logger.error("查询账本：{}不存在", bookId);
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "账本不存在");
        }
        BookUserReqVo bookUserReqVo = new BookUserReqVo();
        BeanUtil.copyProperties(book, bookUserReqVo);


        // 当前请求账本数据人是否是账本成员
        BookUser bookUser = baseMapper.selectOne((new QueryWrapper<BookUser>()).lambda()
                .eq(BookUser::getUserId, UserTokenContextHolder.getUserTokenVOByToken().getUserId())
                .eq(BookUser::getBookId, bookId)
                .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue())
        );
        List<BookUser> bookUsers;
        if (bookUser == null) {
            bookUsers = replaceDataApi.replaceList(bookId);
        } else {
            bookUsers = this.list(new QueryWrapper<BookUser>().lambda()
                    .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue())
                    .eq(BookUser::getBookId, bookId)
                    .ne(BookUser::getAuditStatus, BookUserAuditStatusEnum.REJECT.getValue())
                    .orderByDesc(BookUser::getAuth)
            );
        }
        bookUserReqVo.setBookUserList(bookUsers);
        return bookUserReqVo;
    }


    @Override
    @SensitiveReplace(dataType = DataTypeEnum.LIST)
    public List<BookUser> replaceList(Long bookId) {
        return this.list(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue())
                .eq(BookUser::getBookId, bookId)
                .ne(BookUser::getAuditStatus, BookUserAuditStatusEnum.REJECT.getValue())
                .orderByDesc(BookUser::getAuth)
        );
    }

    @Override
    public Object bookAudit(Long bookId) {
        Book bookServiceById = bookService.getById(bookId);
        if (null == bookServiceById) {
            logger.error("审核账本：{}不存在", bookId);
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(), "账本不存在");
        }
        BookUserReqVo bo = new BookUserReqVo();
        BeanUtil.copyProperties(bookServiceById, bo);
        List<BookUser> bookUsers = this.list(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getStatus, DataStatusEnum.DISABLE.getValue())
                .eq(BookUser::getAuditStatus, BookUserAuditStatusEnum.REJECT.getValue())
                .orderByDesc(BookUser::getAuth)
                .eq(BookUser::getBookId, bookId)
        );
        bo.setBookUserList(bookUsers);
        return bo;
    }

    @Override
    public Object editBookUser(BookUserEditVo bookUserEditVo) {
        BookUser editBookUser = isEditBookUser(bookUserEditVo);
        BeanUtils.copyProperties(bookUserEditVo, editBookUser);
        editBookUser.setUpdateBy(UserTokenContextHolder.getUserTokenVOByToken().getUserId());
        return ResultUtils.success();
    }

    @Override
    public BaseResult update(BookUserEditVo bookUserEditVo) {
        if (StringUtils.isNotBlank(bookUserEditVo.getUserName()) && bookUserEditVo.getUserName().length() > 50) {
            logger.error("用户名太长：{}", bookUserEditVo.getUserName());
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "用户名太长");
        }
        if (StringUtils.isNotBlank(bookUserEditVo.getReallyName()) && bookUserEditVo.getReallyName().length() > 50) {
            logger.error("用户别名太长：{}", bookUserEditVo.getReallyName());
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "用户别名太长");
        }

        // TODO: 2022/4/30 0030  book_user 需要建立 book_id与 userName联合唯一索引
        // 查询账本下是否存在当前用户
        BookUser repeatBookUser = baseMapper.selectOne(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getBookId, bookUserEditVo.getBookId())
                .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue())
                .eq(BookUser::getUserName, bookUserEditVo.getUserName())
                .ne(BookUser::getUserId, bookUserEditVo.getUserId())
        );
        if (null != repeatBookUser) {
            logger.error("账本下存在：{}相同名称", bookUserEditVo.getUserName());
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "该名称已存在");
        }
        BookUser editBookUser = isEditBookUser(bookUserEditVo);
        // 只能修改用户名称跟头像
        editBookUser.setUserName(StringUtils.isEmpty(bookUserEditVo.getUserName()) ? null : bookUserEditVo.getUserName());
        // 只能修改用户名称跟头像
        editBookUser.setAvatarUrl(StringUtils.isEmpty(bookUserEditVo.getAvatarUrl()) ? null : bookUserEditVo.getAvatarUrl());

        // 修改人
        editBookUser.setUpdateBy(UserTokenContextHolder.getUserTokenVOByToken().getUserId());
        baseMapper.updateById(editBookUser);
        return ResultUtils.success();
    }

    /**
     * 1、用户权限只能改自己
     * 2、管理员只能改用户与自己
     * 3、群主改任何
     *
     * @param bookUserEditVo
     */
    private BookUser isEditBookUser(BookUserEditVo bookUserEditVo) {
        //
        Long nowId = UserTokenContextHolder.getUserTokenVOByToken().getUserId();
        // 查询当前操作人
        BookUser nowBookUser = baseMapper.selectOne(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getUserId, nowId)
                .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue())
                .eq(BookUser::getBookId, bookUserEditVo.getBookId()));
        // 获取被修改人
        BookUser updateBookUser = baseMapper.selectOne(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getUserId, bookUserEditVo.getUserId())
                .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue())
                .eq(BookUser::getBookId, bookUserEditVo.getBookId()));
        if (nowBookUser == null) {
            logger.error("账本修改失败：操作人不存在：操作人id：{}", nowId);
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "操作人不存在");
        }
        // 操作人是群主权限
        if (nowBookUser.getAuth().longValue() == BookAuthEnum.ROOT.getValue().longValue()) {
            return updateBookUser;
        }
        // 如果是自己可修改
        if (updateBookUser.getUserId().longValue() == nowId.longValue()) {
            return updateBookUser;
        }
        // 操作人是管理员权限
        if (nowBookUser.getAuth().longValue() == BookAuthEnum.ADMIN.getValue().longValue()) {
            // 被修改人不是用户
            if (updateBookUser.getAuth().longValue() != BookAuthEnum.USER.getValue().longValue()) {
                logger.error("账管理员只能修改自己与用户，管理员id：{}", nowId);
                throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                        "只能修改用户");
            }
        }
        // 操作人是用户权限
        if (nowBookUser.getAuth().longValue() == BookAuthEnum.USER.getValue().longValue()) {
            // 被修改人不是自己
            if (updateBookUser.getUserId().longValue() != nowId.longValue()) {
                logger.error("用户没有权限修改他人信息，用户id：{}", nowId);
                throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                        "用户没有权限");
            }
        }
        return updateBookUser;
    }

    @Override
    public Object remove(Long bookId, Long userId) {
        BookUser bookUser = isDeleteBookUser(bookId, userId);
        bookUser.setStatus(DataStatusEnum.DELETE.getValue());
        bookUser.setUpdateBy(UserTokenContextHolder.getUserTokenVOByToken().getUserId());
        baseMapper.updateById(bookUser);
        return ResultUtils.success();
    }

    private BookUser isDeleteBookUser(Long bookId, Long userId) {
        Long nowId = UserTokenContextHolder.getUserTokenVOByToken().getUserId();
        BookUser bookUser = baseMapper.selectOne(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getUserId, userId)
                .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue())
                .eq(BookUser::getBookId, bookId));
        if (null == bookUser) {
            logger.error("账本无该用户，用户id：{}", nowId);
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "账本无该用户");
        }
        // 查询当前操作人
        BookUser authBook = baseMapper.selectOne(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getUserId, nowId)
                .eq(BookUser::getStatus, DataStatusEnum
                        .ENABLE.getValue())
                .eq(BookUser::getBookId, bookId));
        // 仅群主管理员可以操作
        if (BookAuthEnum.USER.getValue().longValue() == authBook.getAuth().longValue()) {
            logger.error("用户无权删除，用户id：{}", nowId);
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "无权限操作");
        }
        if (nowId.longValue() == userId.longValue()) {
            logger.error("用户不能移除自己，用户id：{}", nowId);
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "不能移除自己");
        }
        // 如果是群主
        if (null != authBook && bookUser.getAuth().longValue() == BookAuthEnum.ROOT.getValue().longValue()) {
            logger.error("不能删除群主，用户id：{}", nowId);
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "不能删除群主");
        }
        // 管理员想删除群主
        if (BookAuthEnum.ADMIN.getValue().intValue() == authBook.getAuth().longValue()) {
            if (bookUser.getAuth().longValue() == BookAuthEnum.ROOT.getValue().longValue()) {
                logger.error("管理员不能删除群主，管理员id：{}", nowId);
                throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                        "想造反？");
            }
            if (bookUser.getAuth().longValue() == BookAuthEnum.ADMIN.getValue().longValue()) {
                logger.error("不可删除管理，管理员id：{}", nowId);
                throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                        "不可删除管理");
            }
        }
        return bookUser;
    }


    @Override
    public BaseResult addAuth(Long bookId, Long userId) {
        BookUser bookUser = rootBookUser(bookId, userId);
        bookUser.setAuth(BookAuthEnum.ADMIN.getValue());
        bookUser.setUpdateBy(UserTokenContextHolder.getUserTokenVOByToken().getUserId());
        baseMapper.updateById(bookUser);
        return ResultUtils.success();
    }

    private BookUser rootBookUser(Long bookId, Long userId) {
        // 只有群主有权限
        Long nowId = UserTokenContextHolder.getUserTokenVOByToken().getUserId();
        // 查询当前操作人
        BookUser nowBookUser = baseMapper.selectOne(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue())
                .eq(BookUser::getUserId, nowId)
                .eq(BookUser::getBookId, bookId));
        // 获取被修改人
        BookUser updateBookUser = baseMapper.selectOne(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getUserId, userId)
                .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue())
                .eq(BookUser::getBookId, bookId));
        if (nowBookUser == null) {
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "操作人不存在");
        }
        // 操作人是群主权限,且被修改人不是群主
        if (nowBookUser.getAuth().longValue() == BookAuthEnum.ROOT.getValue().longValue()
                && updateBookUser.getAuth().longValue() != BookAuthEnum.ROOT.getValue().longValue()
        ) {
            // 返回被修改人
            return updateBookUser;
        }
        throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                "仅群主设置");
    }

    @Override
    public BaseResult removeAuth(Long bookId, Long userId) {
        BookUser bookUser = rootBookUser(bookId, userId);
        bookUser.setAuth(BookAuthEnum.USER.getValue());
        bookUser.setUpdateBy(UserTokenContextHolder.getUserTokenVOByToken().getUserId());
        baseMapper.updateById(bookUser);
        return ResultUtils.success();
    }


    @Override
    public Object signOutBook(Long bookId) {
        BaseResult bookMember = bookService.isBookMember(bookId);
        Boolean isBookMember = (Boolean) bookMember.getData();
        if (!isBookMember) {
            logger.error("请勿重复退出账本，用户id：{}", UserTokenContextHolder.getUserTokenVOByToken().getUserId());
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "请勿重复退出");
        }
        Long userId = UserTokenContextHolder.getUserTokenVOByToken().getUserId();
        BookUser update = baseMapper.selectOne(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getUserId, userId)
                .eq(BookUser::getBookId, bookId));
        update.setStatus(DataStatusEnum.DELETE.getValue());
        baseMapper.updateById(update);
        return ResultUtils.success();
    }


    @Override
    public void reject(UserApplyBookReqVo userApplyBookReqVo) {
        isRejectAndAgree(userApplyBookReqVo.getBookId());
        // 查询待审核状态
        BookUser nowBookUser = baseMapper.selectOne(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getStatus, DataStatusEnum.DISABLE.getValue())
                .eq(BookUser::getUserId, userApplyBookReqVo.getUserId())
                .eq(BookUser::getBookId, userApplyBookReqVo.getBookId()));
        nowBookUser.setStatus(DataStatusEnum.DISABLE.getValue());
        nowBookUser.setUpdateBy(UserTokenContextHolder.getUserTokenVOByToken().getUserId());
        nowBookUser.setRemark(userApplyBookReqVo.getRemark());
        nowBookUser.setAuditStatus(BookUserAuditStatusEnum.REJECT.getValue());
        this.baseMapper.updateById(nowBookUser);
    }


    @Override
    public void agree(UserApplyBookReqVo userApplyBookReqVo) {
        isRejectAndAgree(userApplyBookReqVo.getBookId());
        // 查询待审核状态
        BookUser nowBookUser = baseMapper.selectOne(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getBookId, userApplyBookReqVo.getBookId())
                .eq(BookUser::getStatus, DataStatusEnum.DISABLE.getValue())
                .eq(BookUser::getUserId, userApplyBookReqVo.getUserId()));
        nowBookUser.setStatus(DataStatusEnum.ENABLE.getValue());
        nowBookUser.setAuditStatus(BookUserAuditStatusEnum.ARGEE.getValue());
        nowBookUser.setRemark(userApplyBookReqVo.getRemark());
        nowBookUser.setUpdateBy(UserTokenContextHolder.getUserTokenVOByToken().getUserId());
        this.baseMapper.updateById(nowBookUser);
    }

    private void isRejectAndAgree(Long bookId) {
        //
        // 查询当前操作人
        BookUser nowBookUser = baseMapper.selectOne(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getUserId, UserTokenContextHolder.getUserTokenVOByToken().getUserId())
                .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue())
                .eq(BookUser::getBookId, bookId));
        if (null == nowBookUser) {
            logger.error("拒绝用户加入失败，操作人不存在，操作人id：{}", UserTokenContextHolder.getUserTokenVOByToken().getUserId());
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "操作人不存在");
        }
        if (Objects.equals(BookAuthEnum.USER.getValue(), nowBookUser.getAuth())) {
            logger.error("成员没有权限拒绝用户加入失败，操作成员人id：{}", UserTokenContextHolder.getUserTokenVOByToken().getUserId());
            throw new BasicInfoException(BasicInfoStatusEnum.SYSTEM_ERROR.getCode(),
                    "成员没有权限");
        }
    }

    @Override
    public Integer getAuditCount() {
        // 查询当前用户的管理的账本
        List<BookUser> bookUsers = baseMapper.selectList(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue())
                .eq(BookUser::getUserId, UserTokenContextHolder.getUserTokenVOByToken().getUserId())
                .ne(BookUser::getAuth, BookAuthEnum.USER.getValue())
        );
        List<Long> bookIds = bookUsers.stream().map(x -> x.getBookId()).collect(Collectors.toList());
        if (bookIds.size() == 0) return bookIds.size();
        return baseMapper.selectCount(new QueryWrapper<BookUser>().lambda()
                .in(BookUser::getBookId, bookIds)
                .eq(BookUser::getAuditStatus, BookUserAuditEnum.NO.getValue())
                .eq(BookUser::getStatus, DataStatusEnum.DISABLE.getValue())).intValue();

    }


    @Override
    public Object getAuditAll() {
        // 查询当前用户的管理的账本
        List<Long> bookIds = baseMapper.selectList(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue())
                .eq(BookUser::getUserId, UserTokenContextHolder.getUserTokenVOByToken().getUserId())
                .ne(BookUser::getAuth, BookAuthEnum.USER.getValue())
        ).stream().map(x -> x.getBookId()).collect(Collectors.toList());

        // 查询正常的账本


        // 查询账本
        List<BookUserReqVo> bookUserReqVoList = new LinkedList<>();
        if (bookIds.size() == 0)
            return new ArrayList<>();

        // 根据账本id分组
        List<BookUser> bookUsers = baseMapper.getAuditAll(new BookUserVo(bookIds, DataStatusEnum.DISABLE.getValue())
        );
        return bookUsers;
    }


    @Override
    public Object myApply() {
        // status状态为2
        List<Long> bookIds = baseMapper.selectList(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getUserId, UserTokenContextHolder.getUserTokenVOByToken().getUserId())
        ).stream().map(x -> x.getBookId()).collect(Collectors.toList());
        if (bookIds.size() == 0)
            return new ArrayList<>();
        // 根据账本id分组
        List<BookUser> bookUsers = baseMapper.getAuditAll(
                new BookUserVo(bookIds, DataStatusEnum.DISABLE.getValue(), UserTokenContextHolder.getUserTokenVOByToken().getUserId())
        );
        return bookUsers;
    }

    @Override
    public BaseResult sendApply(UserApplyBookReqVo userApplyBookReqVo) {

        // 当前请求账本数据人是否是账本成员
        BookUser bookUser = baseMapper.selectOne((new QueryWrapper<BookUser>()).lambda()
                .eq(BookUser::getUserId, UserTokenContextHolder.getUserTokenVOByToken().getUserId())
                .eq(BookUser::getBookId, userApplyBookReqVo.getBookId())
                .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue())
        );
        if (null != bookUser) {
            logger.error("用户从搜索中申请加入账本请求你已是该成员，用户已经是账本成员，用户id：{}", UserTokenContextHolder.getUserTokenVOByToken().getUserId());
            throw new BasicInfoException(BasicInfoStatusEnum.UNSUPPORTED.getCode(), "你已是该成员");
        }
        return bookService.userAddBook(userApplyBookReqVo.getBookId(), userApplyBookReqVo.getRemark());
    }

}

