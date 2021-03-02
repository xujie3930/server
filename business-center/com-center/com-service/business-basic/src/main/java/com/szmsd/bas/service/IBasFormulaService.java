package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasFormula;

import java.util.List;

/**
* <p>
    * 公式表 服务类
    * </p>
*
* @author ziling
* @since 2020-07-08
*/
public interface IBasFormulaService extends IService<BasFormula> {

/**
* 查询公式表模块
*
* @param id 公式表模块ID
* @return 公式表模块
*/
public BasFormula selectBasFormulaById(String id);

/**
* 查询公式表模块列表
*
* @param BasFormula 公式表模块
* @return 公式表模块集合
*/
public List<BasFormula> selectBasFormulaList(BasFormula basFormula);

/**
* 新增公式表模块
*
* @param BasFormula 公式表模块
* @return 结果
*/
public int insertBasFormula(BasFormula basFormula);

/**
* 修改公式表模块
*
* @param BasFormula 公式表模块
* @return 结果
*/
public int updateBasFormula(BasFormula basFormula);

/**
* 批量删除公式表模块
*
* @param ids 需要删除的公式表模块ID
* @return 结果
*/
public int deleteBasFormulaByIds(List
<String> ids);

    /**
    * 删除公式表模块信息
    *
    * @param id 公式表模块ID
    * @return 结果
    */
    public int deleteBasFormulaById(String id);
    }
