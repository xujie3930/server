package com.szmsd.common.core.utils;

import com.github.pagehelper.util.StringUtil;
import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.exception.com.LogisticsExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.szmsd.common.core.web.controller.BaseController.getLen;

/**
 * @author liyingfeng
 * @date 2020/9/15 13:00
 */
public class CmdUtil {

    private static final Logger log = LoggerFactory.getLogger(CmdUtil.class);
    /**
     * 执行脚本
     * @param path
     */
//    public static void main(String[] args) {
//        handler("F:\\work\\CITI\\Gulf System\\RTFTP\\Manifest\\upload.bat");
//    }
    public static void handler(String path){
//        String path = "F:\\work\\static\\Gulf System\\RTFTP\\Manifest\\upload.bat";
        if(StringUtil.isEmpty(path) || !(new File(path).exists())){
            throw LogisticsExceptionUtil.getException(ExceptionMessageEnum.EXPWAYBILL033,null,path);
        }
        Process process = null;
        try {
//            cmd.exe /c F: && cd F:\\work\\CITI\\Gulf System\\RTFTP\\Manifest\\upload && start " + path.replaceAll(" ", "\" \"")
            StringBuilder cmdText = new StringBuilder("cmd /c ");
            cmdText.append("cd ");
            cmdText.append(new File(path).getParentFile().toString());
            cmdText.append(" && ");
            cmdText.append("start /b ");
            cmdText.append(path.replaceAll(" ", "\" \""));
            process = Runtime.getRuntime().exec(cmdText.toString());
            new ProcessClearStream(process.getInputStream(),"in").start();
            new ProcessClearStream(process.getErrorStream(),"error").start();

            //关闭流释放资源
//            if(process != null){
//                process.getOutputStream().close();
//            }
//            process.destroy();

            int exitCode = process.waitFor();
            if (0 == exitCode) {
                log.info("脚本文件执行成功");
            } else {
                log.info("脚本文件执行失败");
                throw LogisticsExceptionUtil.getException(ExceptionMessageEnum.EXPWAYBILL034,null,path);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("申报失败:{}", e);
            log.info("命令执行失败:{}",e.toString());
            throw LogisticsExceptionUtil.getException(ExceptionMessageEnum.EXPWAYBILL034,null,path);
        }finally {
            if(null != process){
                try {
                    process.getOutputStream().close();
                    process.destroy();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * waitFor 会阻塞，先消费，不用行读，
     * @param is
     * @return
     * @throws IOException
     */
    private static String readInputStream(InputStream is) throws IOException
    {
//        BufferedReader br = new BufferedReader(new InputStreamReader(is, "GBK"));
        StringBuffer lines = new StringBuffer();
//        for (String line = br.readLine(); line != null; line = br.readLine())
//        {
//            log.info(line);
//            lines.append(line);
//        }
        byte[] by = new byte[1024];
        while (-1 != is.read(by)){
            String gbk = new String(by, "GBK");
            log.info(gbk);
            lines.append(gbk);
        }
        return lines.toString();
    }
}



//            ProcessBuilder pbuilder=new ProcessBuilder(
//                    "cmd /c  start /b ",
//                    path.replaceAll(" ", "\" \"")
//                    );
//            pbuilder.redirectErrorStream(true);
//            process=pbuilder.start();
//            BufferedReader reader=new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line=null;
//            while((line=reader.readLine())!=null){
//                System.err.println(line);
//            }