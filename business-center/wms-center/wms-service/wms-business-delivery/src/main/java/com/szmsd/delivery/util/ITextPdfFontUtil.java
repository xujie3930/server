package com.szmsd.delivery.util;

import cn.hutool.cache.impl.LRUCache;
import com.szmsd.common.core.exception.com.CommonException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public final class ITextPdfFontUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ITextPdfFontUtil.class);

    private ITextPdfFontUtil() {
    }

    private static final LRUCache<String, byte[]> fontCache = new LRUCache<>(5);
    // private static Map<String, byte[]> fontCache = new HashMap<>();

    public static byte[] getFont(String font) {
        if (fontCache.containsKey(font)) {
            return fontCache.get(font);
        } else {
            Resource resource = new ClassPathResource(font);
            InputStream inputStream = null;
            byte[] fontBytes;
            if (resource.exists()) {
                ByteArrayOutputStream byteArrayOutputStream = null;
                try {
                    inputStream = resource.getInputStream();
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    IOUtils.copy(inputStream, byteArrayOutputStream);
                    fontBytes = byteArrayOutputStream.toByteArray();
                } catch (IOException e) {
                    LOGGER.error("加载字体失败," + e.getMessage(), e);
                    throw new CommonException("400", "加载字体失败，" + font + "，" + e.getMessage());
                } finally {
                    IOUtils.closeQuietly(byteArrayOutputStream);
                    IOUtils.closeQuietly(inputStream);
                }
            } else {
                URL fontUrl = ITextPdfFontUtil.class.getClassLoader().getResource(font);
                UrlResource urlResource = new UrlResource(fontUrl);
                ByteArrayOutputStream byteArrayOutputStream = null;
                try {
                    inputStream = urlResource.getInputStream();
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    IOUtils.copy(inputStream, byteArrayOutputStream);
                    fontBytes = byteArrayOutputStream.toByteArray();
                } catch (IOException e) {
                    LOGGER.error("加载字体失败," + e.getMessage(), e);
                    throw new CommonException("400", "加载字体失败，" + font + "，" + e.getMessage());
                } finally {
                    IOUtils.closeQuietly(byteArrayOutputStream);
                    IOUtils.closeQuietly(inputStream);
                }
            }
            fontCache.put(font, fontBytes);
            return fontBytes;
        }
    }

}
