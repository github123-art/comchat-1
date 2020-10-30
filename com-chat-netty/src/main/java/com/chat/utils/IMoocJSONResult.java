package com.chat.utils;

/**
 *   自定义响应数据结构：
 *              提供给门户，ios,安卓，微信商城用的
 *              门户接受此类数据后需要使用本类的方法转换成对应的数据类型格式(类，或list)
 *              其他自行处理
 *              202：成功
 *              500：错误，错误信息在msg字段中
 *              501：bean验证错误，不管多少个错误都以map形式返回
 *              502：拦截器拦截到用户token出错
 *              555：异常抛出信息
 *
 */
public class IMoocJSONResult {

    //响应业务状态
    private static Integer status;

    //响应消息
    private static String msg;

    //响应中的数据
    private static Object data;

    private static String ok;  //不使用

    public IMoocJSONResult(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }


    public static IMoocJSONResult build(Integer status, String msg, Object data){
        return new IMoocJSONResult(status,msg,data);
    }

    public static IMoocJSONResult ok(Object data) {
        return new IMoocJSONResult(data);
    }

    public IMoocJSONResult(Object data){
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }

    /**
     *    返回错误信息
     * @return
     */
    public static IMoocJSONResult errorMsg(String msg){
        return   new IMoocJSONResult(500,msg,data);
    }

    /**
     *    提示信息
     * @return
     */
    public static IMoocJSONResult hintMsg(String msg){
        return new IMoocJSONResult(502,msg,data);
    }

    public Boolean isOK(){
        return  this.status == 200;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }
}
