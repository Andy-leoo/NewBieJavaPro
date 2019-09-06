package com.newbie.factory.utils;

import java.math.BigDecimal;

/**
 * @author Andy-J<br>
 * @version 1.0<br>
 * @createDate 2019/9/4 11:47 <br>
 * @desc 处理金额类
 */
public class BigDecimalUtil {
    private BigDecimalUtil(){

    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/9/4 11:51 <br>
     * @desc 加法
     * */
    public static BigDecimal add(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2);
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/9/4 11:51 <br>
     * @desc 减法
     */
    public static BigDecimal sub(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2);
    }


    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/9/4 11:51 <br>
     * @desc 乘法
     */
    public static BigDecimal mul(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2);
    }

    /**
     * @author Andy-J<br>
     * @version 1.0<br>
     * @createDate 2019/9/4 11:52 <br>
     * @desc 除法  需要传 保留位数 声明四舍五入
     */
    public static BigDecimal div(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);//四舍五入,保留2位小数

        //除不尽的情况
    }

}
