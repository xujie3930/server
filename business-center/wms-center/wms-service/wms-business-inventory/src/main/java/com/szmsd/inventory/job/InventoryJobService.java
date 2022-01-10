package com.szmsd.inventory.job;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.bas.dto.BasSellerEmailDto;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.http.api.feign.HtpRmiFeignService;
import com.szmsd.http.config.CkConfig;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.enums.DomainEnum;
import com.szmsd.http.util.Ck1DomainPluginUtil;
import com.szmsd.http.vo.HttpResponseVO;
import com.szmsd.http.vo.InventoryInfo;
import com.szmsd.inventory.component.RemoteComponent;
import com.szmsd.inventory.component.RemoteRequest;
import com.szmsd.inventory.domain.InventoryWarning;
import com.szmsd.inventory.domain.dto.CkSkuInventoryQueryDTO;
import com.szmsd.inventory.domain.dto.InventorySkuQueryDTO;
import com.szmsd.inventory.domain.vo.CkSkuInventoryVO;
import com.szmsd.inventory.domain.vo.InventorySkuVO;
import com.szmsd.inventory.service.IInventoryService;
import com.szmsd.inventory.service.IInventoryWarningService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InventoryJobService {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RemoteComponent remoteComponent;

    @Resource
    private RemoteRequest remoteRequest;

    @Resource
    private IInventoryService iInventoryService;

    @Resource
    private IInventoryWarningService iInventoryWarningService;

    @Resource
    private Executor inventoryTaskExecutor;

    @Resource
    private HtpRmiFeignService htpRmiFeignService;

    @Resource
    private CkConfig ckConfig;

    @Scheduled(cron = "0 0 2 * * ?")
    public void inventoryWarning() {
        InventoryJobService inventoryJobService = SpringUtils.getBean("inventoryJobService");
        inventoryJobService.asyncInventoryWarning();
    }

    //    @Async
    public void asyncInventoryWarning() {
        log.info("OMS <-> WMS 库存对比开始");
        List<BasSellerEmailDto> customerList = remoteComponent.queryCusAll();
        /*BasSellerEmailDto basSellerEmailDto = new BasSellerEmailDto();
        basSellerEmailDto.setSellerCode("TESTID73");
        List<BasSellerEmailDto> customerList = Arrays.asList(basSellerEmailDto);*/
        if (CollectionUtils.isEmpty(customerList)) {
            log.info("未查询到相关客户信息：库存对比结束");
            return;
        }
        String batchNo = DateUtils.dateTimeNow() + RandomUtil.randomNumbers(6);
        customerList.forEach(customer -> CompletableFuture.supplyAsync(() -> inventoryWarning(customer.getSellerCode()), inventoryTaskExecutor).thenAcceptAsync(data -> {
            if (CollectionUtils.isEmpty(data)) {
                return;
            }
            System.out.println("==================================================================");
            System.out.println(JSONObject.toJSONString(data));
            System.out.println("==================================================================");
            List<InventoryWarning> inventoryWarning = BeanMapperUtil.mapList(data, InventoryWarning.class);
            inventoryWarning.forEach(item -> item.setCusCode(customer.getSellerCode()).setBatchNo(batchNo));
            iInventoryWarningService.createAndSendEmail(null, inventoryWarning);
            log.info("对比完成---发送对比信息");
        }, inventoryTaskExecutor).exceptionally(e -> {
            e.printStackTrace();
            return null;
        }));
    }

    public List<WarehouseSkuCompare> inventoryWarning(String cusCode) {
        RLock lock = redissonClient.getLock("InventoryJobService:inventoryWarning:" + cusCode);
        try {
            if (lock.tryLock()) {
                // OMS 库存
                List<InventorySkuVO> inventoryListOms = iInventoryService.selectList(new InventorySkuQueryDTO().setCusCode(cusCode));

                if (CollectionUtils.isEmpty(inventoryListOms)) {
                    log.info("客户[{}]没有库存", cusCode);
                    return null;
                }
                List<SkuQty> inventories = inventoryListOms.stream().map(SkuQty::new).collect(Collectors.toList());
                log.info("客户[{}], 库存[{}]", cusCode, inventories);
                Map<String, List<SkuQty>> inventoryMapOms = inventories.stream().collect(Collectors.groupingBy(SkuQty::getWarehouse));
                /*List<SkuQty> nj = inventoryMapOms.get("NJ");
                inventoryMapOms.clear();
                inventoryMapOms.put("NJ",nj);*/
                // WMS 库存
                List<WarehouseSkuCompare> compareList = new CopyOnWriteArrayList<>();
                // key: 仓库   ;  value: List<SkuQty>
                inventoryMapOms.forEach((warehouseCode, skuQtyList) -> {
                    System.out.println("【RUNNER】==================================================================" + cusCode + "warehouseCode" + warehouseCode);
                    // 查询 OMS - WMS 库存
                    CompletableFuture<Void> wmsCompletableFuture = CompletableFuture.runAsync(() ->
                            skuQtyList.forEach(item -> {
                                WarehouseSkuCompare compare = compare(item, (warehouseCode2, sku) -> {
                                    List<InventoryInfo> listing = remoteRequest.listing(warehouseCode2, sku);
                                    if (CollectionUtils.isEmpty(listing)) {
                                        return null;
                                    }
                                    return listing.get(0);
                                });
                                if (compare == null) {
                                    log.info("客户[{}], 仓库[{}], SKU[{}], 没有产生差异", cusCode, warehouseCode, item.getSku());
                                    WarehouseSkuCompare warehouseSkuCompare = new WarehouseSkuCompare(item, 0);
                                    compareList.add(warehouseSkuCompare);
                                    return;
                                }
                                log.info("客户[{}], 仓库[{}], SKU[{}], 产生差异", cusCode, warehouseCode, item.getSku());
                                // 查询CK1 的库存记录
                                compareList.add(compare);
                            }));
                    wmsCompletableFuture.join();

                    // CK1 库存
                    System.out.println("【RUNNER】==================================================================" + cusCode + "warehouseCode" + warehouseCode);
                    List<String> skuList = skuQtyList.stream().map(SkuQty::getSku).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
                    int size = skuList.size();
                    int count = 30;
                    int segment = size % count == 0 ? size / count : size / count + 1;
                    ArrayList<CompletableFuture<List<CkSkuInventoryVO>>> completableFutures = new ArrayList<>();
                    for (int i = 0; i < segment; i++) {
                        List<String> skus;
                        if (i == segment - 1) {
                            skus = skuList.subList(count * i, size);
                        } else {
                            skus = skuList.subList(count * i, count * (i + 1));
                        }
                        CompletableFuture<List<CkSkuInventoryVO>> voidCompletableFuture = CompletableFuture.supplyAsync(() -> {
                            log.info("【CK1】查询sku:{} :{}", warehouseCode, JSONObject.toJSONString(skus));
                            List<CkSkuInventoryVO> ckSkuInventoryRespList = new ArrayList<>();
                            // 每30个sku调用一次
                            HttpRequestDto httpRequestDto = new HttpRequestDto();
                            httpRequestDto.setMethod(HttpMethod.POST);
                            httpRequestDto.setBinary(false);
                            CkSkuInventoryQueryDTO ckSkuInventoryQueryDTO = new CkSkuInventoryQueryDTO();
                            ckSkuInventoryQueryDTO.setSkus(skus);
                            // httpRequestDto.setHeaders(DomainInterceptorUtil.genSellerCodeHead(inventoryAdjustmentDTO.getSellerCode()));
                            ckSkuInventoryQueryDTO.setWarehouseId(Ck1DomainPluginUtil.wrapper(warehouseCode));
                            httpRequestDto.setBody(ckSkuInventoryQueryDTO);
                            httpRequestDto.setUri(DomainEnum.Ck1OpenAPIDomain.wrapper(ckConfig.getCheckInventoryUrl()));
                            try {
                                R<HttpResponseVO> rmi = htpRmiFeignService.rmi(httpRequestDto);
                                HttpResponseVO dataAndException = R.getDataAndException(rmi);
                                if (dataAndException.checkStatusFlag()) {
                                    log.info("【CK1】 req：{}:{}", warehouseCode, JSONObject.toJSONString(httpRequestDto));
                                    String body = (String) dataAndException.getBody();
                                    ckSkuInventoryRespList = JSONObject.parseArray(body, CkSkuInventoryVO.class);
                                    log.info("【CK1】 res：{}:{}", warehouseCode, JSONObject.toJSONString(ckSkuInventoryRespList));
                                    return ckSkuInventoryRespList;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return ckSkuInventoryRespList;
                        });
                        completableFutures.add(voidCompletableFuture);
                    }
                    // 拼装数据
                    CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]));
                    voidCompletableFuture.join();
                    ArrayList<CkSkuInventoryVO> ckSkuInventoryResultList = new ArrayList<>();
                    completableFutures.forEach(x -> {
                        try {
                            ckSkuInventoryResultList.addAll(x.get());
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            log.error("执行异常----");
                        }
                    });
                    Map<String, Integer> collect = ckSkuInventoryResultList.stream().collect(Collectors.toMap(CkSkuInventoryVO::getSku, CkSkuInventoryVO::getTotalStockQty, (x1, x2) -> x1));
                    System.out.println("》》》》》" + JSONObject.toJSONString(collect));
                    // 设置出口易库存数量
                    compareList.stream().filter(x -> x.getWarehouse().equals(warehouseCode)).forEach(warehouseSkuCompare -> warehouseSkuCompare.setCkQty(Optional.ofNullable(collect.get(warehouseSkuCompare.getSku())).orElse(0)));
                    System.out.println("》》》》》" + JSONObject.toJSONString(compareList));
                    System.out.println("【FINISH】==================================================================" + cusCode + "warehouseCode" + warehouseCode);
                });

                return compareList;
            }
        } finally {
            lock.unlock();
        }
        return null;
    }

    public WarehouseSkuCompare compare(SkuQty skuQty, BiFunction<String, String, InventoryInfo> consumer) {
        InventoryInfo inventoryInfo = consumer.apply(skuQty.warehouse, skuQty.getSku());
        if (inventoryInfo == null) {
            return new WarehouseSkuCompare(skuQty, 0);
        }
        if (new SkuQty(inventoryInfo).equals(skuQty)) {
            return null;
        }
        return new WarehouseSkuCompare(skuQty, inventoryInfo.getQty());
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public static class WarehouseSkuCompare extends SkuQty {
        /**
         * WMS库存
         **/
        private Integer existQty;
        /**
         * 出口易库存
         */
        private Integer ckQty;

        public WarehouseSkuCompare(SkuQty skuQty, Integer existQty) {
            super(skuQty.getWarehouse(), skuQty.getSku(), skuQty.getQty());
            this.existQty = existQty;
        }

    }

    @Data
    @Accessors(chain = true)
    public static class SkuQty {
        /**
         * 仓库
         **/
        private String warehouse;

        /**
         * 总库存
         **/
        private String sku;

        /**
         * 总库存
         **/
        private Integer qty;

        public SkuQty() {
        }

        public SkuQty(String warehouse, String sku, Integer qty) {
            this.warehouse = warehouse;
            this.sku = sku;
            this.qty = qty;
        }

        public SkuQty(InventorySkuVO inventorySkuVO) {
            this.sku = inventorySkuVO.getSku();
            this.warehouse = inventorySkuVO.getWarehouseCode();
            this.qty = inventorySkuVO.getTotalInventory();
        }

        public SkuQty(InventoryInfo inventoryInfo) {
            this.sku = inventoryInfo.getSku();
            this.warehouse = inventoryInfo.getWarehouseCode();
            this.qty = inventoryInfo.getQty();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SkuQty skuQty = (SkuQty) o;
            return Objects.equals(sku, skuQty.sku) &&
                    Objects.equals(warehouse, skuQty.warehouse) &&
                    Objects.equals(qty, skuQty.qty);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sku, warehouse, qty);
        }
    }

}
