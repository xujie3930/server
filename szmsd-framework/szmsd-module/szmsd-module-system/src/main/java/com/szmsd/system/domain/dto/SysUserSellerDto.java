package com.szmsd.system.domain.dto;

import com.szmsd.system.api.domain.SysUser;
import lombok.Data;

@Data
public class SysUserSellerDto extends SysUser {
    private String sellerCode;
}
