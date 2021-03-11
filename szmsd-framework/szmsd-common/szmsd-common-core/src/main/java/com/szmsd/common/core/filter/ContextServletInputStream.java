package com.szmsd.common.core.filter;

import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ContextServletInputStream extends ServletInputStream {

    ServletInputStream servletInputStream;
    ByteArrayInputStream byteArrayOutputStream;
    private StringBuilder buffer;

    public ContextServletInputStream(ServletInputStream servletInputStream) {
        this.servletInputStream = servletInputStream;
        try {
            byte[] byteArray = IOUtils.toByteArray(this.servletInputStream);
            this.byteArrayOutputStream = new ByteArrayInputStream(byteArray);
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
        int data = byteArrayOutputStream.read();
        buffer.append((char) data);
        return data;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int data = byteArrayOutputStream.read(b);
        if (data > 0) {
            buffer.append(new String(b));
        }
        return data;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int data = byteArrayOutputStream.read(b, off, len);
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
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(byteArrayOutputStream));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
