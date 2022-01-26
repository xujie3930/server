package com.szmsd.bas.keyword;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 关键词过滤工具类
 */
public class KeywordsUtil {

    /**
     * redis 缓存key
     */
    public static final String KEYWORD_KEY = "CARRIER:KEYWORDS:";

    /**
     * 关键词库
     */
    public static Map keywordsMap = null;

    /**
     * 只过滤最小关键词
     */
    public static int minMatchType = 1;

    /**
     * 过滤所有关键词
     */
    public static int maxMatchType = 2;

    /**
     * 关键词库关键词数量
     *
     * @return
     */
    public int getWordSize() {
        if (KeywordsUtil.keywordsMap == null) {
            return 0;
        }
        return KeywordsUtil.keywordsMap.size();
    }

    /**
     * 是否包含关键词
     *
     * @param txt
     * @param matchType
     * @return
     */
    public static boolean isContaintKeywords(String txt, int matchType) {
        boolean flag = false;
        for (int i = 0; i < txt.length(); i++) {
            int matchFlag = checkKeywords(txt, i, matchType);
            if (matchFlag > 0) {
                flag = true;
            }
        }
        return flag;
    }

    public static boolean isContaintKeywords(String txt) {
        return isContaintKeywords(txt,  maxMatchType);
    }


    /**
     * 获取关键词内容
     *
     * @param txt
     * @param matchType
     * @return 关键词内容
     */
    public static Set<String> getKeywords(String txt, int matchType) {
        Set<String> keywordsList = new HashSet<String>();

        for (int i = 0; i < txt.length(); i++) {
            int length = checkKeywords(txt, i, matchType);
            if (length > 0) {
                // 将检测出的关键词保存到集合中
                keywordsList.add(txt.substring(i, i + length));
                i = i + length - 1;
            }
        }

        return keywordsList;
    }

    /**
     * 替换关键词
     *
     * @param txt
     * @param matchType
     * @param replaceChar
     * @return
     */
    public static String replaceKeywords(String txt, int matchType, String replaceChar) {
        String resultTxt = txt;
        Set<String> set = getKeywords(txt, matchType);
        Iterator<String> iterator = set.iterator();
        String word = null;
        String replaceString = null;
        while (iterator.hasNext()) {
            word = iterator.next();
            replaceString = getReplaceChars(replaceChar, word.length());
            resultTxt = resultTxt.replaceAll(word, replaceString);
        }

        return resultTxt;
    }

    /**
     * 替换关键词内容
     *
     * @param replaceChar
     * @param length
     * @return
     */
    private static String getReplaceChars(String replaceChar, int length) {
        String resultReplace = replaceChar;
        for (int i = 1; i < length; i++) {
            resultReplace += replaceChar;
        }

        return resultReplace;
    }

    /**
     * 检查关键词数量
     *
     * @param txt
     * @param beginIndex
     * @param matchType
     * @return
     */
    public static int checkKeywords(String txt, int beginIndex, int matchType) {
        boolean flag = false;
        // 记录关键词数量
        int matchFlag = 0;
        char word = 0;
        Map nowMap = KeywordsUtil.keywordsMap;
        for (int i = beginIndex; i < txt.length(); i++) {
            word = txt.charAt(i);
            // 判断该字是否存在于关键词库中
            nowMap = (Map) nowMap.get(word);
            if (nowMap != null) {
                matchFlag++;
                // 判断是否是关键词的结尾字，如果是结尾字则判断是否继续检测
                if ("1".equals(nowMap.get("isEnd"))) {
                    flag = true;
                    // 判断过滤类型，如果是小过滤则跳出循环，否则继续循环
                    if (KeywordsUtil.minMatchType == matchType) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
        if (!flag) {
            matchFlag = 0;
        }
        return matchFlag;
    }

}
