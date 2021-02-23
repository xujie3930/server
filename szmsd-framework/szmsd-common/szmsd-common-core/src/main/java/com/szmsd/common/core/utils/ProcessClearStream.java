package com.szmsd.common.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author liyingfeng
 * @date 2020/9/18 9:29
 */
@Slf4j
public class ProcessClearStream extends Thread {

    private InputStream inputStream;
    private String type;

    ProcessClearStream(InputStream inputStream, String type) {
        this.inputStream = inputStream;
        this.type = type;
    }

    public void run() {
        try {
//            byte[] by = new byte[1024];
//            while (-1 != inputStream.read(by)){
//                String gbk = new String(by, "GBK");
//                log.info(gbk);
//            }
            InputStreamReader inputStreamReader = new InputStreamReader(
                    inputStream,"GBK");
            BufferedReader br = new BufferedReader(inputStreamReader);
            // 打印信息
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(type + ">" + line);
            }
            // 不打印信息
//           while (br.readLine() != null);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
