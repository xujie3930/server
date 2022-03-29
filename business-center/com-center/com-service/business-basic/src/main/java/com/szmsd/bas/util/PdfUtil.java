package com.szmsd.bas.util;

import cn.hutool.extra.qrcode.BufferedImageLuminanceSource;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
            PDDocument doc = PDDocument.load(file.getInputStream());
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();

            for (int i = 0; i < pageCount; i++){
                BufferedImage image = renderer.renderImage(i, 2.0f);
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                ImageOutputStream imOut = ImageIO.createImageOutputStream(bs);
                ImageIO.write(image, "jpg", imOut);
                InputStream input = new ByteArrayInputStream(bs.toByteArray());



                String result = extractImages(image);

                int lastIndex = file.getOriginalFilename().lastIndexOf(".");

                String preName = file.getOriginalFilename().substring(0, lastIndex) + "-" + i;
                String lastName = file.getOriginalFilename().substring(lastIndex);

                MultipartFile multipartFile =new MockMultipartFile("file", preName + lastName, "text/plain", IOUtils.toByteArray(input));

                java.util.Map<String, Object> map = new HashMap<String, Object>();
                map.put("barCode", result);
                map.put("multipartFile", multipartFile);
                list.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;

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
