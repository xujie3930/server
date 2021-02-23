/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: I18nService
 * Author:   6
 * Date:     2020-06-15 10:04
 * Description: 多语言
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.szmsd.system.api.feign;

import com.szmsd.common.core.constant.ServiceNameConstants;
import com.szmsd.common.core.domain.R;
import com.szmsd.system.api.domain.dto.SysLangDTO;
import com.szmsd.system.api.factory.I18nFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 多语言
 * @author Gao JunWen
 * @create 2020-06-15
 * @since 1.0.0
 */
@FeignClient(contextId = "i18nService", value = ServiceNameConstants.SYSTEM_SERVICE,fallbackFactory = I18nFallbackFactory.class)
public interface I18nFeignService {

    @RequestMapping(value = "/i18n/listAll", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    R<List<SysLangDTO>> queryAllLang(@RequestBody SysLangDTO sysLangDTO);

    @RequestMapping(value = "/i18n/getTableUpdateTime", method = RequestMethod.POST)
    R<String> getTableUpdateTime(@RequestParam(value = "tableName") String tableName);
}
