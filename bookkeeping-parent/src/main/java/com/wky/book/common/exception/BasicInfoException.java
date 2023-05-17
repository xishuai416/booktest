package com.wky.book.common.exception;

/**
 *
 * @author weikaiyu
 * @version 1.0
 * @date 2022-04-02 17:21
 */
public class BasicInfoException extends RuntimeException {

    private int code;

    public BasicInfoException(int code, String msg){
        super(msg);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
