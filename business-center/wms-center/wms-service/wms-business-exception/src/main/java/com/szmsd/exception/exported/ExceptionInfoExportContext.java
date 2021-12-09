package com.szmsd.exception.exported;

import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author zhangyuyuan
 */
public class ExceptionInfoExportContext implements ExportContext {

    private final CacheContext<String, String> stateCache;

    public ExceptionInfoExportContext() {
        this.stateCache = new CacheContext.MapCacheContext<>();
    }

    private void setBySubCode(CacheContext<String, String> cacheContext, List<BasSubWrapperVO> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            for (BasSubWrapperVO vo : list) {
                cacheContext.put(vo.getSubCode(), vo.getSubName());
            }
        }
    }

    public void setStateCacheAdapter(List<BasSubWrapperVO> list) {
        this.setBySubCode(this.stateCache, list);
    }

    @Override
    public String getStateName(String state) {
        return this.stateCache.get(state);
    }

}
