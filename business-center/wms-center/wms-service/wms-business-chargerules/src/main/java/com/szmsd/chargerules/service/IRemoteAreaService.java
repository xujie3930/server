package com.szmsd.chargerules.service;

import com.szmsd.chargerules.dto.RemoteAreaQueryDTO;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.http.dto.RemoteAreaTemplateIdCriteria;
import com.szmsd.http.vo.ImportResult;
import com.szmsd.http.vo.RemoteAreaTemplate;
import org.springframework.web.multipart.MultipartFile;

public interface IRemoteAreaService {

    TableDataInfo<RemoteAreaTemplate> page(RemoteAreaQueryDTO remoteAreaQueryDTO);

    ImportResult importFile(MultipartFile file);

    FileStream exportFile(RemoteAreaTemplateIdCriteria remoteAreaTemplateIdCriteria);

}
