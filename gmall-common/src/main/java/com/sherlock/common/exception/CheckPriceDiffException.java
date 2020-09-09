package com.sherlock.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author xueshuai
 * @Description: TODO
 * @Date 2020/9/9
 **/
public class CheckPriceDiffException extends RuntimeException{


    @Setter @Getter
    private String msg;

    public CheckPriceDiffException(){
        super("");
    }

    public CheckPriceDiffException(String msg) {
        super(msg);
        this.msg = msg;
    }

}
