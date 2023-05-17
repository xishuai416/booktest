package com.wky.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wky.book.common.context.UserTokenContextHolder;
import com.wky.book.common.exception.BasicInfoException;
import com.wky.book.common.exception.BasicInfoStatusEnum;
import com.wky.book.entity.BookCategory;
import com.wky.book.entity.Category;
import com.wky.book.entity.CategoryBase;
import com.wky.book.enums.BookMoneyTypeEnum;
import com.wky.book.mapper.CategoryBaseMapper;
import com.wky.book.mapper.CategoryMapper;
import com.wky.book.request.CategoryQueryReqVo;
import com.wky.book.request.CategoryReqVo;
import com.wky.book.response.CategoryQueryResVo;
import com.wky.book.service.BookCategoryService;
import com.wky.book.service.CategoryBaseService;
import com.wky.book.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 分类表 服务实现类
 * </p>
 *
 * @author wky
 * @since 2022-04-02
 */
@Service
public class CategoryBaseServiceImpl extends ServiceImpl<CategoryBaseMapper, CategoryBase> implements CategoryBaseService {


}
