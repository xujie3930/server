package com.szmsd.http.plugins;

import com.szmsd.http.service.IHtpWarehouseMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component(value = "Ck1DomainPlugin")
public class Ck1DomainPlugin extends AbstractDomainPlugin {
    private final Pattern p = Pattern.compile("\\$\\{(.*?)}");
    @Autowired
    private IHtpWarehouseMappingService warehouseMappingService;

    @Override
    public String requestBody(String requestBody) {
        // {\"WarehouseId\":\"${WarCode:NJ}\"}
        // ${WarCode:NJ}
        // {\"WarehouseId\":\"15\"}
        Matcher matcher = p.matcher(requestBody);
        while (matcher.find()) {
            // ${WarCode:NJ}
            String group = matcher.group();
            if (group.startsWith("${WarCode:")) {
                // NJ
                String warCode = group.substring(10, group.indexOf("}"));
                // 15
                String warId = this.warehouseMappingService.getMappingWarCode(warCode);
                requestBody = requestBody.replace(group, warId);
            }
        }
        return requestBody;
    }

}
