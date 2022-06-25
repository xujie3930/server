package com.szmsd.http.dto.chaLevel;

import com.szmsd.http.dto.PageDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 取消入库单
 */
@Data
@Accessors(chain = true)
public class ChaLevelMaintenancePageRequest extends PageDTO {

    /** name **/
    private String name;

}
