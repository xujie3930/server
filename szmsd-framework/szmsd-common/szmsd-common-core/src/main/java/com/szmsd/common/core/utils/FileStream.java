package com.szmsd.common.core.utils;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class FileStream implements Serializable {

    private String contentDisposition;

    private byte[] inputStream;

}
