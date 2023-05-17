package com.wky.book.controller;


import com.wky.book.common.context.UserTokenContextHolder;
import com.wky.book.common.exception.BasicInfoException;
import com.wky.book.common.exception.BasicInfoStatusEnum;
import com.wky.book.common.response.BaseResult;
import com.wky.book.common.response.ResultUtils;
import com.wky.book.entity.Book;
import com.wky.book.service.BookService;
import com.wky.book.service.BookUserService;
import com.wky.book.vo.UserTokenVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 账本表 前端控制器
 * </p>
 *
 * @author wky
 * @since 2022-04-02
 */
@RestController
@RequestMapping("/api/book")
public class BookController {

    @Autowired
    BookService bookService;

    /**
     * 删除账本
     *
     * @param bookId
     * @return
     */
    @GetMapping("/delete")
    public BaseResult delete(@RequestParam("bookId") Long bookId) {
        bookService.delete(bookId);
        return ResultUtils.success();
    }

    /**
     * 查询公开账本
     */
    @GetMapping("/search")
    public BaseResult<List<Book>> searchBook(@RequestParam("bookName") String bookName) throws UnsupportedEncodingException {
        String decode = URLDecoder.decode(bookName, "UTF-8");
        return ResultUtils.success(bookService.searchBook(decode));
    }


    /**
     * 查询我的账本
     */
    @GetMapping("/get")
    public BaseResult<List<Book>> get() {
        return ResultUtils.success(bookService.getBook());
    }

    /**
     * 创建账本/更新账本
     *
     * @return
     */
    @PostMapping("/saveAndUpdate")
    @Transactional
    public void save(@Valid @RequestBody Book book) {
        Long id = book.getId();
        // 获取用户id
        Long userId = UserTokenContextHolder.getUserTokenVOByToken().getUserId();
        // 新建
        if (id == null) {
            bookService.add(book);
        } else {
            bookService.update(book, userId);
        }
    }


    /**
     * 用户被邀请加入账本
     *
     * @RequestParam("timeStamp") Long timeStamp
     */
    @GetMapping("/userAddBook")
    public BaseResult userAddBook(@RequestParam("bookId") Long bookId, @RequestParam(value = "remark", defaultValue = "", required = false) String remark, @RequestParam(value = "timeStamp") Long timeStamp) throws UnsupportedEncodingException {
        // 单位/分钟
        Long sminute = (System.currentTimeMillis() - timeStamp) / (1000 * 60);
        // 大于一个月，则为失效
        if (sminute.longValue() >= 1 * 60 * 24 * 30) {
            return ResultUtils.error("邀请已失效");
        }
        String decodeRemark = URLDecoder.decode(remark, "UTF-8");
        return bookService.userAddBook(bookId, decodeRemark);

    }


    /**
     * 用户设置账本为默认账本
     */
    @GetMapping("/defaultBook")
    public BaseResult defaultBook(
            @RequestParam("bookId") Long bookId,
            @RequestParam("defaultBook") Long defaultBook
    ) {
        return bookService.defaultBook(bookId, defaultBook);

    }


    /**
     * 判断当前用户是否是账本成员
     */
    @GetMapping("/isBookMember")
    public BaseResult isBookMember(
            @RequestParam("bookId") Long bookId
    ) {
        return bookService.isBookMember(bookId);

    }


}
