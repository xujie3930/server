package com.szmsd.delivery;

import com.alibaba.fastjson.JSONArray;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

public class GTTest {

    private static final String PATH="https://translate.googleapis.com/translate_a/single"; //地址
    private static final String CLIENT="gtx";

    private static final String USER_AGENT="Mozilla/5.0";//"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36";

    private static final Map<String,String> LANGUAGE_MAP = new HashMap();

    private static GTTest _instance = null;

    /**
     * 获取单例
     * @return
     */
    public static GTTest getInstance() {
        if( null == _instance){
            _instance = new GTTest();
            _instance.init();
        }
        return _instance;
    }

    /**
     * 初始化语言类
     */
    private void init(){

        LANGUAGE_MAP.put("zh_cn","Chinese Simplified");
        LANGUAGE_MAP.put("zh_tw","Chinese Traditional");
        LANGUAGE_MAP.put("en","English");

    }

    /**
     * 判断语言是否支持
     * @param language
     * @return
     */
    public boolean isSupport(String language){
        if( null == LANGUAGE_MAP.get( language )){
            return false;
        }
        return true;
    }

    /** 获取 语言代码
     * ISO 639-1 code
     * @param desiredLang 语言
     * @return 如果返回null则标示不支持
     */
    public String getCode(String desiredLang){
        if( null != LANGUAGE_MAP.get(desiredLang)){
            return desiredLang;
        }
        String tmp = desiredLang.toLowerCase();
        for(Map.Entry<String, String> enter : LANGUAGE_MAP.entrySet() ){
            if( enter.getValue().equals( tmp)){
                return enter.getKey();
            }
        }

        return null;
    }


    /**
     * 翻译文本
     * @param text  文本内容
     * @param sourceLang  文本所属语言。如果不知道，可以使用auto
     * @param targetLang  目标语言。必须是明确的有效的目标语言
     * @return
     * @throws Exception
     */
    public String translateText(String text,String sourceLang, String targetLang) throws Exception{


        String retStr="";
        if( !( isSupport(sourceLang) || isSupport(targetLang) ) ){
            throw new Exception("不支持的语言类型");
        }

        List<NameValuePair> nvps = new ArrayList();
        nvps.add(new BasicNameValuePair("client", CLIENT));
        nvps.add(new BasicNameValuePair("sl", sourceLang));
        nvps.add(new BasicNameValuePair("tl", targetLang));
        nvps.add(new BasicNameValuePair("dt", "t"));
        nvps.add(new BasicNameValuePair("q", text));
//        String finalPath=PATH +"?client="+CLIENT+"&sl="+sourceLang+"&tl="+targetLang+"&dt=t&q="+ text ;

        String resp =  postHttp( PATH,nvps);
        if( null == resp ){
            throw  new Exception("网络异常");
        }

//        System.out.println( "==>返回内容：" + resp);

        JSONArray jsonObject = JSONArray.parseArray( resp );
        for (Iterator<Object> it = jsonObject.getJSONArray(0).iterator(); it.hasNext(); ) {
            JSONArray a = (JSONArray) it.next();
            retStr += a.getString(0);
        }

        return retStr;
    }


    /**
     * post 请求
     * @param url 请求地址
     * @param nvps 参数列表
     * @return
     * @throws
     */
    private String postHttp( String url ,List<NameValuePair> nvps){
        String responseStr = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost( url );
        //重要！！必须设置 http 头，否则返回为乱码
        httpPost.setHeader("User-Agent",USER_AGENT);
        CloseableHttpResponse response2 = null;
        try {
            // 重要！！ 指定编码，对中文进行编码
            httpPost.setEntity( new UrlEncodedFormEntity(nvps, Charset.forName("UTF-8"))  );
            response2 = httpclient.execute(httpPost);
            HttpEntity entity2 = response2.getEntity();
            responseStr = EntityUtils.toString(entity2);
            EntityUtils.consume(entity2);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != response2) {
                try {
                    response2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if( null != httpclient ){
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return responseStr;
    }



    @Test
    public void testBringVerifyEnum() throws Exception {

        System.out.println("-----test-----");
        String txt="";
        GTTest g = GTTest.getInstance();
        System.out.println("译文："+g.translateText(txt,"auto","en"));
        //System.out.println("译文："+g.translateText("谁能说支持不支持","auto","zh_tw"));


    }

}
