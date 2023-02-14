package com.szmsd.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 账户余额表日志新表
 * </p>
 *
 * @author xujie
 * @since 2023-02-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("fss_account_balance_log_new")
public class FssAccountBalanceLogNewEntity implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 客户id
     */
    private Integer cusId;

    /**
     * 客户代码
     */
    private String cusCode;

    /**
     * 客户名称
     */
    private String cusName;

    /**
     * 币种编码
     */
    private String currencyCode;

    /**
     * 币种名称
     */
    private String currencyName;

    /**
     * 当前可用余额
     */
    private BigDecimal currentBalance;

    /**
     * 冻结余额
     */
    private BigDecimal freezeBalance;

    /**
     * 总余额
     */
    private BigDecimal totalBalance;

    /**
     * 授信类型(0：额度，1：类型)
     */
    private Integer creditType;

    /**
     * 授信状态（0：未启用，1：启用中，2：欠费停用，3：已禁用）
     */
    private Integer creditStatus;

    /**
     * 授信额度
     */
    private BigDecimal creditLine;

    /**
     * 使用额度金额
     */
    private BigDecimal creditUseAmount;

    /**
     * 授信开始时间
     */
    private Date creditBeginTime;

    /**
     * 授信结束时间
     */
    private Date creditEndTime;

    /**
     * 授信时间间隔
     */
    private Integer creditTimeInterval;

    /**
     * 授信时间是否修改(0:未修改,1:已修改)
     */
    private Integer creditTimeFlag;

    /**
     * 授信时间单位
     */
    private String creditTimeUnit;

    /**
     * 授信缓冲截止时间
     */
    private Date creditBufferTime;

    /**
     * 授信缓冲时间间隔
     */
    private Integer creditBufferTimeInterval;

    /**
     * 授信缓冲时间单位
     */
    private String creditBufferTimeUnit;

    /**
     * 需要偿还金额
     */
    private BigDecimal creditRepaidAmount;

    /**
     * 创建人名称
     */
    private String createByName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人名称
     */
    private String updateByName;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 修改人
     */
    private String updateBy;

    /**
     * 备注
     */
    private String remark;

    /**
     * 版本号
     */
    private Long version;

    /**
     * 数据生成时间
     */
    private Date generatorTime;

    public String getUniCode() {
        return cusCode + currencyCode;
    }
}
