package com.sherlock.gmall.product.exception;

import com.sherlock.common.exception.BizCodeEnume;
import com.sherlock.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @auther Sherlock
 * @date 2020/5/30 23:02
 * @Description:
 */
@Slf4j
public class GmallProductExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R gmallGlobalExceptionHandler(MethodArgumentNotValidException e) {
        Map errorMaps = new HashMap();
        if (e.getBindingResult().hasErrors()) {
            e.getBindingResult().getFieldErrors().stream().forEach(item -> errorMaps.put(item.getField(), item.getDefaultMessage()));
            return R.error(BizCodeEnume.VALID_EXCEPTION.getCode(), BizCodeEnume.VALID_EXCEPTION.getMsg()).put("errors", errorMaps);
        } else {
            return R.ok();
        }
    }

    @ExceptionHandler(Throwable.class)
    public R gmallGlobalExceptionHandler(Throwable e) {
        return R.error(BizCodeEnume.UNKNOW_EXCEPTION.getCode(), BizCodeEnume.UNKNOW_EXCEPTION.getMsg());
    }
}
