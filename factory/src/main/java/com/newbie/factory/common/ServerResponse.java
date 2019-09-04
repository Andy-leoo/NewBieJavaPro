package com.newbie.factory.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * <Description> <br>
 *
 * @author Andy-J<br>
 * @version 1.0<br>
 * @taskId: <br>
 * @createDate 2019/08/26 11:25 <br>
 * 服务响应对象
 * @see com.newbie.factory <br>
 */
public class ServerResponse<T> {
    private int status;
    private String message;
    private T data;

    public ServerResponse(int status) {
        this.status = status;
    }

    public ServerResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    public ServerResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
    /**在json序列化中不出现*/
    @JsonIgnore
    public boolean isSuccess(){return this.status == ResponseCode.SUCCESS.getCode();}

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createBySuccessMsg(String msg){
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(),msg);
    }

    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(),data);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
    }

    public static <T> ServerResponse<T> createByErrorMsg(String errorMsg){
        return new ServerResponse<>(ResponseCode.ERROR.getCode(),errorMsg);
    }

    public static <T> ServerResponse<T> createByError(int errorCode,String errorMsg){
        return new ServerResponse<>(errorCode,errorMsg);
    }
}
