package com.szmsd.delivery;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.szmsd.delivery.util.GoogleBarCodeUtils;
import com.szmsd.delivery.util.ITextPdfUtil;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TestResources {

    @Test
    public void getResources() throws IOException, DocumentException {


        URL resource = TestResources.class.getClassLoader().getResource("fonts/ARIALUNI.TTF");

        System.out.println(resource);
        System.out.println(resource.getPath());
        System.out.println(resource.getFile());

        BaseFont bf = BaseFont.createFont(resource.getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        System.out.println(bf.getWidth("汉字宽度"));
    }

    @Test
    public void getBarCode() throws Exception {
        String content = "CKCNY37321092200000107";
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 0);
        int realWidth = 400;
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, realWidth, 60, hints);
        MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig();
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix, matrixToImageConfig);
        ImageIO.write(bufferedImage, "png", new File("F:\\u01\\www\\ck1\\wms-business-delivery\\simple\\label\\PackageTransfer\\2021\\09\\22\\bc3.png"));
    }

    @Test
    public void testGetBarCode() throws Exception {
        //                 CKCNY37321092200000107
        String vaNumber = "CKCNYWO721092200000004";

        BufferedImage bufferedImage = ITextPdfUtil.getBarCode(vaNumber);
        bufferedImage = ITextPdfUtil.insertWords(bufferedImage, vaNumber);

        ImageIO.write(bufferedImage, "png",
                new File("F:\\u01\\www\\ck1\\wms-business-delivery\\simple\\label\\PackageTransfer\\2021\\09\\22\\bc4.png"));
    }

    @Test
    public void test1() throws Exception {
        String vaNumber = "CKCNYWO721092200000004";
        String barCode = GoogleBarCodeUtils.generateBarCodeBase64(vaNumber);
        System.out.println(barCode);
    }
}
