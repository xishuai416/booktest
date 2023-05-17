package com.wky.book.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wky.book.common.response.BaseResult;
import com.wky.book.common.response.ResultUtils;
import com.wky.book.entity.Message;
import com.wky.book.enums.DataStatusEnum;
import com.wky.book.mapper.MessageMapper;
import com.wky.book.request.BookMoneyQueryReqVo;
import com.wky.book.response.BookMoneyCountRes;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 *
 */

@RestController
@RequestMapping("/api/message")
public class MessageController {


    @Autowired
    MessageMapper messageMapper;
    /**
     * 查询统计/user/send/apply
     */
    // TODO: 2022/4/5 0005  是否分页，待考虑
    @GetMapping("/get")
    public BaseResult<List<Message>> findList(@RequestParam(value = "type",required = false,defaultValue = "")String type) {
        List<Message> messages = messageMapper.selectList(new QueryWrapper<Message>().lambda()
                .eq(StringUtils.isNotBlank(type), Message::getType, type)
                .eq(Message::getStatus, DataStatusEnum.ENABLE.getValue())
        );
        return ResultUtils.success(messages);

    }
}
