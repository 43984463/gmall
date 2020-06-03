package com.sherlock.gmall.product.exception;

import com.sherlock.common.constants.GmallConstant;
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
@RestControllerAdvice(basePackages = GmallConstant.GMALL_PRODUCT_CONTROLLER_BASEPATH)
public class GmallProductExceptionHandler {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public R handleProductVaildException(MethodArgumentNotValidException e) {
        log.error("handleProductVaildException,数据校验出现问题{}，异常类型：{}",e.getMessage(),e.getClass());
        Map errorMaps = new HashMap();
        if (e.getBindingResult().hasErrors()) {
            e.getBindingResult().getFieldErrors().stream().forEach(item -> errorMaps.put(item.getField(), item.getDefaultMessage()));
            return R.error(BizCodeEnume.VALID_EXCEPTION.getCode(), BizCodeEnume.VALID_EXCEPTION.getMsg()).put("errors", errorMaps);
        } else {
            return R.ok();
        }
    }

    @ExceptionHandler(value = Throwable.class)
    public R handleProductException(Throwable e) {
        log.error("handleProductException错误：",e);
        return R.error(BizCodeEnume.UNKNOW_EXCEPTION.getCode(), BizCodeEnume.UNKNOW_EXCEPTION.getMsg());
    }
}
