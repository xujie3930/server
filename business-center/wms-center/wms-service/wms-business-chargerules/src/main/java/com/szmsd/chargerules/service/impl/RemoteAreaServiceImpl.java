package com.szmsd.chargerules.service.impl;

import com.szmsd.chargerules.dto.RemoteAreaQueryDTO;
import com.szmsd.chargerules.service.IRemoteAreaService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.http.api.feign.HtpRemoteAreaTemplateFeignService;
import com.szmsd.http.dto.RemoteAreaTemplateIdCriteria;
import com.szmsd.http.dto.RemoteAreaTemplateSearchCriteria;
import com.szmsd.http.vo.ImportResult;
import com.szmsd.http.vo.RemoteAreaTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@Slf4j
@Service
public class RemoteAreaServiceImpl implements IRemoteAreaService {

    @Resource
    private HtpRemoteAreaTemplateFeignService htpRemoteAreaTemplateFeignService;

    /**
     * 分页查询地址库模板列表，返回指定页面的数据，以及统计总记录数
     * https://api-productremotearea-external.dsloco.com/api/productremotearea/pageResult
     * @param remoteAreaQueryDTO
     * @return
     */
    @Override
    public TableDataInfo<RemoteAreaTemplate> page(RemoteAreaQueryDTO remoteAreaQueryDTO) {
        RemoteAreaTemplateSearchCriteria remoteAreaTemplateSearchCriteria = new RemoteAreaTemplateSearchCriteria();
        remoteAreaTemplateSearchCriteria.setPageNumber(remoteAreaQueryDTO.getPageNum());
        remoteAreaTemplateSearchCriteria.setPageSize(remoteAreaQueryDTO.getPageSize());
        remoteAreaTemplateSearchCriteria.setName(remoteAreaQueryDTO.getName());
        log.info("分页查询地址库模板列表：{}", remoteAreaTemplateSearchCriteria);
        R<PageVO<RemoteAreaTemplate>> pageResult = htpRemoteAreaTemplateFeignService.pageResult(remoteAreaTemplateSearchCriteria);
        AssertUtil.notNull(pageResult.getData(), pageResult.getMsg());
        return TableDataInfo.convert(pageResult.getData());
    }

    /**
     * 导入地址库模板
     * https://api-productremotearea-external.dsloco.com/api/productremotearea/importFile
     * @param file
     * @return
     */
    @Override
    public ImportResult importFile(MultipartFile file) {
        R<ImportResult> importResultR = htpRemoteAreaTemplateFeignService.importFile(file);
        return importResultR.getData();
    }

    /**
     * 导出地址库模板信息
     * https://api-productremotearea-external.dsloco.com/api/productremotearea/exportFile
     * @param remoteAreaTemplateIdCriteria
     * @return
     */
    @Override
    public FileStream exportFile(RemoteAreaTemplateIdCriteria remoteAreaTemplateIdCriteria) {
        R<FileStream> fileStreamR = htpRemoteAreaTemplateFeignService.exportFile(remoteAreaTemplateIdCriteria);
        return fileStreamR.getData();
    }
}
