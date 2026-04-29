package com.xtu.utils;

import lombok.Data;

@Data
public class Result {
    private Integer code;
    private String msg;
    private Object data;

    public static Result success(Integer code,String msg){
        Result result = new Result();
        result.code = code;
        result.msg = msg;
        return result;
    }

    public static Result success(Integer code,String msg, Object object){
        Result result = new Result();
        result.data = object;
        result.code = code;
        result.msg = msg;
        return result;
    }

    public static Result error(Integer code,String msg){
        Result result = new Result();
        result.code = code;
        result.msg = msg;
        return result;
    }
}
