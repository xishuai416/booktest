package com.wky.book.mapper;

import com.wky.book.entity.BookUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wky.book.vo.BookUserVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 账本用户表 Mapper 接口
 * </p>
 *
 * @author wky
 * @since 2022-04-02
 */
public interface BookUserMapper extends BaseMapper<BookUser> {

    /**
     * 左连接查询账本名称 ，
     *
     * @param bookUserVo
     * @return
     */
    List<BookUser> getAuditAll(@Param("bookUserVo") BookUserVo bookUserVo);
}
