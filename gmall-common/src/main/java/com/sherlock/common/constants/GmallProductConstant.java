package com.sherlock.common.constants;

/**
 * @auther Sherlock
 * @date 2020/6/10 22:55
 * @Description:
 */
public interface GmallProductConstant {

    public enum ProductAttrEnum {
        ATTR_TYPE_BASE(1, "基本"), ATTR_TYPE_SALE(0, "销售");
        private int code;
        private String message;

        ProductAttrEnum(int code, String message) {
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

    public enum ProductStatusEnum {
        NEW_SPU(0, "新建"),
        SPU_UP(1, "上架"),
        SPU_DOWN(2, "商品下家");
        private int code;
        private String message;

        ProductStatusEnum(int code, String message) {
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
