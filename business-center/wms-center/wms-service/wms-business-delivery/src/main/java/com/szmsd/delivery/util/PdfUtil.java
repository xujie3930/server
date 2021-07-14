package com.szmsd.delivery.util;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.File;
import java.io.IOException;

/**
 * @author zhangyuyuan
 * @date 2021-07-06 15:24
 */
public final class PdfUtil {

    private PdfUtil() {
    }

    /**
     * pdf合并
     *
     * @param targetPath 合并之后的路径
     * @param sourcePath 合并的pdf路径
     * @return boolean
     */
    public static boolean merge(String targetPath, String... sourcePath) throws IOException {
        PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
        int mergeSize = 0;
        for (String source : sourcePath) {
            File file = new File(source);
            if (file.exists()) {
                pdfMergerUtility.addSource(file);
                mergeSize++;
            }
        }
        if (mergeSize == 0) {
            return false;
        }
        pdfMergerUtility.setDestinationFileName(targetPath);
        // 文件太多，太大会失败，内存异常，IO异常等等
        pdfMergerUtility.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
        return true;
    }
}
