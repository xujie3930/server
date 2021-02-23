package com.szmsd.common.core.exception.com;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @FileName BaseException.java
 * @Description 异常消息模板
 * @Date 2020-06-15 14:07
 * @Author Yan Hua
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class BaseException extends RuntimeException {

	/**
	 * the serialVersionUID
	 */
	private static final long serialVersionUID = 1381325479896057076L;

	private String code;
	private String message;
	private Throwable throwable;
	private transient Object[] values;

}
