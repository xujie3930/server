package com.szmsd.ec.common.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageInfo;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.StringToolkit;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.ec.common.event.ShopifyFulfillmentEvent;
import com.szmsd.ec.common.service.ICommonOrderItemService;
import com.szmsd.ec.common.service.ICommonOrderService;
import com.szmsd.ec.constant.OrderStatusConstant;
import com.szmsd.ec.domain.CommonOrder;
import com.szmsd.ec.domain.CommonOrderItem;
import com.szmsd.ec.dto.CommonOrderDTO;
import com.szmsd.ec.dto.LabelCountDTO;
import com.szmsd.ec.dto.TransferCallbackDTO;
import com.szmsd.ec.enums.OrderSourceEnum;
import com.szmsd.ec.enums.OrderStatusEnum;
import com.szmsd.ec.shopify.task.ShopifyOrderTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 电商平台公共订单表 前端控制器
 * </p>
 *
 * @author zengfanlang
 * @since 2021-12-17
 */
@Api(tags = {"电商平台公共订单表"})
@RestController
@RequestMapping("/ec-common-order/")
public class CommonOrderController extends BaseController {

    @Resource
    private ICommonOrderService ecCommonOrderService;

    @Resource
    private ICommonOrderItemService ecCommonOrderItemService;

    @Autowired
    private ShopifyOrderTask shopifyOrderTask;

    @Autowired
    private ApplicationContext applicationContext;

    @ApiOperation("转仓库单回调")
    @PostMapping("transferCallback")
    public R transferCallback(@RequestBody @Valid TransferCallbackDTO callbackDTO){
        CommonOrder order = this.ecCommonOrderService.getOne(new LambdaQueryWrapper<CommonOrder>().eq(CommonOrder::getOrderNo, callbackDTO.getOrderNo()).last("limit 1"));
        if (order == null) {
            return R.failed("电商单不存在");
        }
        order.setStatus(callbackDTO.getStatus().toString()); // 转单成功 更改状态
        order.setWarehouseCode(callbackDTO.getWarehouseCode());
        order.setWarehouseName(callbackDTO.getWarehouseName());
        order.setWaybillNo(callbackDTO.getWaybillNo());
        order.setOrderType(callbackDTO.getOrderType());
        order.setTransferErrorMsg(callbackDTO.getTransferErrorMsg());
        // 已发货状态调用shopify的履约单接口： https://shopify.dev/api/admin-rest/2021-10/resources/fulfillment#[post]/admin/api/2021-10/fulfillments.json
        if (OrderSourceEnum.Shopify.toString().equalsIgnoreCase(order.getOrderSource()) && OrderStatusEnum.Shipped.equals(callbackDTO.getStatus())) {
            applicationContext.publishEvent(new ShopifyFulfillmentEvent(order));
        }
        return ecCommonOrderService.updateById(order) ? R.ok() : R.failed();
    }

    @ApiOperation(value = "根据参数获取分页列表")
    @GetMapping("/listPage")
    public TableDataInfo listPage(CommonOrderDTO queryDTO) {
        startPage();
        LambdaQueryWrapper<CommonOrder> queryWrapper = commonSearchWrapper(queryDTO);
        queryWrapper.orderByDesc(CommonOrder::getOrderDate);
        List<CommonOrder> commonOrders = this.ecCommonOrderService.list(queryWrapper);
        commonOrders.forEach(item -> {
            item.setCommonOrderItemList(ecCommonOrderItemService.list(new LambdaQueryWrapper<CommonOrderItem>().eq(CommonOrderItem::getOrderId, item.getId())));
        });
        TableDataInfo<CommonOrderDTO> tableDataInfo = getDataTable(JSONObject.parseArray(JSON.toJSONString(commonOrders), CommonOrderDTO.class));
        tableDataInfo.setTotal(new PageInfo(commonOrders).getTotal());
        return tableDataInfo;
    }

    @ApiOperation("手动转仓库单")
    @PostMapping("/updateOrderList")
    public R updateOrderList(@RequestBody List<CommonOrderDTO> tDTOList) {
        for (int i = 0; i < tDTOList.size(); i++) {
            this.ecCommonOrderService.transferWarehouseOrder(tDTOList.get(i));
        }
        return R.ok("正在转仓库单，请耐心等候");
    }

    @ApiOperation("手动拉单")
    @GetMapping("/pullOrder")
    public R pullOrder(@RequestParam String shopName){
        shopifyOrderTask.getShopifyListOrderByShopName(shopName);
        return R.ok();
    }

    @ApiOperation(value = "获取订单各状态合计数量")
    @GetMapping("/getCountByStatus")
    public R getCountByStatus(CommonOrderDTO queryDTO) {
        LambdaQueryWrapper<CommonOrder> queryWrapper = commonSearchWrapper(queryDTO);
        queryWrapper.groupBy(CommonOrder::getStatus);
        List<LabelCountDTO> statusList = ecCommonOrderService.getCountByStatus(queryWrapper);

        Map<String, Object> collect = statusList.stream().collect(Collectors.toMap(item -> item.getStatus(), l -> l.getCount()));
        return R.ok(collect);
    }

    @ApiOperation(value = "批量删除")
    @PostMapping("batchDelete")
    public R deleteOrder(@RequestBody List<Long> ids){
        ecCommonOrderService.update(new LambdaUpdateWrapper<CommonOrder>().set(CommonOrder::getStatus, OrderStatusConstant.DELETED).in(CommonOrder::getId, ids));
        return R.ok();
    }

    /**
     * 公共search param wrapper 封装
     * @param queryDTO
     * @return
     */
    private LambdaQueryWrapper<CommonOrder> commonSearchWrapper(CommonOrderDTO queryDTO){
        LambdaQueryWrapper<CommonOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(queryDTO.getOrderNo()), CommonOrder::getOrderNo, queryDTO.getOrderNo())
                .eq(StringUtils.isNotEmpty(queryDTO.getStatus()), CommonOrder::getStatus, queryDTO.getStatus())
                .eq(StringUtils.isNotEmpty(queryDTO.getOrderSource()), CommonOrder::getOrderSource, queryDTO.getOrderSource())
                .eq(StringUtils.isNotEmpty(queryDTO.getShopName()), CommonOrder::getShopName, queryDTO.getShopName())
                .eq(StringUtils.isNotEmpty(queryDTO.getPlatformOrderNumber()), CommonOrder::getPlatformOrderNumber, queryDTO.getPlatformOrderNumber())
                .eq(StringUtils.isNotEmpty(queryDTO.getPlatformOrderStatus()), CommonOrder::getPlatformOrderStatus, queryDTO.getPlatformOrderStatus());
        if (StringUtils.isNotBlank(queryDTO.getOrderNo())) {
            List<String> orderNoArray = StringToolkit.getCodeByArray(queryDTO.getOrderNo());
            queryWrapper.in(CommonOrder::getOrderNo, orderNoArray);
        }
        if (StringUtils.isNotBlank(queryDTO.getTransferNumber())) {
            List<String> transferNumberArray = StringToolkit.getCodeByArray(queryDTO.getTransferNumber());
            queryWrapper.in(CommonOrder::getTransferNumber, transferNumberArray);
        }
        if (StringUtils.isNotEmpty(queryDTO.getCreateDates()) && queryDTO.getCreateDates().length > 1) {
            queryWrapper.between(CommonOrder::getOrderDate, queryDTO.getCreateDates()[0] + " 00:00:00", queryDTO.getCreateDates()[1] + " 23:59:59");
        }
        return queryWrapper;
    }
}
