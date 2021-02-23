package com.szmsd.system.api.task;


import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.LoginCacheUtil;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.redis.service.RedisService;
import com.szmsd.system.api.domain.dto.SysLangDTO;
import com.szmsd.system.api.feign.I18nFeignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@EnableScheduling
public class I18nHandler {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private I18nFeignService i18nFeignService;

    @Autowired
    private RedisService redisService;

    public static  Map<String, String> I18N_TEMP_MAP = new ConcurrentHashMap();

    //todo 启用停用标识,后期可以做动态开关
    public  static Integer MULTIPLE_LANGUAGE_SHOW_OPTION = 1;

    public  String TAB_SYS_LANRES_LAST_UPDATE_DATA = "";

    private static final LinkedMultiValueMap<String, String> SYS_LANG_MAP = new LinkedMultiValueMap<>();


    public LinkedMultiValueMap<String, String> getSysLangMap() {
        return SYS_LANG_MAP;
    }

    public  void addSysLangMap(String id, String lang) {
        SYS_LANG_MAP.add(id, lang);
    }

    public static String text(String key) {
        return getLang(key);
    }


    /**
     * 功能描述: 更新多语言定时器 第一次延迟1秒执行，然后在上一次执行完毕时间点10分钟再次执行；
     *
     * @return:
     * @since: 1.0.0
     * @Author:Gao Junwen
     * @Date: 2020-06-12
     */
   /* @Scheduled(initialDelay=1000, fixedDelay=6000*10*10)
    public void reloadTask() {
        try {
            boolean reloadFlag = checkMultipleOption();
            if (reloadFlag) {
                //表最新的修改时间
                R<String> resultR = this.i18nFeignService.getTableUpdateTime("sys_lanres");
                String oldLastDate = resultR.getData();
                if (StringUtils.isEmpty(oldLastDate)) {
                    return;
                }

                if (!TAB_SYS_LANRES_LAST_UPDATE_DATA.equalsIgnoreCase(oldLastDate)) {
                    String currentDateTime = DateUtils.getTime();
                    long dateDiff = DateUtils.dateDiff(TAB_SYS_LANRES_LAST_UPDATE_DATA, currentDateTime, DateUtils.YYYY_MM_DD_HH_MM_SS);
                    int TWO_MINUTES = 120000;
                    if (dateDiff > (long)TWO_MINUTES) {
                        this.reloadI18nMapData();
                        TAB_SYS_LANRES_LAST_UPDATE_DATA = oldLastDate;
                    }
                }
            }
        } catch (Exception var8) {
            var8.printStackTrace();
        }
    }*/

    /**
     * 功能描述: 把多语言数据放入map中
     *
     * @return:
     * @since: 1.0.0
     * @Author:Gao Junwen
     * @Date: 2020-06-16
     */
    public  synchronized void addSysLang(List<SysLangDTO> langList) {
        if (langList != null && langList.size() >= 1) {
            SYS_LANG_MAP.clear();
            Iterator var1 = langList.iterator();

            while(var1.hasNext()) {
                SysLangDTO lang = (SysLangDTO)var1.next();
                if (lang.getStrId() != null && !lang.getStrId().trim().equals("")) {
                    addSysLangMap(lang.getStrId(), lang.getStrId());
                    addSysLangMap(lang.getStrId(), lang.getLang1());
                    addSysLangMap(lang.getStrId(), lang.getLang2());
                    addSysLangMap(lang.getStrId(), lang.getLang3());
                }
            }
        }
    }

    /**
     * 功能描述: 根据key和lang获取对应的语言
     *
     * @return:
     * @since: 1.0.0
     * @Author:Gao Junwen
     * @Date: 2020-06-16
     */
    public static final String getLang(String key) {
        String lang = getCurrentLanguage();
        List langList = SYS_LANG_MAP.get(key);
        String resultLang = "";
        if (langList != null && langList.size() > 0) {
            if (lang.equals("zh_CN")) {
                resultLang = (String)langList.get(0);
            } else if (lang.equals("en_US")) {
                resultLang = (String)langList.get(1);
            } else if (lang.equals("ar_SAU")) {
                resultLang = (String)langList.get(2);
            } else if (lang.equals("zh_CN")) {
                resultLang = (String)langList.get(3);
            } else {
                resultLang = key;
            }
        }

        if (resultLang != null && !resultLang.trim().equals("")) {
            return resultLang;
        } else {
            if (key != null && !key.trim().equalsIgnoreCase("") && checkMultipleOption()) {
                I18N_TEMP_MAP.put(key, key);
            }

            return key;
        }
    }

    /**
     * @TODO 改为redis获取
     * @Title:getCurrentLanguage
     * @Description: 获取语言
     * @author GaoJunWen
     * @date 2020-04-22
     * @param
     * @return
     */
    public static String getCurrentLanguage() {
        String locale="";

        //todo 从redis获取用户信息 获取对应语言信息
        LoginCacheUtil loginUser = null;
        //WebLoginUtil.getLoginUser(redisService, request.getHeader(BaseConstant.HTTP_REQUEST_PARAM_ACCESS_TOKEN));

        if(loginUser!=null)
        {
            locale = loginUser.getLang();
        }
        if(StringUtils.isEmpty(locale))
        {
            locale= Locale.CHINA.toString();
        }
        return locale;
    }

    /**
     * 功能描述: 预留-是否开启多语言
     *
     * @return:
     * @since: 1.0.0
     * @Author:Gao Junwen
     * @Date: 2020-06-18 9:17
     */
    private static boolean checkMultipleOption() {
        boolean flag = false;
        if (MULTIPLE_LANGUAGE_SHOW_OPTION != null && MULTIPLE_LANGUAGE_SHOW_OPTION==1) {
            flag = true;
        }
        return flag;
    }



    //todo 查询多语言表数据,放入map中
    protected void reloadI18nMapData() {
        List langList = null;
        try {
            SysLangDTO sysLangDTO = new SysLangDTO();
            //TODO Gao Junwen
            R<List<SysLangDTO>> listR= this.i18nFeignService.queryAllLang(sysLangDTO);
            langList = listR.getData();
        } catch (Exception e) {
            this.logger.error("查询多语言数据出错",e);
        }
        if (langList != null && langList.size() >= 1) {
            addSysLang(langList);
        }
    }

    public  int getCurrentLangNumber() {
        String lang = getCurrentLanguage();
        int langNumber = 1;
        if (lang != null && !lang.trim().equalsIgnoreCase("")) {
            if (lang.equals("zh_CN")) {
                langNumber = 0;
            } else if (lang.equals("en_US")) {
                langNumber = 1;
            } else if (lang.equals("ar_SAU")) {
                langNumber = 2;
            } else if (lang.equals("zh_CN")) {
                langNumber = 3;
            } else {
                langNumber = 0;
            }
            return langNumber;
        } else {
            return langNumber;
        }
    }
}
