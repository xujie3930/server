package com.szmsd.delivery.imported;

import com.szmsd.delivery.dto.DelOutboundPackageTransferImportDto;

public class PackageTransferDefaultAnalysisFormat implements DefaultAnalysisFormat<DelOutboundPackageTransferImportDto> {

    @Override
    public DelOutboundPackageTransferImportDto format(DelOutboundPackageTransferImportDto value) {
        if (null != value) {
            String postCode = value.getPostCode();
            if (null != postCode && postCode.endsWith(".0")) {
                value.setPostCode(postCode.substring(0, postCode.length() - 2));
            }
        }
        return value;
    }
}
