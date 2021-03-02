package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasEmployeesMapper;
import com.szmsd.bas.domain.BasEmployees;
import com.szmsd.bas.service.IBasEmployeesService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringToolkit;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
public class BasEmployeesServiceImpl extends ServiceImpl<BasEmployeesMapper, BasEmployees> implements IBasEmployeesService {


    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    @Override
    public BasEmployees selectBasEmployeesById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询模块列表
     *
     * @param basEmployees 模块
     * @return 模块
     */
    @Override
    public List<BasEmployees> selectBasEmployeesList(BasEmployees basEmployees) {
        String idList = basEmployees.getId();
        QueryWrapper<BasEmployees> where = new QueryWrapper<BasEmployees>();
        if (StringUtils.isNotEmpty(basEmployees.getEmpState())) {
            where.eq("emp_state", basEmployees.getEmpState());
        }
        if (StringUtils.isNotEmpty(basEmployees.getEmpCode())) {
            where.eq("emp_code", basEmployees.getEmpCode());

        }
        if (StringUtils.isNotEmpty(basEmployees.getEmpName())){
            where.like("emp_name",basEmployees.getEmpName());
        }
        if (!CollectionUtils.isEmpty(basEmployees.getEmpNameList())) {
            where.in("emp_name", basEmployees.getEmpNameList());
        }
        if (!CollectionUtils.isEmpty(basEmployees.getEmpCodeList())) {
            where.in("emp_code", basEmployees.getEmpCodeList());
        }
        if (StringUtils.isNotEmpty(basEmployees.getSiteName())){
            where.like("site_name",basEmployees.getSiteName());
        }
        if (StringUtils.isNotEmpty(basEmployees.getSiteCode())){
            where.eq("site_code",basEmployees.getSiteCode());
        }
        if (StringUtils.isNotEmpty(basEmployees.getEmpPhone())){
            where.eq("emp_phone",basEmployees.getEmpPhone());
        }
        if (StringUtils.isNotEmpty(basEmployees.getPassword())){
            where.eq("password",basEmployees.getPassword());
        }
        if (StringUtils.isNotEmpty(basEmployees.getEmpTel())){
            where.eq("emp_tel",basEmployees.getEmpTel());
        }
        if (StringUtils.isNotEmpty(basEmployees.getSiteCodeList())){
            where.in("site_code", StringToolkit.getCodeByArray(basEmployees.getSiteCodeList()));
        }
        if (StringUtils.isNotEmpty(basEmployees.getStartTime())&& StringUtils.isNotEmpty(basEmployees.getEndTime() )) {
            where.between("create_time", basEmployees.getStartTime(), basEmployees.getEndTime());
        }
        if (!StringUtils.isEmpty(basEmployees.getId())) {
            if (!StringUtils.isEmpty(basEmployees.getId())) {
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
        where.orderByDesc("create_time");
        return baseMapper.selectList(where);
    }

    /**
     * 新增模块
     *
     * @param basEmployees 模块
     * @return 结果
     */
    @Override
    public int insertBasEmployees(BasEmployees basEmployees) {
        return baseMapper.insert(basEmployees);
    }

    /**
     * 修改模块
     *
     * @param basEmployees 模块
     * @return 结果
     */
    @Override
    public int updateBasEmployees(BasEmployees basEmployees) {
        return baseMapper.updateById(basEmployees);
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    @Override
    public int deleteBasEmployeesByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    @Override
    public int deleteBasEmployeesById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public R<BasEmployees> getEmpByCode(BasEmployees basEmployees) {
        QueryWrapper<BasEmployees> wrapper = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(basEmployees.getEmpCode())){
            wrapper.eq("emp_code",basEmployees.getEmpCode());
        }
        BasEmployees basEmp = this.baseMapper.selectOne(wrapper);
        if(basEmployees !=null){
            return R.ok(basEmp);
        }
        return  null;
    }
}
