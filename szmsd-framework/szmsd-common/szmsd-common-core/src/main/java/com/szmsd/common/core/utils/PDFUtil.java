package com.szmsd.common.core.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 *  pdf文件工具类
 *
 * @author Zhang
 * @date 2020-07-16
 * @description
 */
@Slf4j
public class PDFUtil {

    /**
     *
     * @param inputStream pdf流
     * @param imagePath 图片的本地地址
     * @return
     * @throws IOException
     */
    public static String pdf2png(InputStream inputStream, String imagePath) throws IOException {
        if(null == inputStream){
            return null;
        }
        try (PDDocument doc = PDDocument.load(inputStream)) {
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            if (pageCount == 1) {
                BufferedImage image = renderer.renderImageWithDPI(0, 144);
                write(image, imagePath);
                return imagePath;
            } else {
                List<String> pathList = new ArrayList<>(16);
                for (int i = 0; i < pageCount; ++i) {
                    BufferedImage image = renderer.renderImageWithDPI(i, 144);
                    write(image, imagePath);
                    pathList.add(imagePath);
                }
                return JSON.toJSONString(pathList);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("PDF转图片失败");
        }
    }

    private static void write(BufferedImage image, String path) throws IOException {
        File file = new File(path);
        if (!file.isFile()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
        }
        ImageIO.write(image, "png", new File(path));
    }
}
