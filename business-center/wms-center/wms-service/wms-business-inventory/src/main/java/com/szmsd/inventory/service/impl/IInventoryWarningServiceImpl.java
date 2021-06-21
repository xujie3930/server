package com.szmsd.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import com.szmsd.common.core.language.util.LanguageUtil;
import com.szmsd.inventory.domain.InventoryWarning;
import com.szmsd.inventory.job.EmailUtil;
import com.szmsd.inventory.mapper.InventoryWarningMapper;
import com.szmsd.inventory.service.IInventoryWarningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class IInventoryWarningServiceImpl extends ServiceImpl<InventoryWarningMapper, InventoryWarning> implements IInventoryWarningService {

    @Resource
    private EmailUtil emailUtil;

    @Override
    public void create(InventoryWarning inventoryWarning) {
    }

    @Override
    public void createAndSendEmail(String email, List<InventoryWarning> inventoryWarningList) {
        super.saveBatch(inventoryWarningList);
        if (!EmailUtil.isEmail(email)) {
            return;
        }
        Map<String, Object> model = new HashMap<>();
        InventoryWarning inventoryWarning = inventoryWarningList.get(0);
        model.put("cusName", LanguageUtil.getLanguage(RedisLanguageTable.BAS_CUSTOMER, inventoryWarning.getCusCode()));
        model.put("batchNo", inventoryWarning.getBatchNo());
        model.put("sysEmail", emailUtil.getFromEmail());
        model.put("data", inventoryWarningList);
        boolean b = emailUtil.sendTemplateMail(email, "CK1 - SKU库存对比", "email.html", model);
        if (b) {
            LambdaUpdateWrapper<InventoryWarning> updateBy = Wrappers.lambdaUpdate();
            updateBy.set(InventoryWarning::getSendEmailFlag, "1");
            updateBy.eq(InventoryWarning::getCusCode, inventoryWarning.getCusCode());
            updateBy.eq(InventoryWarning::getBatchNo, inventoryWarning.getBatchNo());
            this.update(updateBy);
        }
    }
}
