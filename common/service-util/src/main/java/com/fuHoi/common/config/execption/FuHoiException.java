package com.fuHoi.common.config.execption;

import com.fuHoi.common.result.ResultCodeEnum;
import lombok.Data;

/**
 * @title: fuHoiException
 * @Author Xie
 * @Date: 2023/5/27 15:41
 * @Version 1.0
 */
@Data
public class FuHoiException extends RuntimeException{
    private Integer code;

    private String message;

    /**
     * 通过状态码和错误消息创建异常对象
     * @param code
     * @param message
     */
    public FuHoiException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 接收枚举类型对象
     * @param resultCodeEnum
     */
    public FuHoiException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
        this.message = resultCodeEnum.getMessage();
    }

    @Override
    public String toString() {
        return "FuHoiException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }
}
