package com.sherlock.common.constants;

/**
 * @auther Sherlock
 * @date 2020/6/10 22:56
 * @Description:
 */
public interface GmallWareConstant {

    public static final String STOCK_EVENT_EXCHANGE_NAME = "stock-event-exchange";
    public static final String STOCK_RELEASE_QUEUE_NAME = "stock.release.stock.queue";
    public static final String STOCK_LOCKED_QUEUE_NAME = "stock.locked.queue";

    public static final String X_MESSAGE_TTL = "x-message-ttl";
    public static final String X_DEAD_LETTER_EXCHANGE = "x-dead-letter-exchange";
    public static final String X_DEAD_LETTER_ROUTING_KEY = "x-dead-letter-routing-key";
    public static final int X_MESSAGE_TTL_TIME = 1000 * 60 * 2;

    public enum PurchaseStatusEnum {
        CREATED(0, "新建"),
        ASSIGNED(1, "已分配"),
        RECEIVE(2, "已领取"),
        FINISH(3, "已完成"),
        HAS_ERROR(4, "有异常");
        private int code;
        private String message;

        PurchaseStatusEnum(int code, String message) {
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

    public enum PurchaseDetailStatusEnum {
        CREATED(0, "新建"),
        ASSIGNED(1, "已分配"),
        BUYING(2, "正在采购"),
        FINISH(3, "已完成"),
        HAS_ERROR(4, "采购失败");
        private int code;
        private String message;

        PurchaseDetailStatusEnum(int code, String message) {
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

    public enum WareOrderTaskDetailStatusEnum {
        LOCKED(1, "已锁定"),
        UNLOCK(2, "已解锁"),
        DEDUCTION(3, "已扣减");
        private int code;
        private String message;

        WareOrderTaskDetailStatusEnum(int code, String message) {
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
