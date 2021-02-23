package com.szmsd.system.mapper;


import com.szmsd.system.api.domain.SysLang;
import com.szmsd.system.api.domain.dto.SysLangDTO;

import java.util.List;

/**
 *
 * @ClassName:I18nDao
 * @Description:多语言
 * @author GaoJunWen
 * @date 2020-04-22
 * @version V1.0
 */
public interface I18nMapper{

    /**
     * 功能描述: 查询多语言表列表
     *
     * @return:
     * @since: 1.0.0
     * @Author:Gao Junwen
     * @Date: 2020-06-16 11:20
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
}
