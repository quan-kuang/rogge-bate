package com.loyer.common.dedicine.exception;

import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.utils.GeneralUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author kuangq
 * @title BusinessException
 * @description 自定义业务异常
 * @date 2019-07-19 9:21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    private Integer code;

    private String msg;

    private Object data;

    public BusinessException(String msg) {
        super(msg);
        this.code = HintEnum.HINT_1100.getCode();
        this.msg = msg;
    }

    public BusinessException(String msg, Object data) {
        super(msg);
        this.code = HintEnum.HINT_1100.getCode();
        this.msg = msg;
        this.data = data;
    }

    public BusinessException(HintEnum hintEnum) {
        super(hintEnum.getMsg());
        this.code = hintEnum.getCode();
        this.msg = hintEnum.getMsg();
    }

    public BusinessException(HintEnum hintEnum, Object data) {
        super(hintEnum.getMsg());
        this.code = hintEnum.getCode();
        this.msg = hintEnum.getMsg();
        if (data instanceof Exception) {
            this.data = GeneralUtil.getStackTrace((Exception) data);
        } else {
            this.data = data;
        }
    }
}