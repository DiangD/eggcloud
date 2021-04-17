package com.qzh.eggcloud.common.exception;

import com.qzh.eggcloud.common.resp.ErrorCode;
import com.qzh.eggcloud.common.resp.JsonResult;
import com.qzh.eggcloud.common.utils.RespUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName CommonExceptionHandler
 * @Author DiangD
 * @Date 2021/3/6
 * @Version 1.0
 * @Description 异常全局处理
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {BindException.class, ValidationException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<JsonResult<List<String>>> handleParameterVerificationException(Exception e) {
        List<String> errors = new ArrayList<>();
        if (e instanceof MethodArgumentNotValidException) {
            List<FieldError> fieldErrors = ((MethodArgumentNotValidException) e).getFieldErrors();
            if (fieldErrors.size() > 0) {
                for (FieldError fieldError : fieldErrors) {
                    errors.add(fieldError.getDefaultMessage());
                }
            }
        } else if (e instanceof BindException) {
            List<FieldError> fieldErrors = ((BindException) e).getFieldErrors();
            if (fieldErrors.size() > 0) {
                for (FieldError fieldError : fieldErrors) {
                    errors.add(fieldError.getDefaultMessage());
                }
            }
        } else if (e instanceof ConstraintViolationException) {
            String msg = e.getMessage();
            if (msg != null) {
                int lastIndex = msg.lastIndexOf(':');
                if (lastIndex >= 0) {
                    msg = msg.substring(lastIndex + 1).trim();
                }
                errors.add(msg);
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(RespUtil.generate(ErrorCode.InvalidParam, errors));
    }

    @ExceptionHandler(value = {BaseException.class})
    public ResponseEntity<JsonResult<Object>> handlerBaseException(Exception e) {
        if (e instanceof BaseException) {
            BaseException baseException = (BaseException) e;
            JsonResult<Object> jsonResult = RespUtil.generate(baseException.getCode(), baseException.getMessage(), null);
            return ResponseEntity.ok(jsonResult);
        }
        return ResponseEntity.ok(RespUtil.generate(ErrorCode.Fail.getCode(), e.getMessage(), null));
    }


//    @ExceptionHandler(value = {Exception.class})
//    public ResponseEntity<JsonResult<Object>> handlerException(Exception e) {
//        if (e instanceof MaxUploadSizeExceededException) {
//            MaxUploadSizeExceededException ex = (MaxUploadSizeExceededException) e;
//            JsonResult<Object> jsonResult = RespUtil.generate(ErrorCode.UploadFileSizeExceed, ex.getMaxUploadSize());
//            return ResponseEntity.ok(jsonResult);
//        }
//        System.out.println(e);
//        return ResponseEntity.ok(RespUtil.generate(ErrorCode.InternalServerError, null));
//    }
}
