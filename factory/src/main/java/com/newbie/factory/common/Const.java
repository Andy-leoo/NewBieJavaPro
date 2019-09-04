package com.newbie.factory.common;

import java.util.Set;

/**
 * <Description> <br>
 *  常量类
 * @author Andy-J<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2019/08/26 14:45 <br>
 * @see com.newbie.factory <br>
 */
public class Const {

    public static final String CURRENT_USER = "currentUser";

    public static final String USERNAME = "username";

    public static final String EMAIL = "email";

    public static final String MOBILE = "mobile";

    public interface Role{
        int ROLE_ADMIN =1;//管理员
        int ROLE_CUSTOMER = 0;//普通用户
    }

    public enum ProductStatusEnum {
        NO_SALE(1, "在线");
        private int code;
        private String msg;

        ProductStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    public interface ProductListOrderBy {
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc", "price_asc");
    }
}
