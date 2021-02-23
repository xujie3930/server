/*
package com.szmsd.common.core.web.domain;

import java.util.HashMap;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.utils.StringUtils;

*/
/**
 * 操作消息提醒
 * 
 * @author szmsd
 *//*

public class R extends HashMap<String, Object>
{
    private static final long serialVersionUID = 1L;

    */
/** 状态码 *//*

    public static final String CODE_TAG = "code";

    */
/** 返回内容 *//*

    public static final String MSG_TAG = "msg";

    */
/** 数据对象 *//*

    public static final String DATA_TAG = "data";

    */
/**
     * 初始化一个新创建的 R 对象，使其表示一个空消息。
     *//*

    public R()
    {
    }

    */
/**
     * 初始化一个新创建的 R 对象
     * 
     * @param code 状态码
     * @param msg 返回内容
     *//*

    public R(int code, String msg)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    */
/**
     * 初始化一个新创建的 R 对象
     * 
     * @param code 状态码
     * @param msg 返回内容
     * @param data 数据对象
     *//*

    public R(int code, String msg, Object data)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (StringUtils.isNotNull(data))
        {
            super.put(DATA_TAG, data);
        }
    }

    */
/**
     * 返回成功消息
     * 
     * @return 成功消息
     *//*

    public static R success()
    {
        return R.ok("操作成功");
    }

    */
/**
     * 返回成功数据
     * 
     * @return 成功消息
     *//*

    public static R success(Object data)
    {
        return R.ok("操作成功", data);
    }

    */
/**
     * 返回成功消息
     * 
     * @param msg 返回内容
     * @return 成功消息
     *//*

    public static R success(String msg)
    {
        return R.ok(msg, null);
    }

    */
/**
     * 返回成功消息
     * 
     * @param msg 返回内容
     * @param data 数据对象
     * @return 成功消息
     *//*

    public static R success(String msg, Object data)
    {
        return new R(HttpStatus.SUCCESS, msg, data);
    }

    */
/**
     * 返回错误消息
     * 
     * @return
     *//*

    public static R error()
    {
        return R.failed("操作失败");
    }

    */
/**
     * 返回错误消息
     * 
     * @param msg 返回内容
     * @return 警告消息
     *//*

    public static R error(String msg)
    {
        return R.failed(msg, null);
    }

    */
/**
     * 返回错误消息
     * 
     * @param msg 返回内容
     * @param data 数据对象
     * @return 警告消息
     *//*

    public static R error(String msg, Object data)
    {
        return new R(HttpStatus.WEB_MSG, msg, data);
    }

    */
/**
     * 返回错误消息
     * 
     * @param code 状态码
     * @param msg 返回内容
     * @return 警告消息
     *//*

//    public static R error(int code, String msg)
//    {
//        return new R(code, msg, null);
//    }
}
*/
