package com.szmsd.doc.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.szmsd.common.core.utils.StringUtils;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 谷歌条形码打印工具类
 * @author: francis
 * @date: 2021-07-31
 */
@Slf4j
public class GoogleBarCodeUtils {

    /**
     * 背景高度
     */
    private static final int BACKGROUND_HEIGHT = 800;

    /**
     * 背景宽度
     */
    private static final int BACKGROUND_WIDTH = 800;

    /**
     * 条形码宽度
     */
    private static final int WIDTH = 400;

    /**
     * 条形码高度
     */
    private static final int HEIGHT = 60;

    /**
     * 加文字 条形码
     */
    private static final int WORDHEIGHT = 85;

    /**
     * 字体样式
     */
    private static final String FONT_STYLE = "微软雅黑";

    /**
     * 字体大小
     */
    private static final int FONT_SIZE = 14;

    /**
     * 图片base64 字符串前缀
     */
    private static final String BASE64_PREFIX = "data:image/png;base64,";

    /**
     * 设置 条形码参数
     */
    private static Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>() {
        private static final long serialVersionUID = 1L;
        {
            // 设置编码方式
            put(EncodeHintType.CHARACTER_SET, "utf-8");
        }
    };

    /**
     * 生成 图片缓冲
     * @author fxbin
     * @param vaNumber  VA 码
     * @return 返回 BufferedImage
     */
    public static BufferedImage getBarCode(String vaNumber){
        Code128Writer writer = new Code128Writer();
        // 编码内容, 编码类型, 宽度, 高度, 设置参数
        BitMatrix bitMatrix = writer.encode(vaNumber, BarcodeFormat.CODE_128, WIDTH, HEIGHT, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * 生成条形码base64
     * <p>内容和显示文字一致</p>
     *
     * @param content 条形码内容
     * @return 返回 base64 字符串
     */
    public static String generateBarCodeBase64(String content){
        return generateBarCodeBase64(content, content);
    }

    /**
     * 生成条形码base64
     * <p>内容和显示文字不一致</p>
     * @param content 条形码内容
     * @param words 条形码下方显示文字
     * @return 返回 base64 字符串
     */
    public static String generateBarCodeBase64(String content, String words) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            BufferedImage image = insertWords(getBarCode(content), words);
            //io流
            //写入流中
            ImageIO.write(image, "png", stream);
            //转换成字节
            byte[] bytes = stream.toByteArray();
            BASE64Encoder encoder = new BASE64Encoder();
            //转换成base64串
            String png_base64 = encoder.encodeBuffer(bytes).trim();
            if(StringUtils.isNotEmpty(png_base64)) {
                return BASE64_PREFIX.concat(png_base64).replace("\\r","").replace("\r\n","").replace("\r","");
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.info("生成条形码失败: {}", e.getMessage());
        } finally {
            try {
                stream.flush();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 把带logo的二维码下面加上文字
     *
     * @param image 条形码图片
     * @param words 文字
     * @return 返回BufferedImage
     * @author fxbin
     */
    public static BufferedImage insertWords(BufferedImage image, String words) {
        // 新的图片，把带logo的二维码下面加上文字
        if (StringUtils.isNotEmpty(words)) {

            BufferedImage outImage = new BufferedImage(WIDTH, WORDHEIGHT, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2d = outImage.createGraphics();

            // 抗锯齿
            setGraphics2D(g2d);
            // 设置白色
            setColorWhite(g2d);

            // 画条形码到新的面板
            g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
            // 画文字到新的面板
            Color color = new Color(0, 0, 0);
            g2d.setColor(color);
            // 字体、字型、字号
            g2d.setFont(new Font(FONT_STYLE, Font.PLAIN, FONT_SIZE));
            //文字长度
            int strWidth = g2d.getFontMetrics().stringWidth(words);
            //总长度减去文字长度的一半  （居中显示）
            int wordStartX = (WIDTH - strWidth) / 2;
            //height + (outImage.getHeight() - height) / 2 + 12
            int wordStartY = HEIGHT + 20;

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
    private static void setColorWhite(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        //填充整个屏幕
        g2d.fillRect(0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        //设置笔刷
        g2d.setColor(Color.BLACK);
    }

    public static void main(String[] args) throws IOException {
        String scnywo7000209 = generateBarCodeBase64("SCNYWO7000209");
        System.out.println(scnywo7000209);
    }

}
