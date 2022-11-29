package com.loyer.common.core.handler;

import com.loyer.common.dedicine.entity.ApiResult;
import com.loyer.common.dedicine.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * 全局异常处理器
 *
 * @author kuangq
 * @date 2019-07-19 9:36
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 获取响应结果
     *
     * @author kuangq
     * @date 2021-02-08 15:33
     */
    public static Object getResponseResult(Object object) {
        //自定义业务异常
        if (object instanceof BusinessException) {
            return ApiResult.failure((BusinessException) object);
        }
        //实体类入参校验异常
        else if (object instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException e = (MethodArgumentNotValidException) object;
            Optional<ObjectError> optionalObjectError = e.getBindingResult().getAllErrors().stream().findFirst();
            if (optionalObjectError.isPresent()) {
                return ApiResult.failure(optionalObjectError.get().getDefaultMessage(), optionalObjectError.get().getObjectName());
            }
        }
        //表单请求入参必填校验
        else if (object instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException e = (MissingServletRequestParameterException) object;
            return ApiResult.failure(e.getMessage(), null);
        }
        //其他Exception
        else if (object instanceof Exception) {
            return ApiResult.failure((Exception) object);
        }
        return object;
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    private Object handle(Exception e, HttpServletRequest httpServletRequest) {
        //获取响应结果
        Object object = getResponseResult(e);
        //获取错误日志详情
        String errorMsg = object instanceof ApiResult ? ((ApiResult) object).getMsg() : object.toString();
        //日志打印
        logger.error("【全局异常捕获】{}：{}：{}", httpServletRequest.getLocalAddr(), httpServletRequest.getRequestURI(), errorMsg);
        return object;
    }
}