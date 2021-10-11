package com.szmsd.delivery.util;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

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
            String fileExtName = getFileExtName(source);
            if ("pdf".equals(fileExtName)) {
                File file = new File(source);
                if (file.exists()) {
                    pdfMergerUtility.addSource(file);
                    mergeSize++;
                }
            } else {
                ByteArrayOutputStream byteArrayOutputStream = convertImgToPDF(source);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                pdfMergerUtility.addSource(byteArrayInputStream);
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

    public static ByteArrayOutputStream convertImgToPDF(String imagePath) throws IOException {
        PDDocument document = new PDDocument();
        InputStream in = new FileInputStream(imagePath);
        BufferedImage bimg = ImageIO.read(in);
        float width = bimg.getWidth();
        float height = bimg.getHeight();
        PDPage page = new PDPage(new PDRectangle(width, height));
        document.addPage(page);
        PDImageXObject img = PDImageXObject.createFromFile(imagePath, document);
        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.drawImage(img, 0, 0);
        contentStream.close();
        in.close();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();
        return outputStream;
    }

    /**
     * 获取文件扩展名，不包含"."点
     *
     * @param fileName 文件名
     * @return 文件扩展名
     */
    public static String getFileExtName(String fileName) {
        if (fileName.lastIndexOf(".") != -1) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "";
    }
}
