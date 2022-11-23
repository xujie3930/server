package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.BusinessBasInterface;
import com.szmsd.bas.api.factory.BasFileFeignFallback;
import com.szmsd.bas.domain.BasFile;
import com.szmsd.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "FeignClient.BasFileFeignService", name = BusinessBasInterface.SERVICE_NAME, fallbackFactory = BasFileFeignFallback.class)
public interface BasFileFeignService {

    @PostMapping(value = "/basFile/addbasFile")
    R<BasFile>  addbasFile(@RequestBody BasFile basFile);


    @PostMapping(value = "/basFile/updatebasFile")
    R  updatebasFile(@RequestBody BasFile basFile);
}
