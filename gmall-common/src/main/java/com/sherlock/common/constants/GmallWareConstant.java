package com.sherlock.common.constants;

/**
 * @auther Sherlock
 * @date 2020/6/10 22:56
 * @Description:
 */
public class GmallWareConstant {

    public enum Purchase_Status_Enum {
        CREATED(0, "新建"),
        ASSIGNED(1, "已分配"),
        RECEIVE(2, "已领取"),
        FINISH(3, "已完成"),
        HAS_ERROR(4, "有异常");
        private int code;
        private String message;

        Purchase_Status_Enum(int code, String message) {
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

    public enum Purchase_Detail_Status_Enum {
        CREATED(0, "新建"),
        ASSIGNED(1, "已分配"),
        BUYING(2, "正在采购"),
        FINISH(3, "已完成"),
        HAS_ERROR(4, "采购失败");
        private int code;
        private String message;

        Purchase_Detail_Status_Enum(int code, String message) {
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
