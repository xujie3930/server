package com.szmsd.common.core.constant;

import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

/**
 * @FileName Response.java
 * @Description ----------功能描述---------
 * @Date 2020-06-24 14:52
 * @Author Yan Hua
 * @Version 1.0
 */
@SuppressWarnings("unused")
public interface CommonConstant {

	String ENCODING = StandardCharsets.UTF_8.name();
	MediaType MEDIA_TYPE = new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8);

	String DEBUG = "DEBUG";
	String INFO = "INFO";
	String WARN = "WARN";
	String ERROR = "ERROR";

	String EMPTY = "";
	String BLANK = " ";
	String COMMA = ",";
	String DOT = ".";
	String SEMICOLON = ";";
	String COLON = ":";
	String SPRIT = "/";
	String WELL = "#";
	String AT = "@";
	String PLAINT = "#";
	String DOLLAR = "$";
	String PERCENT = "%";
	String AND = "&";
	String ASTERISK = "*";
	String VERTICAL = "|";
	String ADD = "+";
	String SUBTRACT = "-";
	String WAVE = "~";

	/**
	 * 返回Response的field
	 */
	String RESPONSE_FIELD_CODE = "code";
	String RESPONSE_FIELD_MESSAGE = "message";
	String RESPONSE_FIELD_DATA = "data";
	String RESPONSE_FIELD_REQUESTID = "requestId";

	/**
	 * 地球半径
	 */
	double EARTH_RADIUS = 6378.137;

	/**
	 * 默认分页
	 */
	int DEFAULT_PAGE_NUM = 1;
	int DEFAULT_PAGE_SIZE = 20;

	/**
	 * 全局成功码
	 */
	String SUCCESS_CODE = "200";
	String SUCCESS_MSG = "操作成功";

	/**
	 * 全局错误码
	 */
	String DEFAULT_ERROR_CODE = "00099";
	String DEFAULT_ERROR_MSG = "系统内部错误";
	String METHOD_ARGUMENT_NOT_VALID_CODE = "00098";
	String METHOD_ARGUMENT_NOT_VALID_MSG = "参数校验不通过";
	String NO_AUTHORITY_CODE = "00097";
	String NO_AUTHORITY_MSG = "没有权限";
    String GET_USER_FAILED_CODE = "00096";
    String GET_USER_FAILED_MSG = "获取用户信息失败";
	String NO_RECORD_EFFECTED_CODE = "00095";
	String NO_RECORD_EFFECTED_MSG = "更新失败(没有符合条件的记录)";
	String INFO_NOT_SUPPORT_CODE = "00094";
	String INFO_NOT_SUPPORT_MSG = "该方法不支持";
	String CANNOT_FIND_CLASS_CODE = "00093";
	String CANNOT_FIND_CLASS_MSG = "找不到类";
	String REQUEST_TIMEOUT_CODE = "00092";
	String REQUEST_TIMEOUT_MSG = "请求超时";
    String DATA_ERROR_CODE = "00091";
    String DATA_ERROR_MSG = "数据异常";

	// 日期正则表达式
	String REG_DATE_FMT_Y = "^\\d{4}$";
	String REG_DATE_FMT_Y_M = "^\\d{4}-\\d{1,2}$";
	String REG_DATE_FMT_Y_M_D = "^\\d{4}-\\d{1,2}-\\d{1,2}$";
	String REG_DATE_FMT_Y_M_D_H = "^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}$";
	String REG_DATE_FMT_Y_M_D_HM = "^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}$";
	String REG_DATE_FMT_Y_M_D_HMS = "^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$";
	String REG_DATE_FMT_Y_M_D_HMSSS = "^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{1,3}$";



	// 文件存储前缀
	String PREFIX_FILE = "/file";
	String PREFIX_TEMP = "/temp"; //临时目录
	String PREFIX_PROBLEM_IMAGE = PREFIX_FILE + "/problem"; //问题件图片/文件
	String PREFIX_FILE_SIGN = PREFIX_FILE +"/sign"; //签收文件
	String PREFIX_FILE_WAYBILL = PREFIX_FILE +"/waybill"; //运单文件/图片
	String PREFIX_FILE_USER = PREFIX_FILE +"/user"; //运单文件/图片
	String PREFIX_FILE_NOTICE = PREFIX_FILE +"/notice"; //消息相关文件
}
