package com.wky.book.common.response;

/**
 * @author weikaiyu
 * @version 1.0
 * @date 2022-04-02 17:17
 */
public class ResultUtils {

    /**
     * 成功时生成result的方法,有返回数据
     */
    public static <T> BaseResult<T> success(T t) {
        BaseResult<T> result = new BaseResult<>();
        result.setStatus(ResultEnum.SUCCESS.getCode());
        result.setMsg(ResultEnum.SUCCESS.getMsg());
        result.setData(t);
        return result;
    }

    /**
     * 成功时生成result的方法,无返回数据
     */
    public static <T> BaseResult<T> success() {
        return success(null);
    }

    /**
     * 成功时生成result的方法,无返回数据
     */
    public static <T> BaseResult<T> success(String message) {
        return successMessage(message);
    }


    /**
     * 失败时生成result的方法
     */
    public static <T> BaseResult<T> success(int status, String msg) {
        BaseResult<T> result = new BaseResult<>();
        result.setStatus(status);
        result.setMsg(msg);
        return result;
    }
    /**
     * 成功时生成result的方法,有返回数据
     */
    public static <T> BaseResult<T> successMessage(String messag) {
        BaseResult<T> result = new BaseResult<>();
        result.setStatus(ResultEnum.SUCCESS.getCode());
        result.setMsg(messag);
        result.setData(null);
        return result;
    }


    /**
     * 失败时生成result的方法
     */
    public static <T> BaseResult<T> error(int status, String msg) {
        BaseResult<T> result = new BaseResult<>();
        result.setStatus(status);
        result.setMsg(msg);
        return result;
    }

    /**
     * 失败时生成result的方法
     */
    public static <T> BaseResult<T> error(String msg) {
        BaseResult<T> result = new BaseResult<>();
        result.setStatus(ResultEnum.DETAILS_DATA_BASIC_INFO_ID_IS_EMPTY.getCode());
        result.setMsg(msg);
        return result;
    }
}
