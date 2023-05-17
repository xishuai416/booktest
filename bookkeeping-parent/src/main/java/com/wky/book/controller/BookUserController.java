package com.wky.book.controller;


import com.wky.book.common.response.BaseResult;
import com.wky.book.common.response.ResultUtils;
import com.wky.book.entity.BookUser;
import com.wky.book.request.UserApplyBookReqVo;
import com.wky.book.response.BookUserReqVo;
import com.wky.book.service.BookUserService;
import com.wky.book.vo.BookUserEditVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

/**
 * <p>
 * 账本用户表 前端控制器
 * </p>
 *
 * @author wky
 * @since 2022-04-02
 */
@RestController
@RequestMapping("/api/book/user")
public class BookUserController {

    @Autowired
    BookUserService bookUserService;

    /**
     * 查询账本下的用户列表
     */
    @GetMapping("/get")
    public Object getBookUser(@RequestParam("bookId") Long bookId) {
        return ResultUtils.success(bookUserService.getBookUser(bookId));
    }

    /**
     * 用户从搜索中申请加入账本请求
     */
    @PostMapping("/send/apply")
    public Object sendApply(@RequestBody UserApplyBookReqVo userApplyBookReqVo) {
       return  bookUserService.sendApply(userApplyBookReqVo);

    }

    /**
     * 同意用户加入账本
     */
    @PostMapping("/agree")
    public Object agree(@RequestBody UserApplyBookReqVo userApplyBookReqVo) {
        bookUserService.agree(userApplyBookReqVo);
        return ResultUtils.success();
    }

    /**
     * 拒绝用户加入账本
     */
    @PostMapping("/reject")
    public Object reject(@RequestBody UserApplyBookReqVo userApplyBookReqVo) {
        bookUserService.reject(userApplyBookReqVo);
        return ResultUtils.success();
    }


    /**
     * 获取待审核的数量
     */
    @GetMapping("/auditCount")
    public Object getAuditCount() {
        return ResultUtils.success(bookUserService.getAuditCount());
    }


    /**
     * 查询所有申请加入的用户
     */
    @GetMapping("/auditAll")
    public Object getAuditAll() {
        return ResultUtils.success(bookUserService.getAuditAll());
    }

    /**
     * 查询我的申请
     */
    @GetMapping("/my/apply")
    public Object myApply() {
        return ResultUtils.success(bookUserService.myApply());
    }

    /**
     * 查询账本申请加入的用户
     */
    @GetMapping("/audit")
    public Object bookAudit(@RequestParam("bookId") Long bookId) {
        return ResultUtils.success(bookUserService.bookAudit(bookId));
    }



    /**
     * 删除成员
     */
    @GetMapping("/edit")
    public BaseResult editBookUser(@RequestBody BookUserEditVo bookUserEditVo) {
        return ResultUtils.success(bookUserService.editBookUser(bookUserEditVo));
    }


    /**
     * 删除成员，
     */
    @GetMapping("/remove")
    public Object remove(@RequestParam("bookId") Long bookId,
                         @RequestParam("userId") Long userId) {
        return ResultUtils.success(bookUserService.remove(bookId, userId));
    }


    /**
     * 添加成员为管理员
     */
    @Deprecated
    @GetMapping("/addAdmin")
    public BaseResult addAuth(
            @RequestParam("bookId") Long bookId,
            @RequestParam("userId") Long userId
    ) {
        return bookUserService.addAuth(bookId, userId);
    }

    /**
     * 移除管理员
     */
    @Deprecated
    @GetMapping("/removeAdmin")
    public BaseResult removeAuth(
            @RequestParam("bookId") Long bookId,
            @RequestParam("userId") Long userId
    ) {
        return bookUserService.removeAuth(bookId, userId);
    }


    /**
     * 修改用户，目前只支持修改用户名称跟头像
     */
    @PostMapping("/update")
    public BaseResult update(@RequestBody BookUserEditVo bookUserEditVo) {
        return bookUserService.update(bookUserEditVo);
    }


    /**
     * 用户自己退出账本
     */
    @GetMapping("/signOutBook")
    public BaseResult signOutBook(@RequestParam("bookId") Long bookId) {
        return ResultUtils.success(bookUserService.signOutBook(bookId));
    }




}
