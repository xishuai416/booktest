package com.wky.book.common.response;

import java.io.Serializable;

/**
 * https://www.jb51.net/article/195663.htm
 * 统一返回结果的实体
 * @version 1.0
 * @date 2022-04-02 17:16
 * @param <T>
 */
public class BaseResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private int status;

    /**
     * 提示消息
     */
    private String msg;

    /**
     * 返回的数据体
     */
    private T data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
