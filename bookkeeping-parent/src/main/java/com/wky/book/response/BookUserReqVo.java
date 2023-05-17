package com.wky.book.response;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wky.book.entity.BookUser;
import com.wky.book.entity.User;
import com.wky.sensitive.annotation.ChildReplace;
import com.wky.sensitive.annotation.Replace;
import com.wky.sensitive.enums.DataTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 查询账本下的用户列表响应实体
 */
@Data
@Accessors(chain = true)
public class BookUserReqVo {

    @ApiModelProperty("账本id")
    private String id;

    @ApiModelProperty("账本名称")
    private String bookName;

    @ApiModelProperty("账本图片")
    private String bookAvatar;


    @ApiModelProperty("账本成员信息")
    private List<BookUser> bookUserList;



}
