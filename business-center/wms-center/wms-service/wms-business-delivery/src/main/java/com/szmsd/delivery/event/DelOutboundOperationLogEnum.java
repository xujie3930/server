package com.szmsd.delivery.event;

import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.enums.DelOutboundExceptionStateEnum;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.vo.DelOutboundOperationDetailVO;
import com.szmsd.inventory.domain.dto.InventoryOperateDto;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.MDC;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-06-22 13:57
 */
public enum DelOutboundOperationLogEnum implements OperationLogEnum {

    CREATE {
        final MessageFormat format = new MessageFormat("新增出库单:{0},类型:{1}");

        @Override
        public String getType() {
            return "新增";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType())};
            return format.format(arguments);
        }
    },
    UPDATE {
        final MessageFormat format = new MessageFormat("修改出库单:{0},类型:{1}");

        @Override
        public String getType() {
            return "修改";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType())};
            return format.format(arguments);
        }
    },
    DELETE {
        final MessageFormat format = new MessageFormat("删除出库单:{0},类型:{1}");

        @Override
        public String getType() {
            return "删除";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType())};
            return format.format(arguments);
        }
    },
    CANCEL {
        final MessageFormat format = new MessageFormat("取消出库单:{0},类型:{1}");

        @Override
        public String getType() {
            return "取消";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType())};
            return format.format(arguments);
        }
    },
    HANDLER {
        final MessageFormat format = new MessageFormat("处理出库单:{0},类型:{1}");

        @Override
        public String getType() {
            return "处理";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType())};
            return format.format(arguments);
        }
    },
    FURTHER_HANDLER {
        final MessageFormat format = new MessageFormat("继续处理出库单:{0},类型:{1}");

        @Override
        public String getType() {
            return "继续处理";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType())};
            return format.format(arguments);
        }
    },
    /**
     * brv and rk_brv
     *
     * @see com.szmsd.delivery.service.wrapper.BringVerifyEnum
     */
    BRV_PRC_PRICING {
        final MessageFormat format = new MessageFormat("出库单:{0},类型:{1},长宽高:{2}*{3}*{4},计费重:{5} {6},费用:{7} {8}");

        @Override
        public String getType() {
            return "提交-PRC计费";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType()),
                    delOutbound.getLength(), delOutbound.getWidth(), delOutbound.getHeight(),
                    delOutbound.getCalcWeight(), delOutbound.getCalcWeightUnit(),
                    delOutbound.getAmount(), delOutbound.getCurrencyCode()};
            return format.format(arguments);
        }
    },
    BRV_FREEZE_BALANCE {
        final MessageFormat format = new MessageFormat("出库单:{0},类型:{1},费用:{2} {3}");

        @Override
        public String getType() {
            return "提交-冻结物流费用";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType()),
                    delOutbound.getAmount(), delOutbound.getCurrencyCode()};
            return format.format(arguments);
        }
    },
    BRV_PRODUCT_INFO {
        final MessageFormat format = new MessageFormat("出库单:{0},类型:{1},产品代码(发货规则):{2},结果@挂号获取方式:{3},发货服务名称:{4}");

        @Override
        public String getType() {
            return "提交-获取产品信息";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType()),
                    delOutbound.getShipmentRule(),
                    delOutbound.getTrackingAcquireType(), delOutbound.getShipmentService()};
            return format.format(arguments);
        }
    },
    BRV_SHIPMENT_RULE {
        final MessageFormat format = new MessageFormat("出库单:{0},类型:{1},产品代码(发货规则):{2}");

        @Override
        public String getType() {
            return "提交-新增/修改发货规则";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType()),
                    delOutbound.getShipmentRule()};
            return format.format(arguments);
        }
    },
    BRV_SHIPMENT_ORDER {
        final MessageFormat format = new MessageFormat("出库单:{0},类型:{1},创建承运商返回结果,挂号:{2},承运商订单号:{3}");

        @Override
        public String getType() {
            return "提交-创建承运商物流订单";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType()),
                    delOutbound.getTrackingNo(), delOutbound.getShipmentOrderNumber()};
            return format.format(arguments);
        }
    },
    BRV_FREEZE_INVENTORY {
        final MessageFormat format = new MessageFormat("出库单:{0},类型:{1},库存:{2}");

        @Override
        public String getType() {
            return "提交-冻结库存";
        }

        @Override
        boolean personalize() {
            return true;
        }

        @SuppressWarnings({"unchecked"})
        @Override
        String personalizeFormat(Object object) {
            Object[] objects = (Object[]) object;
            DelOutbound delOutbound = (DelOutbound) objects[0];
            List<InventoryOperateDto> list = (List<InventoryOperateDto>) objects[1];
            String inventoryText = "";
            if (CollectionUtils.isNotEmpty(list)) {
                String[] strings = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    InventoryOperateDto dto = list.get(i);
                    strings[i] = "(SKU:" + dto.getSku() + ",数量:" + dto.getNum() + ")";
                }
                inventoryText = String.join(",", strings);
            }
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType()),
                    inventoryText};
            return format.format(arguments);
        }
    },
    BRV_FREEZE_OPERATION {
        final MessageFormat format = new MessageFormat("出库单:{0},类型:{1},库存:{2}");

        @Override
        public String getType() {
            return "提交-冻结操作费用";
        }

        @Override
        boolean personalize() {
            return true;
        }

        @SuppressWarnings({"unchecked"})
        @Override
        String personalizeFormat(Object object) {
            Object[] objects = (Object[]) object;
            DelOutbound delOutbound = (DelOutbound) objects[0];
            List<DelOutboundOperationDetailVO> list = (List<DelOutboundOperationDetailVO>) objects[1];
            String inventoryText = "";
            if (CollectionUtils.isNotEmpty(list)) {
                String[] strings = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    DelOutboundOperationDetailVO vo = list.get(i);
                    strings[i] = "(SKU:" + vo.getSku() + ",数量:" + vo.getQty() + ",重量:" + vo.getWeight() + ")";
                }
                inventoryText = String.join(",", strings);
            }
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType()),
                    inventoryText};
            return format.format(arguments);
        }
    },
    BRV_SHIPMENT_CREATE {
        final MessageFormat format = new MessageFormat("出库单:{0},类型:{1},WMS单号:{2}");

        @Override
        public String getType() {
            return "提交-推单WMS";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType()),
                    delOutbound.getRefOrderNo()};
            return format.format(arguments);
        }
    },
    BRV_SHIPMENT_LABEL {
        final MessageFormat format = new MessageFormat("出库单:{0},类型:{1}");

        @Override
        public String getType() {
            return "提交-更新标签";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType())};
            return format.format(arguments);
        }
    },
    RK_BRV_FREEZE_BALANCE {
        final MessageFormat format = new MessageFormat("出库单:{0},类型:{1},费用:{2} {3}");

        @Override
        public String getType() {
            return "提交-取消冻结物流费用";
        }

        @Override
        String format(DelOutbound delOutbound) {
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType()),
                    delOutbound.getAmount(), delOutbound.getCurrencyCode()};
            return format.format(arguments);
        }
    },
    RK_BRV_SHIPMENT_ORDER {
        final MessageFormat format = new MessageFormat("出库单:{0},类型:{1},挂号:{2},承运商订单号:{3}");

        @Override
        public String getType() {
            return "提交-取消承运商物流订单";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType()),
                    delOutbound.getTrackingNo(), delOutbound.getShipmentOrderNumber()};
            return format.format(arguments);
        }
    },
    RK_BRV_FREEZE_INVENTORY {
        final MessageFormat format = new MessageFormat("出库单:{0},类型:{1}");

        @Override
        public String getType() {
            return "提交-取消冻结库存";
        }

        @Override
        String format(DelOutbound delOutbound) {
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType())};
            return format.format(arguments);
        }
    },
    RK_BRV_FREEZE_OPERATION {
        final MessageFormat format = new MessageFormat("出库单:{0},类型:{1},费用:{2} {3}");

        @Override
        public String getType() {
            return "提交-取消冻结操作费用";
        }

        @Override
        String format(DelOutbound delOutbound) {
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType()),
                    delOutbound.getAmount(), delOutbound.getCurrencyCode()};
            return format.format(arguments);
        }
    },
    /**
     * smt
     *
     * @see com.szmsd.delivery.service.wrapper.ShipmentEnum
     */
    SMT_SHIPMENT_ORDER {
        @Override
        public String getType() {
            return "发货指令-创建承运商物流订单";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            return BRV_SHIPMENT_ORDER.format(delOutbound);
        }
    },
    SMT_SHIPMENT_TRACKING {
        final MessageFormat format = new MessageFormat("出库单:{0},类型:{1},更新挂号:{2}");

        @Override
        public String getType() {
            return "发货指令-更新挂号";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType()),
                    delOutbound.getTrackingNo()};
            return format.format(arguments);
        }
    },
    SMT_LABEL {
        final MessageFormat format = new MessageFormat("出库单:{0},类型:{1},承运商订单号:{2},挂号:{3}");

        @Override
        public String getType() {
            return "发货指令-获取标签";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType()),
                    delOutbound.getShipmentOrderNumber(), delOutbound.getTrackingNo()};
            return format.format(arguments);
        }
    },
    SMT_SHIPMENT_LABEL {
        final MessageFormat format = new MessageFormat("出库单:{0},类型:{1}");

        @Override
        public String getType() {
            return "发货指令-更新标签";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType())};
            return format.format(arguments);
        }
    },
    SMT_THAW_BALANCE {
        @Override
        public String getType() {
            return "发货指令-取消冻结物流费用";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            return RK_BRV_FREEZE_BALANCE.format(delOutbound);
        }
    },
    SMT_PRC_PRICING {
        @Override
        public String getType() {
            return "发货指令-PRC计费";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            return BRV_PRC_PRICING.format(delOutbound);
        }
    },
    SMT_FREEZE_BALANCE {
        @Override
        public String getType() {
            return "发货指令-冻结物流费用";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            return BRV_FREEZE_BALANCE.format(delOutbound);
        }
    },
    SMT_SHIPMENT_SHIPPING {
        final MessageFormat format = new MessageFormat("出库单:{0},类型:{1},挂号:{2},是否异常:{3}");
        final MessageFormat exFormat = new MessageFormat("出库单:{0},类型:{1},挂号:{2},是否异常:{3},异常原因:{4}");

        @Override
        public String getType() {
            return "发货指令-更新发货指令";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            if (DelOutboundExceptionStateEnum.ABNORMAL.getCode().equals(delOutbound.getExceptionState())) {
                Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType()),
                        delOutbound.getTrackingNo(), "true", delOutbound.getExceptionMessage()};
                return exFormat.format(arguments);
            } else {
                Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType()),
                        delOutbound.getTrackingNo(), "false"};
                return format.format(arguments);
            }
        }
    },
    /**
     * opn
     *
     * @see com.szmsd.delivery.controller.DelOutboundOpenController
     */
    OPN_SHIPMENT {
        final MessageFormat format = new MessageFormat("出库单:{0},更新状态:{1}");

        @Override
        public String getType() {
            return "WMS指令-接收出库单状态";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundStateEnum.getOriginName(delOutbound.getState())};
            return format.format(arguments);
        }
    },
    OPN_PACKING {
        final MessageFormat format = new MessageFormat("出库单:{0},类型:{1},更新包材:{2}");

        @Override
        public String getType() {
            return "WMS指令-接收出库包裹信息";
        }

        @Override
        boolean personalize() {
            return true;
        }

        @Override
        String personalizeFormat(Object object) {
            Object[] objects = (Object[]) object;
            DelOutbound delOutbound = (DelOutbound) objects[0];
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType()),
                    objects[1]};
            return format.format(arguments);
        }
    },
    OPN_CONTAINERS {
        final MessageFormat format = new MessageFormat("出库单:{0},类型:{1}");

        @Override
        public String getType() {
            return "WMS指令-接收批量出库单类型装箱信息";
        }

        @Override
        public String format(DelOutbound delOutbound) {
            Object[] arguments = new Object[]{delOutbound.getOrderNo(), DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType())};
            return format.format(arguments);
        }
    },

    AGAIN_TRACKING_NO {
        final MessageFormat format = new MessageFormat("出库单:{0},类型:{1},物流服务由{2}改为{3}");

        @Override
        public String getType() {
            return "重新获取挂号";
        }

        @Override
        boolean personalize() {
            return true;
        }

        @Override
        String personalizeFormat(Object object) {
            Object[] objects = (Object[]) object;
            DelOutbound delOutbound = (DelOutbound) objects[0];
            Object[] arguments = new Object[]{delOutbound.getOrderNo(),
                    DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType()),
                    delOutbound.getShipmentRule(),
                    objects[1]};
            return format.format(arguments);
        }

    };

    /**
     * 个性化
     *
     * @return boolean
     */
    boolean personalize() {
        return false;
    }

    /**
     * 个性化格式化
     * <blockquote><pre>
     * boolean personalize() {
     *     return true;
     * }
     * </pre></blockquote>
     *
     * @return String
     */
    String personalizeFormat(Object object) {
        return "";
    }

    /**
     * 格式化
     *
     * @param delOutbound delOutbound
     * @return String
     */
    String format(DelOutbound delOutbound) {
        return "";
    }

    @Override
    public String getInvoiceNo(Object object) {
        DelOutbound delOutbound;
        if (this.personalize()) {
            delOutbound = (DelOutbound) ((Object[]) object)[0];
        } else {
            delOutbound = (DelOutbound) object;
        }
        return delOutbound.getOrderNo();
    }

    @Override
    public String getInvoiceType(Object object) {
        DelOutbound delOutbound;
        if (this.personalize()) {
            delOutbound = (DelOutbound) ((Object[]) object)[0];
        } else {
            delOutbound = (DelOutbound) object;
        }
        return DelOutboundOrderTypeEnum.getOriginName(delOutbound.getOrderType());
    }

    @Override
    public String getLog(Object object) {
        if (null == object) {
            return "";
        }
        if (this.personalize()) {
            return this.personalizeFormat(object);
        } else if (object instanceof DelOutbound) {
            DelOutbound delOutbound = (DelOutbound) object;
            return this.format(delOutbound);
        }
        return "";
    }

    /**
     * listener
     */
    public void listener(Object object) {
        String tid = MDC.get("TID");
        String userCode = "";
        String userName = "";
        String ip = "";
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (null != loginUser) {
            userCode = String.valueOf(loginUser.getUserId());
            userName = loginUser.getUsername();
        }
        Date nowDate = new Date();
        EventUtil.publishEvent(new DelOutboundOperationLogEvent(object, this, tid, userCode, userName, ip, nowDate));
    }
}
