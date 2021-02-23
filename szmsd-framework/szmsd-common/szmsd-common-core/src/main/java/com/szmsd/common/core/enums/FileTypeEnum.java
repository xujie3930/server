package com.szmsd.common.core.enums;

/**
 * @author liyingfeng
 * @date 2020/8/18 9:40
 */
public enum FileTypeEnum
{
    //运单
    WAYBILL("WAYBILL","运单")
    //临时存放
    ,TEMP("TEMP","临时存放")
    //问题件
    ,PROBLEM("PROBLEM","问题件")
    //签收
    ,SIGN("SIGN","签收")
    //用户相关
    ,USER("USER","用户相关")
    //消息相关
    ,NOTICE("NOTICE","消息相关")
    //其他
    ,OTHER("OTHER","其他")
    ;

    private final String code;

    private final String info;

    FileTypeEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    /**
     * 根据code获取enum对象
     * @param code
     * @return
     */
    public static FileTypeEnum getEnumByKey(String code) {
        for (FileTypeEnum fileTypeEnum : values()) {
            if (fileTypeEnum.getCode().equals(code))
                return fileTypeEnum;
        }
        return FileTypeEnum.OTHER;
    }
}
