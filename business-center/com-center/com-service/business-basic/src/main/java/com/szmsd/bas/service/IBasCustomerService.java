package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.api.domain.BasCustomer;
import com.szmsd.common.core.domain.R;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ziling
 * @since 2020-06-19
 */
public interface IBasCustomerService extends IService<BasCustomer> {

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    public BasCustomer selectBasCustomerById(String id);

    /**
     * 查询模块列表
     *
     * @param BasCustomer 模块
     * @return 模块集合
     */
    public List<BasCustomer> selectBasCustomerList(BasCustomer basCustomer);

    /**
     * 新增模块
     *
     * @param BasCustomer 模块
     * @return 结果
     */
    public int insertBasCustomer(BasCustomer basCustomer);

    /**
     * 修改模块
     *
     * @param BasCustomer 模块
     * @return 结果
     */
    public int updateBasCustomer(BasCustomer basCustomer);

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    public int deleteBasCustomerByIds(List<String> ids);

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    public int deleteBasCustomerById(String id);


    /**
     * 导入excel解析数据
     *
     * @param file
     * @return
     */
    R importExcel(MultipartFile file) throws Exception;
}
