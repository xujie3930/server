package com.szmsd.delivery.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * itext pdf
 */
public final class ITextPdfUtil {

    /**
     * mm t px
     *
     * @param mm  mm
     * @param dpi dpi
     * @return px
     */
    public static float mm2px(int mm, float dpi) {
        return (mm / 25.4f) * dpi;
    }

    public static BufferedImage getBarCode(String vaNumber) throws WriterException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 0);
        int realWidth = getBarCodeNoPaddingWidth(200, vaNumber, 200) * 2;
        BitMatrix bitMatrix = new MultiFormatWriter().encode(vaNumber, BarcodeFormat.CODE_128, realWidth, 60, hints);
        MatrixToImageConfig matrixToImageConfig = new MatrixToImageConfig();
        return MatrixToImageWriter.toBufferedImage(bitMatrix, matrixToImageConfig);
    }

    public static int getBarCodeNoPaddingWidth(int expectWidth, String contents, int maxWidth) {
        boolean[] code = new Code128Writer().encode(contents);

        int inputWidth = code.length;

        double outputWidth = Math.max(expectWidth, inputWidth);
        double multiple = outputWidth / inputWidth;

        //优先取大的
        int returnVal = 0;
        int ceil = (int) Math.ceil(multiple);
        if (inputWidth * ceil <= maxWidth) {
            returnVal = inputWidth * ceil;
        } else {
            int floor = (int) Math.floor(multiple);
            returnVal = inputWidth * floor;
        }

        return returnVal;
    }

    public static BufferedImage insertWords(BufferedImage image, String words) {
        // 新的图片，把带logo的二维码下面加上文字
        if (StringUtils.isNotEmpty(words)) {

            int fontSize = 20;

            BufferedImage outImage = new BufferedImage(image.getWidth(), image.getHeight() + fontSize, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = outImage.createGraphics();

            // 抗锯齿
            setGraphics2D(g2d);
            // 设置白色
            setColorWhite(g2d, image.getWidth(), image.getHeight() + fontSize);

            // 画条形码到新的面板
            g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
            // 画文字到新的面板
            Color color = new Color(0, 0, 0);
            g2d.setColor(color);
            // 字体、字型、字号
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, fontSize));
            //文字长度
            int strWidth = g2d.getFontMetrics().stringWidth(words);
            //总长度减去文字长度的一半  （居中显示）
            int wordStartX = (image.getWidth() - strWidth) / 2;
            //height + (outImage.getHeight() - height) / 2 + 12
            int wordStartY = image.getHeight() + fontSize;

            // 画文字
            g2d.drawString(words, wordStartX, wordStartY);
            g2d.dispose();
            outImage.flush();
            return outImage;
        }
        return null;
    }

    /**
     * 设置 Graphics2D 属性  （抗锯齿）
     *
     * @param g2d Graphics2D提供对几何形状、坐标转换、颜色管理和文本布局更为复杂的控制
     */
    private static void setGraphics2D(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
        Stroke s = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        g2d.setStroke(s);
    }

    /**
     * 设置背景为白色
     *
     * @param g2d Graphics2D提供对几何形状、坐标转换、颜色管理和文本布局更为复杂的控制
     */
    private static void setColorWhite(Graphics2D g2d, int width, int height) {
        g2d.setColor(Color.WHITE);
        //填充整个屏幕
        g2d.fillRect(0, 0, width, height);
        //设置笔刷
        g2d.setColor(Color.BLACK);
    }
}
