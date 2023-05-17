package com.wky.book.service.impl;

import com.wky.book.entity.BookCategory;
import com.wky.book.mapper.BookCategoryMapper;
import com.wky.book.service.BookCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 账本分类表 服务实现类
 * </p>
 *
 * @author wky
 * @since 2022-04-02
 */
@Service
public class BookCategoryServiceImpl extends ServiceImpl<BookCategoryMapper, BookCategory> implements BookCategoryService {

}
