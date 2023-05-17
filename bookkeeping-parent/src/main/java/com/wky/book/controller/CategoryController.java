package com.wky.book.controller;


import com.wky.book.common.response.BaseResult;
import com.wky.book.common.response.ResultUtils;
import com.wky.book.request.CategoryQueryReqVo;
import com.wky.book.response.CategoryQueryResVo;
import com.wky.book.service.BookCategoryService;
import com.wky.book.service.CategoryService;
import com.wky.book.request.CategoryReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>
 * 分类表 前端控制器
 * </p>
 *
 * @author wky
 * @since 2022-04-02
 */
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    BookCategoryService bookCategoryService;

    /**
     * 新建账本分类
     *
     * @return
     */
    @PostMapping("/save")
    public BaseResult save(@Valid @RequestBody CategoryReqVo categoryReqVo, HttpServletRequest request) {
        categoryService.save(categoryReqVo,"/bookCategory",categoryReqVo.getBookId(),request);
        return ResultUtils.success();
    }


    /**
     * 新建账本分类
     *
     * @return
     */
    @GetMapping("/remove")
    public BaseResult remove(@RequestParam("categoryId") Long categoryId) {
        categoryService.remove(categoryId);
        return ResultUtils.success();
    }

    /**
     * 查询分类
     *
     * @return
     */
    @PostMapping("/findList")
    public void findList(@Valid @RequestBody CategoryQueryReqVo categoryQueryReqVo) {
        categoryService.findList(categoryQueryReqVo);
    }


    /**
     * 根据bookId查询分类
     *
     * @return
     */
    @GetMapping("/findBookIdList")
    public CategoryQueryResVo findBookIdList(@RequestParam("bookId") Long bookId) {
        return categoryService.findBookIdList(bookId);
    }
}
