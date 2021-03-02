package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasVercode;

import java.util.List;

/**
 * <p>
 * 短信发送表 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-09-10
 */
public interface IBasVercodeService extends IService<BasVercode> {

    /**
     * 查询短信发送表模块
     *
     * @param id 短信发送表模块ID
     * @return 短信发送表模块
     */
    public BasVercode selectBasVercodeById(String id);

    /**
     * 查询短信发送表模块列表
     *
     * @param BasVercode 短信发送表模块
     * @return 短信发送表模块集合
     */
    public List<BasVercode> selectBasVercodeList(BasVercode basVercode);

    /**
     * 新增短信发送表模块
     *
     * @param BasVercode 短信发送表模块
     * @return 结果
     */
    public int insertBasVercode(BasVercode basVercode);

    /**
     * 修改短信发送表模块
     *
     * @param BasVercode 短信发送表模块
     * @return 结果
     */
    public int updateBasVercode(BasVercode basVercode);

    /**
     * 批量删除短信发送表模块
     *
     * @param ids 需要删除的短信发送表模块ID
     * @return 结果
     */
    public int deleteBasVercodeByIds(List
                                             <String> ids);

    /**
     * 删除短信发送表模块信息
     *
     * @param id 短信发送表模块ID
     * @return 结果
     */
    public int deleteBasVercodeById(String id);
}
