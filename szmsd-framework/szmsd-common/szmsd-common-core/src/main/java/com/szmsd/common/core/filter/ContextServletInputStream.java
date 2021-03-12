package com.szmsd.common.core.filter;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ContextServletInputStream extends ServletInputStream {

    ServletInputStream servletInputStream;
    ByteArrayInputStream byteArrayInputStream;
    ByteArrayOutputStream byteArrayOutputStream;
    private StringBuilder buffer;

    public ContextServletInputStream(ServletInputStream servletInputStream) {
        this.servletInputStream = servletInputStream;
        try {
            // byte[] byteArray = IOUtils.toByteArray(this.servletInputStream);
            byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = servletInputStream.read(buffer))) {
                byteArrayOutputStream.write(buffer, 0, n);
            }
            this.byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.buffer = new StringBuilder();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener listener) {

    }

    @Override
    public int read() throws IOException {
        int data = byteArrayInputStream.read();
        buffer.append((char) data);
        return data;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int data = byteArrayInputStream.read(b);
        if (data > 0) {
            buffer.append(new String(b));
        }
        return data;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int data = byteArrayInputStream.read(b, off, len);
        if (data > 0) {
            buffer.append(new String(b, off, data));
        }
        return data;
    }

    @Override
    public int readLine(byte[] b, int off, int len) throws IOException {
        if (len <= 0) {
            return 0;
        } else {
            int count = 0;
            int c;
            while ((c = this.read()) != -1) {
                b[off++] = (byte) c;
                ++count;
                if (c == 10 || count == len) {
                    break;
                }
            }
            return count > 0 ? count : -1;
        }
    }

    public String getContent() {
        if (null == byteArrayOutputStream) {
            return "";
        }
        return new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
    }
}
