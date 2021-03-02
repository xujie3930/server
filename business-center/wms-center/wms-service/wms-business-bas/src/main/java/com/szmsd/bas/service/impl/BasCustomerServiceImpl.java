package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasCustomerMapper;
import com.szmsd.bas.domain.BasCustomer;
import com.szmsd.bas.service.IBasCustomerService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-06-19
 */
@Service
public class BasCustomerServiceImpl extends ServiceImpl<BasCustomerMapper, BasCustomer> implements IBasCustomerService {


    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    @Override
    public BasCustomer selectBasCustomerById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询模块列表
     *
     * @param BasCustomer 模块
     * @return 模块
     */
    @Override
    public List<BasCustomer> selectBasCustomerList(BasCustomer basCustomer) {
        String idList = basCustomer.getId();
        QueryWrapper<BasCustomer> where = new QueryWrapper<BasCustomer>();
        if (StringUtils.isNotEmpty(basCustomer.getCusCode())) {
            where.eq("cus_code", basCustomer.getCusCode());
        }
        if (StringUtils.isNotEmpty(basCustomer.getCusName())){
            where.like("cus_name",basCustomer.getCusName());
        }
        if (StringUtils.isNotEmpty(basCustomer.getStartTime() ) && StringUtils.isNotEmpty(basCustomer.getEndTime())) {
            where.between("create_time", basCustomer.getStartTime(), basCustomer.getEndTime());
        }
        if (StringUtils.isNotEmpty(basCustomer.getSiteCode())){
            where.eq("site_code",basCustomer.getSiteCode());
        }
        if (StringUtils.isNotEmpty(basCustomer.getSiteName())){
            where.eq("site_name",basCustomer.getSiteName());
        }
        if (StringUtils.isNotEmpty(basCustomer.getCusAbbverviation())){
            where.like("Cus_abbverviation",basCustomer.getCusAbbverviation());
        }
        if (!StringUtils.isEmpty(basCustomer.getId())) {
            if (!StringUtils.isEmpty(basCustomer.getId())) {
                List<String> strings = new ArrayList<>();
                if (idList.contains(",")) {
                    String[] split = idList.split(",");
                    strings = Arrays.asList(split);
                } else {
                    strings.add(idList);
                }

                where.in("id", strings);
            }
        }
        if(!CollectionUtils.isEmpty(basCustomer.getCusCodeList())){
            where.in("cus_code",basCustomer.getCusCodeList());
        }
        where.orderByDesc("create_time");
        return baseMapper.selectList(where);
    }

    /**
     * 新增模块
     *
     * @param BasCustomer 模块
     * @return 结果
     */
    @Override
    public int insertBasCustomer(BasCustomer basCustomer) {
        return baseMapper.insert(basCustomer);
    }

    /**
     * 修改模块
     *
     * @param BasCustomer 模块
     * @return 结果
     */
    @Override
    public int updateBasCustomer(BasCustomer basCustomer) {
        return baseMapper.updateById(basCustomer);
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    @Override
    public int deleteBasCustomerByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    @Override
    public int deleteBasCustomerById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public R importExcel(MultipartFile file) throws Exception {
        ExcelUtil<BasCustomer> util = new ExcelUtil<>(BasCustomer.class);
        List<BasCustomer> list = util.importExcel(file.getInputStream());
        return R.ok(list);
    }
}
