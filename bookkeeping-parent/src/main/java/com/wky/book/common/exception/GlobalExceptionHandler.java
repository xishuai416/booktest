package com.wky.book.common.exception;

import com.wky.book.common.response.BaseResult;
import com.wky.book.common.response.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;

import javax.jws.WebResult;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author weikaiyu
 * @version 1.0
 * @date 2022-04-02 17:00
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 转发到/error，表示由BasicErrorController处理，
     * BasicErrorController是由springboot自动装配到容器中的
     */
  /*@ExceptionHandler(BasicInfoException.class)
  public String handleException(Exception ex, HttpServletRequest request){
    request.setAttribute("javax.servlet.error.status_code", 401);
    request.setAttribute("exMsg", ex.getMessage());
    return "forward:/error";
  }*/


    /**
     * 处理基本信息相关的异常
     */
    @ExceptionHandler(BasicInfoException.class)
    @ResponseBody
    public BaseResult handleBasicInfoException(BasicInfoException ex){
        return ResultUtils.error(ex.getCode(), ex.getMessage());
    }



    /**
     * 处理未知异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public BaseResult handleUnKnowException(Exception ex){
        return ResultUtils.error(BasicInfoStatusEnum.UNKNOWN_ERROR.getCode(), ex.getMessage());
    }


    /**
     * 文件太大异常
     * @param exception
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = MultipartException.class)
    public Object fileUploadExceptionHandler(MultipartException exception){
        return ResultUtils.error(BasicInfoStatusEnum.FILE_MAN_ERROR.getCode(), BasicInfoStatusEnum.FILE_MAN_ERROR.getDesc());
    }

}
