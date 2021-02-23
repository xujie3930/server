package com.szmsd.system.service.impl;


import com.szmsd.system.api.domain.SysLang;
import com.szmsd.system.api.domain.dto.SysLangDTO;
import com.szmsd.system.config.I18nConfig;
import com.szmsd.system.mapper.I18nMapper;
import com.szmsd.system.service.I18nService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @ClassName:I18nServiceImpl
 * @Description:多语言
 * @author GaoJunWen
 * @date 2020-04-22
 * @version V1.0
 */
@Service
public class I18nServiceImpl  implements I18nService {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    I18nMapper i18nMapper;

//    @Autowired
//    SysI18nHandler sysI18nHandler;

    @Autowired
    I18nConfig i18nConfig;

    // public int insertTabSysLanres(SysLang sysLang) throws BusinessException {
    //     if (sysLang == null) {
    //         throw new BusinessException("参数：插入对象为null值");
    //     } else {
    //         try {
    //             I18nHandler.addSysLangMap(sysLang.getStrId(), sysLang.getStrId());
    //             I18nHandler.addSysLangMap(sysLang.getStrId(), sysLang.getLang1());
    //             I18nHandler.addSysLangMap(sysLang.getStrId(), sysLang.getLang2());
    //             I18nHandler.addSysLangMap(sysLang.getStrId(), sysLang.getLang3());
    //             I18nHandler.I18N_TEMP_MAP.remove(sysLang.getStrId());
    //             int count = this.i18nDao.save(sysLang);
    //             return count;
    //         } catch (Exception var3) {
    //             this.logger.error(var3.getMessage());
    //             if (var3.getMessage() != null && var3.getMessage().contains("ORA-00001")) {
    //                 throw new BusinessException("保存错误，数据已存在", var3);
    //             } else {
    //                 throw new BusinessException("插入数据到数据库异常", var3);
    //             }
    //         }
    //     }
    // }
    //
    // public int updateTabSysLanres(SysLang sysLang) throws BusinessException {
    //     if (sysLang == null) {
    //         throw new BusinessException("参数：修改对象为null值");
    //     } else {
    //         try {
    //             int count = this.i18nDao.update(sysLang);
    //             I18nHandler.I18N_TEMP_MAP.remove(sysLang.getStrId());
    //             return count;
    //         } catch (Exception var3) {
    //             this.logger.error(var3.getMessage());
    //             throw new BusinessException("修改数据到数据库异常", var3);
    //         }
    //     }
    // }
    //
    // public int delTabSysLanres(String strid) throws BusinessException {
    //     if (StringUtils.isEmpty(strid)) {
    //         throw new BusinessException("参数：编号不能为空");
    //     } else {
    //         List<String> ids = Arrays.asList(strid.split("\\|"));
    //         if (ids.isEmpty()) {
    //             throw new BusinessException("参数：编号必须以|为分隔符");
    //         } else {
    //             try {
    //                 Iterator var3 = ids.iterator();
    //
    //                 while(var3.hasNext()) {
    //                     String str = (String)var3.next();
    //                     this.i18nDao.delete(str);
    //                 }
    //
    //                 return 1;
    //             } catch (Exception e) {
    //                 this.logger.error(e.getMessage());
    //                 throw new BusinessException(BaseErrorEnum.COMMON_ERROR.getErrorCode(), e.getMessage());
    //             }
    //         }
    //     }
    // }
    //
    // public int insertTabSysLanresList(List<SysLang> sysLangList) throws BusinessException {
    //     try {
    //         int count = 0;
    //         LinkedMultiValueMap<String, String> sysLangMap = I18nHandler.getSysLangMap();
    //         Iterator var4 = sysLangList.iterator();
    //
    //         while(true) {
    //             while(var4.hasNext()) {
    //                 SysLang sysLang = (SysLang)var4.next();
    //                 List<String> langList = sysLangMap.get(sysLang.getStrId());
    //                 if (langList != null && langList.size() > 0) {
    //                     this.updateTabSysLanres(sysLang);
    //                     ++count;
    //                 } else {
    //                     this.insertTabSysLanres(sysLang);
    //                     ++count;
    //                 }
    //             }
    //
    //             return count;
    //         }
    //     } catch (Exception e) {
    //         this.logger.error(e.getMessage());
    //         throw new BusinessException(BaseErrorEnum.COMMON_ERROR.getErrorCode(), e.getMessage());
    //     }
    // }
    //
    // public ResultJson getTableData(Pagination pagination, Map<String, Object> params) throws BusinessException {
    //     try {
    //         this.i18nDao.getTableData(pagination, params);
    //         return new ResultJson(pagination);
    //     } catch (Exception e) {
    //         this.logger.error("获取分页数据异常, 参数：" + params.toString(), var4);
    //         throw new BusinessException(BaseErrorEnum.COMMON_ERROR.getErrorCode(), e.getMessage());
    //     }
    // }

//    private boolean checkMultipleOption() {
//        boolean flag = false;
//         if (sysI18nHandler.MULTIPLE_LANGUAGE_SHOW_OPTION!=null && sysI18nHandler.MULTIPLE_LANGUAGE_SHOW_OPTION==1) {
//             flag = true;
//         }
//        return flag;
//    }


    /**
     * 功能描述: 启用初始化多语言数据
     *
     * @return:
     * @since: 1.0.0
     * @Author:Gao Junwen
     * @Date: 2020-06-17 10:57
     */
//    @Override
//    public void initLang() {
//        boolean initFlag = this.checkMultipleOption();
//        if (initFlag) {
//            this.logger.debug("----初值化多语言数据----");
//            this.reloadI18nMapData();
//            String currentDateTime = DateUtils.getTime();
//            SysI18nHandler.TAB_SYS_LANRES_LAST_UPDATE_DATA = currentDateTime;
//        }
//    }

    @Override
    public List<SysLang> getTableData(SysLangDTO sysLangDTO) {
        return this.i18nMapper.getTableData(sysLangDTO);
    }

    @Override
    public String getTableUpdateTime(String tableName) {
        return this.i18nMapper.getTableUpdateTime(tableName);
    }

    /**
     * 功能描述: 查询多语言数据放入map中
     *
     * @return:
     * @since: 1.0.0
     * @Author:Gao Junwen
     * @Date: 2020-06-16 10:56
     */
//    @Override
//    public void reloadI18nMapData() {
//        List langList = null;
//        try {
//            SysLangDTO sysLangDTO = new SysLangDTO();
//
//            langList = this.i18nMapper.getTableData(sysLangDTO);
//        } catch (Exception e) {
//            this.logger.error("查询多语言数据出错",e);
//        }
//        if (langList != null && langList.size() >= 1) {
//            sysI18nHandler.addSysLang(langList);
//        }
//    }
}
