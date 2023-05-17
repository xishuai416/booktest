package com.wky.book.controller;


import com.wky.book.common.context.UserTokenContextHolder;
import com.wky.book.entity.Book;
import com.wky.book.entity.BookCategory;
import com.wky.book.service.BookCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;

/**
 * <p>
 * 账本分类表 前端控制器
 * </p>
 *
 * @author wky
 * @since 2022-04-02
 */
@RestController
@RequestMapping("/api/bookCategory")
public class BookCategoryController {


    @Autowired
    BookCategoryService bookCategoryService;

}
