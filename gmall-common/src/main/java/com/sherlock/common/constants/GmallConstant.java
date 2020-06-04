package com.sherlock.common.constants;

/**
 * @Author xueshuai
 * @Description: Gmall常量类
 * @Date 2020/6/3
 **/
public interface GmallConstant {
    public static final String GMALL_PRODUCT_CONTROLLER_BASEPATH = "com.sherlock.gmall.product.controller";

    public enum Product_Attr_Enum {
        ATTR_TYPE_BASE(1, "基本"), ATTR_TYPE_SALE(0, "销售");
        private int code;
        private String message;

        Product_Attr_Enum(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
