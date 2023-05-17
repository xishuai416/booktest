package com.wky.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wky.book.entity.BookUser;
import com.wky.book.enums.BookUserAuditStatusEnum;
import com.wky.book.enums.DataStatusEnum;
import com.wky.book.service.BookUserService;
import com.wky.sensitive.annotation.SensitiveReplace;
import com.wky.sensitive.enums.DataTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReplaceDataApi {

    @Autowired
    BookUserService bookUserService;

    @SensitiveReplace(dataType= DataTypeEnum.LIST)
    public List<BookUser> replaceList(Long bookId) {
        return   bookUserService.list(new QueryWrapper<BookUser>().lambda()
                .eq(BookUser::getStatus, DataStatusEnum.ENABLE.getValue())
                .eq(BookUser::getBookId, bookId)
                .ne(BookUser::getAuditStatus, BookUserAuditStatusEnum.REJECT.getValue())
                .orderByDesc(BookUser::getAuth)
        );
    }

}
