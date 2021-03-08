package com.szmsd.common.core.filter;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ContextServletInputStream extends ServletInputStream {

    private ByteArrayInputStream bais;
    private StringBuilder buffer;

    public ContextServletInputStream(byte[] body) {
        this.bais = new ByteArrayInputStream(body);
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
    public void setReadListener(ReadListener readListener) {

    }

    @Override
    public int read() throws IOException {
        int data = bais.read();
        if (data > 0) {
            buffer.append((char) data);
        }
        return data;
    }

    public String getContent() {
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(bais));
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
