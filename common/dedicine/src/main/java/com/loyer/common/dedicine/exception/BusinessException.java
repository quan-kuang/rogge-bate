package com.loyer.common.dedicine.exception;

import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.utils.ExceptionUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义业务异常
 *
 * @author kuangq
 * @date 2019-07-19 9:21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    private Integer code;

    private String msg;

    private Object data;

    public BusinessException(Throwable throwable) {
        super(ExceptionUtil.getErrorMessage(throwable));
        this.code = HintEnum.HINT_1001.getCode();
        this.msg = super.getMessage();
        this.data = ExceptionUtil.getStackTrace(throwable);
    }

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
            this.data = ExceptionUtil.getStackTrace((Exception) data);
        } else {
            this.data = data;
        }
    }
}