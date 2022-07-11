package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.domain.BasFba;
import com.szmsd.bas.dto.BasFbaDTO;
import com.szmsd.bas.mapper.BasFbaMapper;
import com.szmsd.bas.service.IBasFbaService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.security.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BasFbaServiceImpl extends ServiceImpl<BasFbaMapper, BasFba> implements IBasFbaService {

    /**
     * 新增和删除
     * @param basFba
     * @return
     */
    @Override
    public R insertBasFba(BasFba basFba) {

        try {
            //代表是新增
           if (basFba.getId()==null){
               basFba.setCreateTime(new Date());
               basFba.setCreateBy(SecurityUtils.getUsername());
               basFba.setCreateByName(SecurityUtils.getLoginUser().getUsername());
               baseMapper.insertSelective(basFba);
           }
           //修改
           if (basFba.getId()!=null){
               basFba.setUpdateTime(new Date());
               basFba.setUpdateBy(SecurityUtils.getUsername());
               basFba.setUpdateByName(SecurityUtils.getLoginUser().getUsername());
               baseMapper.updateByPrimaryKeySelective(basFba);
           }
            return R.ok("操作成功");
        }catch (Exception e){
            e.printStackTrace();
            return R.failed("操作失败");
        }

    }


    /**
     * 查询
     * @param basFbaDTO
     * @return
     */
    @Override
    public R<List<BasFba>> selectBasFba(BasFbaDTO basFbaDTO) {
        try {
            if (StringUtils.isNotEmpty(basFbaDTO.getFbaCode())){
                basFbaDTO.setFbaCode(new StringBuilder("%").append(basFbaDTO.getFbaCode()).append("%").toString());
            }
            if (StringUtils.isNotEmpty(basFbaDTO.getPostcode())){
                basFbaDTO.setPostcode(new StringBuilder("%").append(basFbaDTO.getPostcode()).append("%").toString());
            }
        return   R.ok(baseMapper.selectBasFbaList(basFbaDTO));
        }catch (Exception e){
            e.printStackTrace();
            return R.failed("查询失败");
        }

    }

    @Override
    public R deleteBasFba(Integer id) {
        try {
            baseMapper.deleteByPrimaryKey(id);
            return R.ok("操作成功");
        }catch (Exception e){
            e.printStackTrace();
            return  R.failed("操作失败");
        }

    }
}
