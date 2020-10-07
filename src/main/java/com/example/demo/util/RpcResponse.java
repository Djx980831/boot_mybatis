package com.example.demo.util;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * @param
 * @Description TODO
 * @Author dongjingxiong
 * @return
 * @Date 2020-03-17 23:16
 */
public class RpcResponse<T> implements Serializable {
    private int code;
    private String msg;
    private T data;

    public RpcResponse() {
    }

    private RpcResponse(int code) {
        this.code = code;
    }

    private RpcResponse(ErrorInfo errorInfo) {
        this.code = errorInfo.getCode();
        this.msg = errorInfo.getMessage();
    }

    private RpcResponse(ErrorInfo errorInfo, T data) {
        this.code = errorInfo.getCode();
        this.msg = errorInfo.getMessage();
        this.data = data;
    }

    public static <T> RpcResponse<T> success() {
        return new RpcResponse(0);
    }

    public static <T> RpcResponse<T> success(T data) {
        return new RpcResponse(new ErrorInfo(0, ""), data);
    }

    public static <T> RpcResponse<T> error(ErrorInfo errorInfo, T data) {
        return new RpcResponse(errorInfo, data);
    }

    public static <T> RpcResponse<T> error(ErrorInfo errorInfo) {
        return new RpcResponse(errorInfo);
    }

    public static <T> Boolean resultIsSuccess(RpcResponse<T> rpcResponse) {
        return null != rpcResponse && 0 == rpcResponse.getCode();
    }

    public static <T> Boolean resultIsSuccessWithData(RpcResponse<T> rpcResponse) {
        return null != rpcResponse && 0 == rpcResponse.getCode() && null != rpcResponse.getData();
    }

    @JsonIgnore
    public ErrorInfo getErrorInfo() {
        return new ErrorInfo(this.getCode(), this.getMsg());
    }

    public String toString() {
        return "{code:" + this.code + ", msg:" + this.msg + ", data:" + this.data.toString() + "}";
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(final int code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public T getData() {
        return this.data;
    }

    public void setData(final T data) {
        this.data = data;
    }
}
