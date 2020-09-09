package com.sherlock.common.constants;

/**
 * @Author xueshuai
 * @Description: Gmall常量类
 * @Date 2020/9/4
 **/
public interface GmallOrderConstant {


    public static final String GMALL_ORDER_TOKEN_PREFIX = "order:token:";

    public static final String ORDER_EVENT_EXCHANGE = "order-event-exchange";
    public static final String ORDER_RELEASE_ORDER_QUEUE_NAME = "order.release.order.queue";
    public static final String ORDER_DELAY_QUEUE_NAME = "order.delay.queue";
    public static final String ORDER_RELEASE_ORDER_ROUTING_KEY_NAME = "order.release.order";
    public static final String ORDER_CREATE_ORDER_ROUTING_KEY_NAME = "order.create.order";
    public static final String ORDER_RELEASE_OTHER_ROUTING_KEY_NAME = "order.release.other.#";
    public static final String ORDER_RELEASE_OTHER_ROUTING_KEY = "order.release.other";


    public static final int X_MESSAGE_TTL_TIME = 1000 * 60 * 1;

    // 订单自动收货时间
    public static final int GMALL_ORDER_AUTO_CONFIRM_DAY = 7;

    public enum  OrderStatusEnum {

        CREATE_NEW(0,"待付款"),
        PAYED(1,"已付款"),
        SENDED(2,"已发货"),
        RECIEVED(3,"已完成"),
        CANCLED(4,"已取消"),
        SERVICING(5,"售后中"),
        SERVICED(6,"售后完成");
        private Integer code;
        private String msg;

        OrderStatusEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    public enum OrderDeleteStatusEnum {

        NOT_DELETE(0,"未删除"),
        DELETE(1,"已删除");
        private Integer code;
        private String msg;

        OrderDeleteStatusEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    public enum OrderConfirmStatusEnum {

        NOT_CONFIRM(0,"未确认"),
        CONFIRMED(1,"已签收");
        private Integer code;
        private String msg;

        OrderConfirmStatusEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

}
