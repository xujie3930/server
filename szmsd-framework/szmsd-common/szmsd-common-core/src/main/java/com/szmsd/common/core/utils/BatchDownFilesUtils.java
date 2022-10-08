package com.szmsd.common.core.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author jiangjun
 * @version 1.0
 * @description
 * @date 2020/8/17 17:29
 */
public class BatchDownFilesUtils {
    /**
     * @Description 输入文件集合，zipPath临时路径,request,response导出zip文件
     * @Param [files, zipPath, request, response]
     * @Return javax.servlet.http.HttpServletResponse
     */
    public static HttpServletResponse downLoadFiles(List<File> files,
                                                    String zipPath,
                                                    HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        try {
            /**这个集合就是你想要打包的所有文件，
             * 这里假设已经准备好了所要打包的文件
             */

            //List<File> files = new ArrayList<File>();

            /**创建一个临时压缩文件，
             * 我们会把文件流全部注入到这个文件中
             * 这里的文件你可以自定义是.rar还是.zip
             　　      * 这里的file路径发布到生产环境时可以改为
             */
            File file = new File(zipPath);
            if (!file.exists()) {
                file.createNewFile();
            }
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/vnd.ms-excel");
            //response.reset();
            //response.getWriter()
            //创建文件输出流
            FileOutputStream fous = new FileOutputStream(file);
            /**打包的方法我们会用到ZipOutputStream这样一个输出流,
             * 所以这里我们把输出流转换一下*/
            //            org.apache.tools.zip.ZipOutputStream zipOut
            //                = new org.apache.tools.zip.ZipOutputStream(fous);
            ZipOutputStream zipOut = new ZipOutputStream(fous);
            /**这个方法接受的就是一个所要打包文件的集合，
             * 还有一个ZipOutputStream
             */
            zipFile(files, zipOut);
            zipOut.close();
            fous.close();
            return downloadZip(file, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**直到文件的打包已经成功了，
         * 文件的打包过程被我封装在FileUtil.zipFile这个静态方法中，
         * 稍后会呈现出来，接下来的就是往客户端写数据了
         */
        // OutputStream out = response.getOutputStream();


        return response;
    }

    /**
     * 把接受的全部文件打成压缩包
     */
    private static void zipFile
    (List files, ZipOutputStream outputStream) {
        int size = files.size();
        for (int i = 0; i < size; i++) {
            File file = (File) files.get(i);
            zipFile(file, outputStream);
        }
    }

    /**
     * @Description 下载zip文件，删除缓存文件
     * @Param [file, response]
     * @Return javax.servlet.http.HttpServletResponse
     */
    private static HttpServletResponse downloadZip(File file, HttpServletResponse response) {
        try {
            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            //response.reset();

            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                File f = new File(file.getPath());
                f.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    /**
     * 根据输入的文件与输出流对文件进行打包
     */
    private static void zipFile(File inputFile,
                                ZipOutputStream ouputStream) {
        try {
            if (inputFile.exists()) {
                /**如果是目录的话这里是不采取操作的，
                 * 至于目录的打包正在研究中
                 */
                if (inputFile.isFile()) {
                    FileInputStream IN = new FileInputStream(inputFile);
                    BufferedInputStream bins = new BufferedInputStream(IN, 512);
                    //org.apache.tools.zip.ZipEntry
                    ZipEntry entry = new ZipEntry(inputFile.getName());
                    ouputStream.putNextEntry(entry);
                    // 向压缩文件中输出数据
                    int nNumber;
                    byte[] buffer = new byte[512];
                    while ((nNumber = bins.read(buffer)) != -1) {
                        ouputStream.write(buffer, 0, nNumber);
                    }
                    // 关闭创建的流对象
                    bins.close();
                    IN.close();
                } else {
                    try {
                        File[] files = inputFile.listFiles();
                        for (int i = 0; i < files.length; i++) {
                            zipFile(files[i], ouputStream);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static HttpServletResponse download(HttpServletRequest request, HttpServletResponse response, String filepath) {
        try {
            String filePath = filepath;
            //SuponRequestUtil suponRequest=new SuponRequestUtil();
            //把前端传来的数据放到工具类里面加工得到想要的数据JSONObject jsonObject=suponRequest.getparam(request);
            //JSONObject jsonObject=suponRequest.getparam(request);
            // 取得文件名。
            String filename = request.getParameter("filename");
            // path是指欲下载的文件的路径。
            String path = filePath + "/" + filename;

            File file = new File(filePath, filename);
            // 取得文件的后缀名。
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();
            // 清空response
            //response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(new String(filename.getBytes()), "utf8"));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            //response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/vnd.ms-excel");
            // 以流的形式下载文件
            InputStream fis = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            toClient.write(buffer);
            toClient.flush();
            toClient.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return response;
    }

}
