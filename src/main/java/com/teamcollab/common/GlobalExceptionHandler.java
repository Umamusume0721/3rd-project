package com.teamcollab.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Void> handleBiz(BizException e) {

        log.warn("业务异常: code={}, msg={}", e.getErrorCode().getCode(), e.getMessage());
        return R.fail(e.getErrorCode().getCode(), e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.OK)
    public R<Void> handleValid(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String msg = fieldError == null ? ErrorCode.PARAM_ERROR.getMessage()
                : fieldError.getField() + " " + fieldError.getDefaultMessage();
        return R.fail(ErrorCode.PARAM_ERROR, msg);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Void> handleOther(Exception e) {
        log.error("未捕获异常", e);
        return R.fail(ErrorCode.SERVER_ERROR);
    }
}
