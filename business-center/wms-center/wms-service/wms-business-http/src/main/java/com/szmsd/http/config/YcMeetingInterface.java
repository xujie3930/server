package com.szmsd.http.config;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import cn.hutool.json.JSONObject;
import cn.hutool.json.XML;
import com.alibaba.fastjson.JSON;
import com.szmsd.http.domain.YcAppParameter;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

public class YcMeetingInterface {


    public Map YcApi(YcAppParameter ycAppParameter) {

//        String url = "http://pgl.yunwms.com/default/svc/web-service";
        YcMeetingInterface tmi = new YcMeetingInterface();
//        tmi.post(url,"listSummaryMeeting.xml");
       Map map= tmi.post(ycAppParameter.getYcUrl(),getXmlInfo(ycAppParameter));

        /*//判断当前系统是否支持Java AWT Desktop扩展
        if(java.awt.Desktop.isDesktopSupported()){
            try {
                URI path = tmi.getClass().getResource("/listSummaryMeeting.xml").toURI();
                System.out.println(path);
                //创建一个URI实例
//              java.net.URI uri = java.net.URI.create(path);
                //获取当前系统桌面扩展
                java.awt.Desktop dp = java.awt.Desktop.getDesktop();
                //判断系统桌面是否支持要执行的功能
                if(dp.isSupported(java.awt.Desktop.Action.BROWSE)){
                    //获取系统默认浏览器打开链接
                    dp.browse(path);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        return  map;
    }

    private  String getXmlInfo(YcAppParameter ycAppParameter) {

//        String apptoken="535c9208072438fadfb479f1e488ee17";
//        String appKey="1170fb271c9f5f4ffdd5bf4dbfddd76e";
//        String service="getShippingMethod";
//        JSONObject jsonObject=new JSONObject();

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://www.example.org/Ec/\">");
        sb.append("<SOAP-ENV:Body>");
        sb.append("<ns1:callService>");
        sb.append("<paramsJson>"+ycAppParameter.getJsonObject()+"</paramsJson>");
        sb.append("<appToken>"+ycAppParameter.getAppToken()+"</appToken>");
        sb.append("<appKey>"+ycAppParameter.getAppKey()+"</appKey>");
        sb.append("<service>"+ycAppParameter.getService()+"</service>");
        sb.append("</ns1:callService>");
        sb.append("</SOAP-ENV:Body>");
        sb.append("</SOAP-ENV:Envelope>");

        return sb.toString();
    }



    /**
     * 发送xml数据请求到server端
     * @param url xml请求数据地址
     * @param xmlString 发送的xml数据流
     * @return null发送失败，否则返回响应内容
     */
    public Map  post(String url,String xmlFileName){
        //关闭
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "stdout");

        //创建httpclient工具对象
        HttpClient client = new HttpClient();
        //创建post请求方法
        PostMethod myPost = new PostMethod(url);
        //设置请求超时时间
        client.setConnectionTimeout(300*1000);
        String responseString = null;
        try{
            //设置请求头部类型
            myPost.setRequestHeader("Content-Type","text/xml");
            myPost.setRequestHeader("charset","utf-8");

            //设置请求体，即xml文本内容，注：这里写了两种方式，一种是直接获取xml内容字符串，一种是读取xml文件以流的形式
            myPost.setRequestBody(xmlFileName);
            myPost.setRequestEntity(new StringRequestEntity(xmlFileName,"text/xml","utf-8"));

            //读取xml文件流
//            InputStream body=this.getClass().getResourceAsStream("/"+xmlFileName);
//            myPost.setRequestBody(body);
            int statusCode = client.executeMethod(myPost);
            if(statusCode == HttpStatus.SC_OK){
                BufferedInputStream bis = new BufferedInputStream(myPost.getResponseBodyAsStream());
                byte[] bytes = new byte[1024];
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int count = 0;
                while((count = bis.read(bytes))!= -1){
                    bos.write(bytes, 0, count);
                }
                byte[] strByte = bos.toByteArray();
                responseString = new String(strByte,0,strByte.length,"utf-8");
                bos.close();
                bis.close();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        myPost.releaseConnection();

        return  response(responseString);
    }

    public Map response(String sb){

        JSONObject jsonXML = XML.toJSONObject(sb);
        Map map= (Map) jsonXML.get("SOAP-ENV:Envelope");
        Map map1= (Map) map.get("SOAP-ENV:Body");
        Map map2= (Map) map1.get("ns1:callServiceResponse");
        String  response=  String.valueOf(map2.get("response"));
        Map map3 = JSON.parseObject(response, Map.class);
//        List<Map> list= (List<Map>) map3.get("data");
//        System.out.println(list);
        return map3;
    }

}
