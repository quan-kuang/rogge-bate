package com.loyer.common.dedicine.entity;

import com.alibaba.fastjson.JSON;
import com.loyer.common.dedicine.enums.HintEnum;
import com.loyer.common.dedicine.exception.BusinessException;
import com.loyer.common.dedicine.utils.ExceptionUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API响应实体类
 *
 * @author kuangq
 * @date 2019-08-01 9:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult {

    //标示
    private Boolean flag;

    //异常编码
    private Integer code;

    //异常说明
    private String msg;

    //返回结果
    private Object data;

    public static ApiResult success() {
        return ApiResult.hintEnum(HintEnum.HINT_1000, null);
    }

    public static ApiResult success(Object data) {
        return ApiResult.hintEnum(HintEnum.HINT_1000, data);
    }

    public static ApiResult success(String msg, Object data) {
        return new ApiResult(true, HintEnum.HINT_1000.getCode(), msg, data);
    }

    public static ApiResult failure(Object data) {
        return ApiResult.hintEnum(HintEnum.HINT_1001, data);
    }

    public static ApiResult failure(String msg, Object data) {
        return new ApiResult(false, HintEnum.HINT_1100.getCode(), msg, data);
    }

    public static ApiResult failure(Throwable throwable) {
        return new ApiResult(false, HintEnum.HINT_1001.getCode(), ExceptionUtil.getErrorMessage(throwable), ExceptionUtil.getStackTrace(throwable));
    }

    public static ApiResult failure(BusinessException businessException) {
        return new ApiResult(false, businessException.getCode(), businessException.getMessage(), businessException.getData());
    }

    public static ApiResult hintEnum(HintEnum hintEnum) {
        return new ApiResult(hintEnum == HintEnum.HINT_1000, hintEnum.getCode(), hintEnum.getMsg(), null);
    }

    public static ApiResult hintEnum(HintEnum hintEnum, Object data) {
        return new ApiResult(hintEnum == HintEnum.HINT_1000, hintEnum.getCode(), hintEnum.getMsg(), data);
    }

    @Override
    public String toString() {
        return "{flag=" + flag + ", code=" + code + ", msg=" + msg + ", data=" + JSON.toJSONString(data) + "}";
    }
}