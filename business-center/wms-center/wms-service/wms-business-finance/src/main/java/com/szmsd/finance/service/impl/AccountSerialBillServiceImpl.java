package com.szmsd.finance.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.szmsd.bas.api.feign.BasFileFeignService;
import com.szmsd.bas.domain.BasFile;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.DelOutboundListQueryDto;
import com.szmsd.delivery.vo.DelOutboundListVO;
import com.szmsd.finance.domain.AccountSerialBill;
import com.szmsd.finance.domain.AccountSerialBillTotalVO;
import com.szmsd.finance.domain.ChargeRelation;
import com.szmsd.finance.dto.*;
import com.szmsd.finance.mapper.AccountSerialBillMapper;
import com.szmsd.finance.mapper.ChargeRelationMapper;
import com.szmsd.finance.service.IAccountSerialBillService;
import com.szmsd.finance.service.ISysDictDataService;
import com.szmsd.finance.task.EasyPoiExportTask;
import com.szmsd.finance.vo.AccountSerialBillExcelVO;
import com.szmsd.putinstorage.api.feign.InboundReceiptFeignService;
import com.szmsd.putinstorage.domain.dto.InboundReceiptQueryDTO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AccountSerialBillServiceImpl extends ServiceImpl<AccountSerialBillMapper, AccountSerialBill> implements IAccountSerialBillService {

    @Resource
    private AccountSerialBillMapper accountSerialBillMapper;

    @Resource
    private ISysDictDataService sysDictDataService;

    @Resource
    private ChargeRelationMapper chargeRelationMapper;

    @Autowired
    private BasFileFeignService basFileFeignService;

    @Value("${filepath}")
    private String filePath;

    @Override
    public List<AccountSerialBill> listPage(AccountSerialBillDTO dto) {
        if (Objects.nonNull(SecurityUtils.getLoginUser())) {
            String cusCode = StringUtils.isNotEmpty(SecurityUtils.getLoginUser().getSellerCode()) ? SecurityUtils.getLoginUser().getSellerCode() : "";
            if (com.szmsd.common.core.utils.StringUtils.isEmpty(dto.getCusCode())) {
                dto.setCusCode(cusCode);
            }
        }

        this.generatorTime(dto);

        List<AccountSerialBill> accountSerialBills = accountSerialBillMapper.selectPageList(dto);
        // 修改下单时间等信息
        showProcess(accountSerialBills);
        return accountSerialBills;
    }

    @Resource
    private InboundReceiptFeignService inboundReceiptFeignService;
    @Resource
    private DelOutboundFeignService delOutboundFeignService;

    @Data
    @AllArgsConstructor
    private static class ListProcess {
        private Date orderTime;
        private String no;
        private String warehouseCode;

    }

    /**
     * 账单展示仓库 下单时间
     *
     * @param accountSerialBills
     */
    public void showProcess(List<AccountSerialBill> accountSerialBills) {
        ArrayList<AccountSerialBill> resultList = new ArrayList<>();
        List<AccountSerialBill> noList = accountSerialBills.stream().filter(x -> StringUtils.isNotBlank(x.getNo())).collect(Collectors.toList());
        // 查询下单时间 结算时间，列表时间展示
        CompletableFuture<List<ListProcess>> listCompletableFuture = CompletableFuture.supplyAsync(() -> {
            List<AccountSerialBill> rk = noList.parallelStream().filter(x -> x.getNo().startsWith("RK")).distinct().collect(Collectors.toList());
            String rkNo = rk.stream().map(AccountSerialBill::getNo).distinct().collect(Collectors.joining(","));

            List<ListProcess> result = new ArrayList<>();
            if (StringUtils.isNotBlank(rkNo)) {
                InboundReceiptQueryDTO inboundReceiptQueryDTO = new InboundReceiptQueryDTO();
                inboundReceiptQueryDTO.setWarehouseNo(rkNo);
                TableDataInfo<InboundReceiptVO> inboundReceiptVOTableDataInfo = inboundReceiptFeignService.postPage(inboundReceiptQueryDTO);
                if (inboundReceiptVOTableDataInfo.getCode() == HttpStatus.SUCCESS) {
                    List<InboundReceiptVO> ckPage = inboundReceiptVOTableDataInfo.getRows();
                    Map<String, InboundReceiptVO> collect = ckPage.stream().collect(Collectors.toMap(InboundReceiptVO::getWarehouseNo, x -> x, (x1, x2) -> x1));
                    result = rk.stream().map(x -> {
                        InboundReceiptVO inboundReceiptVO = collect.get(x.getNo());
                        if (Objects.nonNull(inboundReceiptVO)) {
                            Date createTime = inboundReceiptVO.getCreateTime();
                            String warehouseCode = inboundReceiptVO.getWarehouseCode();
                            return new ListProcess(createTime, x.getNo(), warehouseCode);
                        }
                        return null;
                    }).filter(Objects::nonNull).collect(Collectors.toList());
                }
            }
            return result;
        });


        CompletableFuture<List<ListProcess>> listCompletableFuture1 = CompletableFuture.supplyAsync(() -> {
            List<AccountSerialBill> ck = noList.parallelStream().filter(x -> x.getNo().startsWith("CK")).distinct().collect(Collectors.toList());
            String ckNo = ck.stream().map(AccountSerialBill::getNo).distinct().collect(Collectors.joining(","));
            if (StringUtils.isNotBlank(ckNo)) {
                DelOutboundListQueryDto delOutboundListQueryDto = new DelOutboundListQueryDto();
                delOutboundListQueryDto.setOrderNo(ckNo);
                TableDataInfo<DelOutboundListVO> page = delOutboundFeignService.page(delOutboundListQueryDto);
                if (page.getCode() == HttpStatus.SUCCESS) {
                    List<DelOutboundListVO> rows = page.getRows();
                    Map<String, DelOutboundListVO> collect = rows.stream().collect(Collectors.toMap(DelOutboundListVO::getOrderNo, x -> x, (x1, x2) -> x1));
                    return ck.stream().map(x -> {
                        DelOutboundListVO delOutboundListVO = collect.get(x.getNo());
                        if (Objects.nonNull(delOutboundListVO)) {
                            Date createTime = delOutboundListVO.getCreateTime();
                            String warehouseCode = delOutboundListVO.getWarehouseCode();
                            x.setOrderTime(createTime);
                            x.setWarehouseCode(warehouseCode);
                            return new ListProcess(createTime, x.getNo(), warehouseCode);
                        }
                        return null;
                    }).filter(Objects::nonNull).collect(Collectors.toList());
                }
            }
            return new ArrayList<>();
        });

        Void join = CompletableFuture.allOf(listCompletableFuture1, listCompletableFuture).join();

        List<ListProcess> listProcesses = listCompletableFuture.join();
        List<ListProcess> listProcesses1 = listCompletableFuture1.join();
        ArrayList<ListProcess> queryResult = new ArrayList<>();
        queryResult.addAll(listProcesses);
        queryResult.addAll(listProcesses1);
        Map<String, ListProcess> queryResultMap = queryResult.stream().collect(Collectors.toMap(ListProcess::getNo, x -> x, (x1, x2) -> x1));
        List<String> positiveNumber = Arrays.asList("线下充值", "退费", "优惠","赔偿");// 正数

        List<String> negativeNumber = Arrays.asList("补收", "增值消费"); //为负数
        accountSerialBills.forEach(x -> {
            if (StringUtils.isNotBlank(x.getNo())) {
                Optional.ofNullable(queryResultMap.get(x.getNo())).ifPresent(queryResultNo -> {
                    x.setWarehouseCode(queryResultNo.getWarehouseCode());
                    x.setOrderTime(queryResultNo.getOrderTime());
                });
            }
            if (null == x.getAmount()) x.setAmount(BigDecimal.ZERO);
            // 负数
            String businessCategory = x.getBusinessCategory();
            //费用类型，费用类型为汇率转换的也不能去转换负数
            String chargeType=x.getChargeType();
            if (StringUtils.isNotBlank(businessCategory) && positiveNumber.contains(businessCategory)) {
                x.setAmount(x.getAmount().abs());
            } else if (!chargeType.equals("汇率转换充值")){
                Optional.ofNullable(x.getAmount()).ifPresent(amount -> x.setAmount(amount.abs().negate()));
            }

        });

    }

    @Override
    public int add(AccountSerialBillDTO dto) {

        List<String> isPrcStateList = new ArrayList();
        isPrcStateList.add("操作费");
        isPrcStateList.add("仓租");
        isPrcStateList.add("增值消费");
        isPrcStateList.add("物料费");
        
        AccountSerialBill accountSerialBill = BeanMapperUtil.map(dto, AccountSerialBill.class);
        if (StringUtils.isBlank(accountSerialBill.getWarehouseName())) {
            accountSerialBill.setWarehouseName(sysDictDataService.getWarehouseNameByCode(accountSerialBill.getWarehouseCode()));
        }
        if (StringUtils.isBlank(accountSerialBill.getCurrencyName())) {
            accountSerialBill.setCurrencyName(sysDictDataService.getCurrencyNameByCode(dto.getCurrencyCode()));
        }
        accountSerialBill.setBusinessCategory(accountSerialBill.getChargeCategory());//性质列内容，同费用类别
        //单号不为空的时候
        if (StringUtils.isNotBlank(dto.getNo())){
           DelOutbound delOutbound=accountSerialBillMapper.selectDelOutbound(dto.getNo());
           if (delOutbound!=null){
               if (delOutbound.getId()!=null){
                   accountSerialBill.setRefNo(delOutbound.getRefNo());
                   accountSerialBill.setShipmentService(delOutbound.getShipmentService());
                   accountSerialBill.setWeight(delOutbound.getWeight());
                   accountSerialBill.setCalcWeight(delOutbound.getCalcWeight());
                   accountSerialBill.setSpecifications(delOutbound.getSpecifications());
               }
           }
        }

        String serialNumber = this.createSerialNumber();
        accountSerialBill.setSerialNumber(serialNumber);

        String bcategory = accountSerialBill.getBusinessCategory();
        if(bcategory != null){

            boolean prcState = isPrcStateList.contains(bcategory);
            if(!prcState){
                accountSerialBill.setPrcState(1);
            }
        }

        return accountSerialBillMapper.insert(accountSerialBill);
    }

    private String createSerialNumber(){

        String s = DateUtils.dateTime();
        String randomNums = RandomUtil.randomNumbers(8);

        return s + randomNums;
    }

    @Override
    public boolean saveBatch(List<AccountSerialBillDTO> dto) {

        List<String> isPrcStateList = new ArrayList();
        isPrcStateList.add("操作费");
        isPrcStateList.add("仓租");
        isPrcStateList.add("增值消费");
        isPrcStateList.add("物料费");

        List<AccountSerialBill> accountSerialBill = BeanMapperUtil.mapList(dto, AccountSerialBill.class);
        List<AccountSerialBill> collect = accountSerialBill.stream().map(value -> {
            if (StringUtils.isBlank(value.getWarehouseName())) {
                value.setWarehouseName(sysDictDataService.getWarehouseNameByCode(value.getWarehouseCode()));
            }
            if (StringUtils.isBlank(value.getCurrencyName())) {
                value.setCurrencyName(sysDictDataService.getCurrencyNameByCode(value.getCurrencyCode()));
            }
            value.setBusinessCategory(value.getChargeCategory());//性质列内容，同费用类别
            //单号不为空的时候
            if (StringUtils.isNotBlank(value.getNo())){
//                DelOutbound delOutbound=accountSerialBillMapper.selectDelOutbound(value.getNo());
//                if (delOutbound!=null) {
//                    if (delOutbound.getId() != null) {
//                        value.setRefNo(delOutbound.getRefNo());
//                        value.setShipmentService(delOutbound.getShipmentService());
//                        value.setWeight(delOutbound.getWeight());
//                        value.setCalcWeight(delOutbound.getCalcWeight());
//                        value.setSpecifications(delOutbound.getSpecifications());
//                    }
//                }
            }

            if(StringUtils.isEmpty(value.getAmazonLogisticsRouteId()) || value.getAmazonLogisticsRouteId().equals("null")){
                value.setAmazonLogisticsRouteId(null);
            }

            String serialNumber = this.createSerialNumber();
            value.setSerialNumber(serialNumber);

            String bcategory = value.getBusinessCategory();
            if(bcategory != null){

                boolean prcState =  isPrcStateList.contains(bcategory);
                if(!prcState){
                    value.setPrcState(1);
                }
            }

            return value;
        }).collect(Collectors.toList());
        boolean b = this.saveBatch(collect);
        if (!b){
            log.error("saveBatch() insert failed. {}", accountSerialBill);
        }
        return b;
    }

    /**
     * 幂等校验 校验重复扣费 ： 单号—发生额-业务类型
     *
     * @param dto
     * @return true : 已存在
     */
    @Override
    public boolean checkForDuplicateCharges(CustPayDTO dto) {
        List<AccountSerialBillDTO> serialBillInfoList = dto.getSerialBillInfoList();
        if (CollectionUtils.isEmpty(serialBillInfoList)){
            return false;
        }
        AccountSerialBillDTO accountSerialBillDTO = serialBillInfoList.get(0);

        Integer count = baseMapper.selectCount(Wrappers.<AccountSerialBill>lambdaQuery()
                .eq(AccountSerialBill::getNo, dto.getNo())
                .eq(AccountSerialBill::getAmount, accountSerialBillDTO.getAmount())
                .eq(AccountSerialBill::getChargeCategory, accountSerialBillDTO.getChargeCategory())
        );

        boolean flag = !count.equals(0);

        return flag;
    }

    @Override
    public void executeSerialBillNature() {

        Integer count = accountSerialBillMapper.selectBillOutbountCount();

        if(count == 0){
            return;
        }

        Integer pageSize = 5000;

        Integer totalPage = (count + pageSize -1) / pageSize;

        for(int i = 0;i<totalPage;i++){

            List<AccountSerialBillNatureDTO> accountSerialBillDTOList = accountSerialBillMapper.selectBillOutbount(i,pageSize);

            for(AccountSerialBillNatureDTO billNatureDTO : accountSerialBillDTOList){

                String chargeCategory = billNatureDTO.getChargeCategory();
                String businessCategory = billNatureDTO.getBusinessCategory();
                String orderType = billNatureDTO.getOrderType();
                Long id = billNatureDTO.getId();

                if(StringUtils.isBlank(businessCategory) || StringUtils.isBlank(orderType)){
                    continue;
                }

                List<ChargeRelation> chargeRelationList = chargeRelationMapper.findChargeRelation(businessCategory,orderType);

                if(CollectionUtils.isNotEmpty(chargeRelationList)){

                    ChargeRelation chargeRelation = chargeRelationList.get(0);

                    AccountSerialBill accountSerialBill = new AccountSerialBill();
                    accountSerialBill.setId(id);
                    accountSerialBill.setNature(chargeRelation.getNature());
                    accountSerialBill.setBusinessType(chargeRelation.getBusinessType());
                    accountSerialBill.setChargeCategoryChange(chargeRelation.getChargeCategoryChange());

                    accountSerialBillMapper.updateById(accountSerialBill);
                }
            }

        }

    }

    @Override
    public List<AccountBalanceBillCurrencyVO> findBillCurrencyData(AccountSerialBillDTO dto) {

        if (Objects.nonNull(SecurityUtils.getLoginUser())) {
            String cusCode = StringUtils.isNotEmpty(SecurityUtils.getLoginUser().getSellerCode()) ? SecurityUtils.getLoginUser().getSellerCode() : "";
            if (com.szmsd.common.core.utils.StringUtils.isEmpty(dto.getCusCode())) {
                dto.setCusCode(cusCode);
            }
        }

        this.generatorTime(dto);

        return accountSerialBillMapper.findBillCurrencyData(dto);
    }

    @Override
    public void exportBillTotal(HttpServletResponse response, AccountSerialBillDTO dto) {

        this.generatorTime(dto);

        List<AccountSerialBillTotalVO> accountSerialBillTotalVOS = accountSerialBillMapper.selectBillTotal(dto);

        ExcelUtil<AccountSerialBillTotalVO> util = new ExcelUtil<>(AccountSerialBillTotalVO.class);
        util.exportExcel(response,accountSerialBillTotalVOS,"业务明细汇总");

    }

    @Override
    public List<AccountSerialBillExcelVO> exportData(AccountSerialBillDTO dto) {

        this.generatorTime(dto);

        List<AccountSerialBillExcelVO> accountSerialBillTotalVOS = accountSerialBillMapper.exportData(dto);

        return accountSerialBillTotalVOS;
    }

    @Override
    public void asyncExport(HttpServletResponse response, AccountSerialBillDTO dto) {

        this.generatorTime(dto);

        LoginUser loginUser = SecurityUtils.getLoginUser();

        if(loginUser == null){
            throw new RuntimeException("无法获取登录人信息");
        }

        Integer pageSize = 100000;

        //查询条数
        int totalCount = accountSerialBillMapper.selectSerialBillCount(dto);

        if(totalCount == 0){
            throw new RuntimeException("无数据生成");
        }

        if(totalCount > 20000) {

            int pageTotal = totalCount % pageSize == 0 ? totalCount / pageSize : totalCount / pageSize + 1;

            CountDownLatch countDownLatch = new CountDownLatch(pageTotal);

            long start = System.currentTimeMillis();

            for (int i = 1; i <= pageTotal; i++) {

                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

                //查询数据
                PageHelper.startPage(i, pageSize);
                List<AccountSerialBillExcelVO> accountSerialBillExcelVOS = accountSerialBillMapper.exportData(dto);
                //List<AccountSerialBillExcelVO> accountSerialBillExcelVOS = AccountSerialBillConvert.INSTANCE.toSerialBillExcelListVO(accountSerialBills);

                String fileName = "业务明细-"+loginUser.getUsername()+"-" + simpleDateFormat.format(date);

                BasFile basFile = new BasFile();
                basFile.setState("0");
                basFile.setFileRoute(filePath);
                basFile.setCreateBy(SecurityUtils.getUsername());
                basFile.setFileName(fileName + ".xls");
                basFile.setModularType(0);
                basFile.setModularNameZh("业务明细导出");
                basFile.setModularNameEn("accountSerialBill");
                R<BasFile> r = basFileFeignService.addbasFile(basFile);
                BasFile basFile1 = r.getData();

                //分批异步写入excel

                EasyPoiExportTask<AccountSerialBillExcelVO> delOutboundExportExTask = new EasyPoiExportTask<AccountSerialBillExcelVO>()
                        .setExportParams(new ExportParams(fileName, "业务明细(" + ((i - 1) * pageSize) + "-" + (Math.min(i * pageSize, totalCount)) + ")", ExcelType.XSSF))
                        .setData(accountSerialBillExcelVOS)
                        .setClazz(AccountSerialBillExcelVO.class)
                        .setFilepath(filePath)
                        .setCountDownLatch(countDownLatch)
                        .setFileId(basFile1.getId());

                basFile1.setState("1");
                basFileFeignService.updatebasFile(basFile1);

                new Thread(delOutboundExportExTask, "export-" + i).start();

            }

            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            log.info("所有导出任务完成，总计耗时：{}ms", System.currentTimeMillis() - start);


        }else{

            PageHelper.startPage(1, pageSize);
            List<AccountSerialBillExcelVO> accountSerialBillExcelVOS = accountSerialBillMapper.exportData(dto);

            ExportParams params = new ExportParams();
            // 设置sheet得名称
            params.setSheetName("业务账单");
            ExportParams exportParams2 = new ExportParams();
            exportParams2.setSheetName("业务账单明细");
            // 创建sheet1使用得map
            Map<String, Object>  DelOutboundExportMap = new HashMap<>(4);
            // title的参数为ExportParams类型
            DelOutboundExportMap.put("title", params);
            // 模版导出对应得实体类型
            DelOutboundExportMap.put("entity", AccountSerialBillExcelVO.class);
            // sheet中要填充得数据
            DelOutboundExportMap.put("data", accountSerialBillExcelVOS);
            // 将sheet1和sheet2使用得map进行包装
            List<Map<String, Object>> sheetsList = new ArrayList<>();
            sheetsList.add(DelOutboundExportMap);

            Workbook workbook = ExcelExportUtil.exportExcel(sheetsList, ExcelType.HSSF);
            Sheet sheet= workbook.getSheet("业务账单");

            //获取第一行数据
            Row row2 =sheet.getRow(0);

            for (int i=0;i<26;i++){
                Cell deliveryTimeCell = row2.getCell(i);

                CellStyle styleMain = workbook.createCellStyle();

                styleMain.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());


                Font font = workbook.createFont();
                //true为加粗，默认为不加粗
                font.setBold(true);
                //设置字体颜色，颜色和上述的颜色对照表是一样的
                font.setColor(IndexedColors.WHITE.getIndex());
                //将字体样式设置到单元格样式中
                styleMain.setFont(font);

                styleMain.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                styleMain.setAlignment(HorizontalAlignment.CENTER);
                styleMain.setVerticalAlignment(VerticalAlignment.CENTER);

                deliveryTimeCell.setCellStyle(styleMain);
            }

            try {
                String fileName="业务账单"+System.currentTimeMillis();
                URLEncoder.encode(fileName, "UTF-8");
                //response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1"));
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xls");

                response.addHeader("Pargam", "no-cache");
                response.addHeader("Cache-Control", "no-cache");

                ServletOutputStream outStream = null;
                try {
                    outStream = response.getOutputStream();
                    workbook.write(outStream);
                    outStream.flush();
                } finally {
                    outStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }

    @Override
    public R<Integer> exportCount(AccountSerialBillDTO dto) {

        this.generatorTime(dto);

        int totalCount = accountSerialBillMapper.selectSerialBillCount(dto);

        return R.ok(totalCount);
    }

    @Override
    public List<AccountSerialBill> selectAccountPrcSerialBill(AccountOrderQueryDTO dto) {

        List<AccountSerialBill> accountSerialBills = accountSerialBillMapper.selectList(Wrappers.<AccountSerialBill>query().lambda()
                .in(AccountSerialBill::getNo,dto.getOrderNoList())
                .eq(AccountSerialBill::getPrcState,dto.getPrcState())
        );

        return accountSerialBills;
    }


    private void generatorTime(AccountSerialBillDTO dto){

        if(StringUtils.isNotBlank(dto.getCreateTimeStart())) {
            String billStartTime = dto.getCreateTimeStart() + " 00:00:00";
            dto.setCreateTimeStart(billStartTime);
        }

        if(StringUtils.isNotBlank(dto.getCreateTimeEnd())) {
            String billEndTime = dto.getCreateTimeEnd() + " 23:59:59";
            dto.setCreateTimeEnd(billEndTime);
        }

        if(StringUtils.isNotBlank(dto.getPaymentTimeStart())) {
            String billStartTime = dto.getPaymentTimeStart() + " 00:00:00";
            dto.setPaymentTimeStart(billStartTime);
        }
        if(StringUtils.isNotBlank(dto.getPaymentTimeEnd())) {
            String billEndTime = dto.getPaymentTimeEnd() + " 23:59:59";
            dto.setPaymentTimeEnd(billEndTime);
        }
    }


}
