package com.szmsd.putinstorage.annotation;

import com.szmsd.putinstorage.enums.InboundReceiptRecordEnum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface InboundReceiptLog {

    InboundReceiptRecordEnum record();

}
