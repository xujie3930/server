package com.szmsd.chargerules.service.impl;

import com.szmsd.chargerules.domain.ChaLevelMaintenanceDtoQuery;
import com.szmsd.chargerules.service.IChaLevelMaintenanceService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.http.api.feign.HtpChaLevelMaintenanceFeignService;
import com.szmsd.http.dto.chaLevel.ChaLevelMaintenanceDto;
import com.szmsd.http.dto.chaLevel.ChaLevelMaintenancePageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* <p>
    *  服务实现类
    * </p>
*
* @author admin
* @since 2022-06-22
*/
@Service
public class ChaLevelMaintenanceServiceImpl  implements IChaLevelMaintenanceService {



    @Resource
    private HtpChaLevelMaintenanceFeignService htpChaLevelMaintenanceFeignService;

    @Override
    public TableDataInfo<ChaLevelMaintenanceDto> selectChaLevelMaintenanceList(ChaLevelMaintenanceDtoQuery chaLevelMaintenance) {

        ChaLevelMaintenancePageRequest pageDTO = new ChaLevelMaintenancePageRequest();
        BeanUtils.copyProperties(chaLevelMaintenance, pageDTO);
        pageDTO.setPageSize(chaLevelMaintenance.getPageSize());
        pageDTO.setPageNumber(chaLevelMaintenance.getPageNum());
        R<PageVO<ChaLevelMaintenanceDto>> r = htpChaLevelMaintenanceFeignService.page(pageDTO);
        if(r.getCode() == 200){
            TableDataInfo tableDataInfo = new TableDataInfo();
            tableDataInfo.setCode(r.getCode());
            tableDataInfo.setRows(r.getData().getData());
            tableDataInfo.setTotal(r.getData().getTotalRecords());
            return tableDataInfo;
        }else{
            throw new CommonException("400", r.getMsg());
        }
    }

    @Override
    public R insertChaLevelMaintenance(ChaLevelMaintenanceDto chaLevelMaintenance) {
        return htpChaLevelMaintenanceFeignService.create(chaLevelMaintenance);
    }

    @Override
    public R updateChaLevelMaintenance(ChaLevelMaintenanceDto chaLevelMaintenance) {
        return htpChaLevelMaintenanceFeignService.update(chaLevelMaintenance);
    }

    @Override
    public R deleteChaLevelMaintenanceById(String id) {
        return htpChaLevelMaintenanceFeignService.delete(id);
    }

    @Override
    public R<ChaLevelMaintenanceDto> selectChaLevelMaintenanceById(String id) {
        return htpChaLevelMaintenanceFeignService.get(id);
    }
}

