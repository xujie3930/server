package com.szmsd.system.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.common.core.domain.R;
import com.szmsd.system.domain.SysLanres;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 多语言配置表 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-08-06
 */
public interface ISysLanresService extends IService<SysLanres> {

    /**
     * 查询多语言配置表模块
     *
     * @param id 多语言配置表模块ID
     * @return 多语言配置表模块
     */
    public SysLanres selectSysLanresById(String id);

    /**
     * 查询多语言配置表模块列表
     *
     * @param SysLanres 多语言配置表模块
     * @return 多语言配置表模块集合
     */
    public List<SysLanres> selectSysLanresList(SysLanres sysLanres);

    /**
     * 查询多语言配置表模块列表
     *
     * @param SysLanres 多语言配置表模块
     * @return 多语言配置表模块集合
     */
    public List<SysLanres> selectSysLanres(SysLanres sysLanres);


    /**
     * 查询多语言配置表模块列表
     *
     * @param SysLanres 多语言配置表模块
     * @return 多语言配置表模块集合
     */
    public List<SysLanres> selectLanres(SysLanres sysLanres);

    /**
     * 新增多语言配置表模块
     *
     * @param SysLanres 多语言配置表模块
     * @return 结果
     */
    public int insertSysLanres(SysLanres sysLanres);

    /**
     * 修改多语言配置表模块
     *
     * @param SysLanres 多语言配置表模块
     * @return 结果
     */
    public int updateSysLanres(SysLanres sysLanres);

    /**
     * 批量删除多语言配置表模块
     *
     * @param ids 需要删除的多语言配置表模块ID
     * @return 结果
     */
    public int deleteSysLanresByIds(List<String> ids);

    /**
     * 删除多语言配置表模块信息
     *
     * @param id 多语言配置表模块ID
     * @return 结果
     */
    public int deleteSysLanresById(String id);


    /**
     * 导入excel解析数据
     *
     * @param file
     * @return
     */
    R importExcel(MultipartFile file) throws Exception;
}
