package com.szmsd.delivery.util;

import cn.hutool.cache.impl.LRUCache;
import com.szmsd.common.core.exception.com.CommonException;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public final class ITextPdfFontUtil {

    private ITextPdfFontUtil() {
    }

    private static final LRUCache<String, byte[]> fontCache = new LRUCache<>(5);
    // private static Map<String, byte[]> fontCache = new HashMap<>();

    public static byte[] getFont(String font) {
        if (fontCache.containsKey(font)) {
            return fontCache.get(font);
        } else {
            URL fontUrl = ITextPdfFontUtil.class.getClassLoader().getResource(font);
            UrlResource urlResource = new UrlResource(fontUrl);
            InputStream inputStream = null;
            byte[] fontBytes;
            try {
                inputStream = urlResource.getInputStream();
                fontBytes = new byte[inputStream.available()];
                inputStream.read(fontBytes);
                fontCache.put(font, fontBytes);
            } catch (IOException e) {
                throw new CommonException("400", "加载字体失败，" + font + "，" + e.getMessage());
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
            return fontBytes;
        }
    }

}
