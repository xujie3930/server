package com.szmsd.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import com.szmsd.common.core.language.util.LanguageUtil;
import com.szmsd.inventory.domain.InventoryWarning;
import com.szmsd.inventory.domain.dto.InventoryWarningQueryDTO;
import com.szmsd.inventory.domain.dto.InventoryWarningSendEmailDTO;
import com.szmsd.inventory.job.EmailUtil;
import com.szmsd.inventory.mapper.InventoryWarningMapper;
import com.szmsd.inventory.service.IInventoryWarningService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class IInventoryWarningServiceImpl extends ServiceImpl<InventoryWarningMapper, InventoryWarning> implements IInventoryWarningService {

    @Resource
    private EmailUtil emailUtil;

    @Resource
    private Executor inventoryTaskExecutor;

    @Override
    public void create(InventoryWarning inventoryWarning) {
    }

    @Override
    public void createAndSendEmail(String email, List<InventoryWarning> inventoryWarningList) {
        if (CollectionUtils.isEmpty(inventoryWarningList)) {
            return;
        }
        super.saveBatch(inventoryWarningList);
        if (!EmailUtil.isEmail(email)) {
            return;
        }
        InventoryWarning inventoryWarning = inventoryWarningList.get(0);
        String cusName = LanguageUtil.getLanguage(RedisLanguageTable.BAS_CUSTOMER, inventoryWarning.getCusCode());
        boolean b = sendEmail(cusName, inventoryWarning.getBatchNo(), inventoryWarningList, email);
        if (b) {
            LambdaUpdateWrapper<InventoryWarning> updateBy = Wrappers.lambdaUpdate();
            updateBy.set(InventoryWarning::getSendEmailFlag, "1");
            updateBy.eq(InventoryWarning::getCusCode, inventoryWarning.getCusCode());
            updateBy.eq(InventoryWarning::getBatchNo, inventoryWarning.getBatchNo());
            this.update(updateBy);
        }
    }

    @Override
    public List<InventoryWarning> selectList(InventoryWarningQueryDTO queryDTO) {
        return baseMapper.selectList(queryDTO);
    }

    @Override
    public void sendEmail(InventoryWarningSendEmailDTO sendEmailDTO) {
        Long id = sendEmailDTO.getId();
        String batchNo = sendEmailDTO.getBatchNo();
        String toEmail = sendEmailDTO.getToEmail();
        toEmail = StringUtils.isEmpty(toEmail) ? "liangchao@szmsd.com" : toEmail;
        String finalToEmail = toEmail;
        CompletableFuture.runAsync(() -> {
            if (id != null) {
                Optional.ofNullable(super.getById(id)).ifPresent(item -> {
                    String cusName = LanguageUtil.getLanguage(RedisLanguageTable.BAS_CUSTOMER, item.getCusCode());
                    boolean b = sendEmail(cusName, item.getBatchNo(), Collections.singletonList(item), finalToEmail);
                    if (b) {
                        baseMapper.updateById(item.setSendEmailFlag("1"));
                    }
                });
            }
            if (StringUtils.isNotEmpty(batchNo)) {
                List<InventoryWarning> inventoryWarnings = this.selectList(new InventoryWarningQueryDTO().setBatchNo(batchNo));
                if (CollectionUtils.isNotEmpty(inventoryWarnings)) {
                    InventoryWarning inventoryWarning = inventoryWarnings.get(0);
                    String cusName = LanguageUtil.getLanguage(RedisLanguageTable.BAS_CUSTOMER, inventoryWarning.getCusCode());
                    boolean b = sendEmail(cusName, inventoryWarning.getBatchNo(), inventoryWarnings, finalToEmail);
                    if (b) {
                        LambdaUpdateWrapper<InventoryWarning> updateBy = Wrappers.lambdaUpdate();
                        updateBy.set(InventoryWarning::getSendEmailFlag, "1");
                        updateBy.eq(InventoryWarning::getBatchNo, inventoryWarning.getBatchNo());
                        this.update(updateBy);
                    }
                }
            }
        }, inventoryTaskExecutor);
    }

    @Override
    public List<String> selectBatch() {
        return baseMapper.selectBatch();
    }

    public boolean sendEmail(String cusName, String batchNo, List<InventoryWarning> data, String toEmail) {
        Map<String, Object> model = new HashMap<>();
        model.put("cusName", cusName);
        model.put("batchNo", batchNo);
        model.put("sysEmail", emailUtil.getFromEmail());
        model.put("data", data);
        return emailUtil.sendTemplateMail(toEmail, "CK1 - SKU库存对比", "email.html", model);
    }
}
