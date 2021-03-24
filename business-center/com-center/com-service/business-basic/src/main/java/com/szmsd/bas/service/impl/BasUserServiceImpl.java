package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.BasUser;
import com.szmsd.bas.dao.BasUserMapper;
import com.szmsd.bas.service.IBasUserService;
import com.szmsd.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ziling
 * @since 2020-08-11
 */
@Service
public class BasUserServiceImpl extends ServiceImpl<BasUserMapper, BasUser> implements IBasUserService {


    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    @Override
    public BasUser selectBasUserById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询模块列表
     *
     * @param BasUser 模块
     * @return 模块
     */
    @Override
    public List<BasUser> selectBasUserList(BasUser basUser) {
        QueryWrapper<BasUser> where = new QueryWrapper<BasUser>();
        if (StringUtils.isNotEmpty(basUser.getPhone())) {
            where.eq("phone", basUser.getPhone());
        }
        if (StringUtils.isNotEmpty(basUser.getEmpName())) {
            where.like("emp_name", basUser.getEmpName());
        }
        if (StringUtils.isNotEmpty(basUser.getUserType())) {
            where.like("user_type", basUser.getUserType());
        }
        if (StringUtils.isNotEmpty(basUser.getCusName())) {
            where.like("cus_name", basUser.getCusName());
        }
        if (StringUtils.isNotEmpty(basUser.getCusCode())) {
            where.eq("cus_code", basUser.getCusCode());
        }
        if (StringUtils.isNotEmpty(basUser.getNickName())) {
            where.eq("nick_name", basUser.getNickName());
        }
        if (StringUtils.isNotEmpty(basUser.getPassword())) {
            where.eq("password", basUser.getPassword());
        }
        if (StringUtils.isNotEmpty(basUser.getUserCode())) {
            where.eq("user_code", basUser.getUserCode());
        }
        if (StringUtils.isNotEmpty(basUser.getName())) {
            where.like("name", basUser.getName());
        }
        if (StringUtils.isNotEmpty(basUser.getSiteCode())) {
            where.eq("site_code", basUser.getSiteCode());
        }
        if (StringUtils.isNotEmpty(basUser.getSiteName())) {
            where.like("site_name", basUser.getSiteName());
        }
        if (basUser.getUserId()!=0){
            where.eq("user_id",basUser.getUserId());
        }
        if (basUser.getId()!=0){
            where.eq("id",basUser.getId());
        }
        if (StringUtils.isNotEmpty(basUser.getCreateBy())){
            where.eq("create_by",basUser.getCreateBy());
        }
//        if (StringUtils.isNotEmpty(basUser.getPageSize()) && StringUtils.isNotEmpty(basUser.getPageNum())){
//            where.
//        }
        where.orderByDesc("create_time");
        return baseMapper.selectList(where);
    }

    /**
     * 新增模块
     *
     * @param BasUser 模块
     * @return 结果
     */
    @Override
    public int insertBasUser(BasUser basUser) {
        return baseMapper.insert(basUser);
    }

    /**
     * 修改模块
     *
     * @param BasUser 模块
     * @return 结果
     */
    @Override
    public int updateBasUser(BasUser basUser) {
        return baseMapper.updateById(basUser);
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    @Override
    public int deleteBasUserByIds(List<Integer> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    @Override
    public int deleteBasUserById(String id) {
        return baseMapper.deleteById(id);
    }
}
