package com.szmsd.bas.util;

import cn.hutool.extra.qrcode.BufferedImageLuminanceSource;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PdfUtil {


    /**
     * 一个pdf文件转多个
     * @param file
     * @return
     */
    public static List<java.util.Map> toMonyFile(MultipartFile file){
        List<java.util.Map> list = new ArrayList<java.util.Map>();
        try {
            PDDocument document = PDDocument.load(file.getInputStream());
            Splitter splitter = new Splitter();

            // split the pages of a PDF document
            List<PDDocument> Pages = splitter.split(document);

            /* Creating an iterator */
            Iterator<PDDocument> iterator = Pages.listIterator();

            // saving splits as pdf
            int i = 0;
            while(iterator.hasNext()) {
                PDDocument doc = iterator.next();
                PDFRenderer renderer = new PDFRenderer(doc);
                BufferedImage image = renderer.renderImage(0, 2.0f);
                String result = extractImages(image);


                ByteArrayOutputStream pdfOps = new ByteArrayOutputStream();
                doc.save(pdfOps);
                int lastIndex = file.getOriginalFilename().lastIndexOf(".");

                String preName = file.getOriginalFilename().substring(0, lastIndex) + "-" + i;
                String lastName = file.getOriginalFilename().substring(lastIndex);

                MultipartFile multipartFile =new MockMultipartFile("file", preName + lastName, "pdf", pdfOps.toByteArray());
                java.util.Map<String, Object> map = new HashMap<String, Object>();
                map.put("barCode", result);
                map.put("multipartFile", multipartFile);
                list.add(map);
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;

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
    public static boolean merge(OutputStream ops, String... sourcePath) throws IOException {
        PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
        int mergeSize = 0;
        for (String source : sourcePath) {
            if (null == source || "".equals(source)) {
                continue;
            }
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
        OutputStream outputStream = new ByteArrayOutputStream();
        // 指定目标文件输出流
        pdfMergerUtility.setDestinationStream(outputStream);
        pdfMergerUtility.mergeDocuments(null);
        // 获取合并后的目标数据流
        ByteArrayOutputStream mergerUtilityDestinationStream = (ByteArrayOutputStream) pdfMergerUtility.getDestinationStream();
        mergerUtilityDestinationStream.writeTo(ops);
        outputStream.close();
        ops.close();
        return true;
    }
    /**
     * 解析pdf二维码内容
     * @param file
     * @return
     */
    public static String fileBarCodeParse(MultipartFile file){
        try {
            PDDocument doc = PDDocument.load(file.getInputStream());
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            if(pageCount > 0){
                BufferedImage image = renderer.renderImage(0, 2.0f);
                return extractImages(image);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 识别 png图片中的二维码信息
     */
    private static String extractImages(BufferedImage image){
        String returnResult = "";
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        // 定义二维码参数
        java.util.Map hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        // 获取读取二维码结果
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
        Result result = null;
        try {
            result = multiFormatReader.decode(binaryBitmap, hints);
            returnResult = result.getText();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        return returnResult;
    }

}
