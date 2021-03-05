package com.szmsd.bas.enums;

import com.szmsd.common.core.constant.CommonConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author liyingfeng
 * @date 2020/8/18 9:40
 */
@Getter
@AllArgsConstructor
public enum FileTypeEnum {
    //临时存放
    TEMP("TEMP", "临时存放", CommonConstant.PREFIX_TEMP),
    //其他
    OTHER("OTHER", "其他", CommonConstant.PREFIX_FILE),
    ;

    private final String code;

    private final String info;

    private final String commonConstant;

    /**
     * 根据code获取enum对象
     *
     * @param code
     * @return
     */
    public static FileTypeEnum getEnumByKey(String code) {
        for (FileTypeEnum fileTypeEnum : values()) {
            if (fileTypeEnum.getCode().equals(code)) {
                return fileTypeEnum;
            }
        }
        return FileTypeEnum.OTHER;
    }

}
