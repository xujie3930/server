package com.szmsd.system.service;


import com.szmsd.system.api.domain.SysLang;
import com.szmsd.system.api.domain.dto.SysLangDTO;

import java.util.List;

/**
 *
 * @ClassName:I18nService
 * @Description:多语言
 * @author GaoJunWen
 * @date 2020-04-22
 * @version V1.0
 */

public interface I18nService {


//    /**
//     *初始化多语言
//     */
//    void initLang();

    /**
     * 功能描述: 查询多语言表列表
     *
     * @return:
     * @since: 1.0.0
     * @Author:Gao Junwen
     * @Date: 2020-06-15 13:49
     */
    List<SysLang> getTableData(SysLangDTO sysLangDTO);

    /**
     * 功能描述: 获取某张表修改或者新增数据最新的时间
     *
     * @return:
     * @since: 1.0.0
     * @Author:Gao Junwen
     * @Date: 2020-06-12 20:02
     */
    String getTableUpdateTime(String tableName);

//    /**
//     * 功能描述: 查询多语言数据放入map中
//     *
//     * @return:
//     * @since: 1.0.0
//     * @Author:Gao Junwen
//     * @Date: 2020-06-17 10:59
//     */
//    void reloadI18nMapData();
}
