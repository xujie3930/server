package com.szmsd.http.enums;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @ClassName: RemoteConstant
 * @Description:
 * @Author: 11
 * @Date: 2021-11-10 20:16
 */
public class RemoteConstant {

    public static Integer max = Arrays.stream(RemoteTypeEnum.values()).map(Enum::ordinal).max(Comparator.comparingInt(integer -> integer)).orElse(0) - 1;

    @Getter
    @AllArgsConstructor
    public enum RemoteTypeEnum {
        /**
         * 默认
         */
        DEFAULT(0, "CK1", Object.class),
        /**
         * 创建sku
         */
        SKU_CREATE(1, "CK1", Object.class),
        /**
         * 上架SKU
         */
        SKU_ON_SELL(2, "CK1", Object.class),
        /**
         * 创建入库单
         */
        CREATE_WAREHOUSE_ORDER(3, "CK1", Object.class),
        /**
         * 入库完成
         */
        WAREHOUSE_ORDER_COMPLETED(4, "CK1", Object.class),
        /**
         * 手动调整库存
         */
        ADJUST_INVENTORY(5, "CK1", Object.class),
        ;
        private Integer typeCode;
        private String typeName;
        private Class<?> classTypeObj;

        RemoteTypeEnum(int typeCode, String typeName) {
            this.typeCode = typeCode;
            this.typeName = typeName;
        }

       /* public List<?> getClassType(String text) {
            return JSONObject.parseArray(text, this.getClassType());
        }*/

        public static List<?> getClassTypeObjList(Integer typeCode, String text) {
            Class<?> classType = getScanEnumByType(typeCode).getClassTypeObj();
            return JSONObject.parseArray(text, classType);
        }

//        public static   getClassTypeObj(Integer typeCode, String text) {
//            Class<?> classType = getScanEnumByType(typeCode).getClassTypeObj();
//            return JSONObject.parseObject(text, classType);
//        }

        public static RemoteTypeEnum getScanEnumByType(Integer typeCode) {
            return Arrays.stream(RemoteTypeEnum.values()).filter(x -> x.getTypeCode().equals(typeCode)).findAny().orElseThrow(() -> new RuntimeException("类型不支持"));
        }

        public static RemoteTypeEnum getScanEnumByTypeOrElse(Integer typeCode) {
            return Arrays.stream(RemoteTypeEnum.values()).filter(x -> x.getTypeCode().equals(typeCode)).findAny().orElse(DEFAULT);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum RemoteStatusEnum {
        WAIT(0),
        SUCCESS(2),
        FAIL(1),
        REPEAT(3);
        private Integer status;
    }
}
