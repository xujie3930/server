package com.szmsd.chargerules.export;

import com.github.pagehelper.Page;
import com.szmsd.common.core.utils.QueryPage;
import com.szmsd.delivery.vo.DelOutboundExportListVO;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-23 15:16
 */
public class GradeExportQueryPage implements QueryPage<DelOutboundExportListVO> {

    private List<GradeExportListVO> list;


    public GradeExportQueryPage(List<GradeExportListVO> list) {
    }

    @Override
    public Page<DelOutboundExportListVO> getPage() {
        Page<DelOutboundExportListVO> page = new Page<>();

        return page;
    }

    @Override
    public void nextPage() {
        // 下一页
    }
}
