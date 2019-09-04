package com.newbie.factory.common;

/**
 * <Description> <br>
 *
 * @author Andy-J<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2019/08/26 12:39 <br>
 * 枚举类
 * @see com.newbie.factory <br>
 */
public enum ResponseCode {
    ERROR(0,"ERROR"),
    SUCCESS(1,"SUCCESS"),
    NEED_LOGIN(10,"需要登入")
    ;
    private final int code;
    private final String desc;

    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
