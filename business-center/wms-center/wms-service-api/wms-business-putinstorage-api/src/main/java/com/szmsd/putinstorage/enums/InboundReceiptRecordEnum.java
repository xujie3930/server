package com.szmsd.putinstorage.enums;

import com.szmsd.common.core.language.enums.LocalLanguageTypeEnum;
import com.szmsd.common.core.language.util.LanguageUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public enum InboundReceiptRecordEnum implements RecordEnum {

    CREATE {
        @Override
        public String getType() {
            return "创建";
        }

        @Override
        public String getWarehouseNo() {
            return "warehouseNo";
        }

        @Override
        public String getContent() {
            return "创建";
        }
    },

    ARRAIGNED {
        @Override
        public String getType() {
            return "提审";
        }

        @Override
        public String getContent() {
            return "提审";
        }
    },

    CANCEL {
        @Override
        public String getType() {
            return "取消";
        }

        @Override
        public String getContent() {
            return "取消";
        }
    },

    REVIEW {
        @Override
        public String getType() {
            return "审核";
        }

        @Override
        public String getWarehouseNo() {
            return "warehouseNos";
        }

        @Override
        public String getContent() {
            return "{0}，{1}";
        }

        @Override
        public List<String> getContentFill() {
            return Arrays.asList("status", "reviewRemark");
        }

        @Override
        public Function<List<String>, String> func() {
            return (param) -> {
                if (CollectionUtils.isEmpty(param)) {
                    return "";
                }
                String arg0 = LanguageUtil.getLanguage(LocalLanguageTypeEnum.INBOUND_RECEIPT_STATUS, param.get(0));
                String arg1 = param.get(1);
                return MessageFormat.format(this.getContent(), arg0, arg1);
            };
        }
    },

    PUT {
        @Override
        public String getType() {
            return "上架";
        }

        @Override
        public String getWarehouseNo() {
            return "orderNo";
        }

        @Override
        public String getSku() {
            return "sku";
        }

        @Override
        public String getContent() {
            return "SKU[{0}]，上架数量[{1}]";
        }

        @Override
        public List<String> getContentFill() {
            return Arrays.asList("sku", "qty");
        }

        @Override
        public String getCreateBy() {
            return "operator";
        }

        @Override
        public String getCreateByName() {
            return "operator";
        }

        @Override
        public String getCreateTime() {
            return "operateOn";
        }
    },

    COMPLETED {
        @Override
        public String getType() {
            return "完成";
        }

        @Override
        public String getWarehouseNo() {
            return "orderNo";
        }

        @Override
        public String getContent() {
            return "完成";
        }

        @Override
        public String getCreateBy() {
            return "operator";
        }

        @Override
        public String getCreateByName() {
            return "operator";
        }

        @Override
        public String getCreateTime() {
            return "operateOn";
        }
    };

    @Override
    public String getWarehouseNo() {
        return "";
    }

    @Override
    public String getSku() {
        return null;
    }

    @Override
    public List<String> getContentFill() {
        return null;
    }

    @Override
    public String getCreateBy() {
        return "";
    }

    @Override
    public String getCreateByName() {
        return "";
    }

    @Override
    public String getCreateTime() {
        return "";
    }

    @Override
    public Function<List<String>, String> func() {
        return (param) -> CollectionUtils.isEmpty(param) ? this.getContent() : MessageFormat.format(this.getContent(), param.toArray());
    }

}