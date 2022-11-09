package com.szmsd.finance.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.github.pagehelper.PageInfo;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.plugin.annotation.AutoValue;
import com.szmsd.delivery.domain.DelOutboundTarckError;
import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.domain.AccountBalanceExcle;
import com.szmsd.finance.domain.AccountBalanceExcleEn;
import com.szmsd.finance.dto.*;
import com.szmsd.finance.service.IAccountBalanceService;
import com.szmsd.finance.vo.UserCreditInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.*;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.szmsd.finance.factory.abstractFactory.AbstractPayFactory.leaseTime;
import static com.szmsd.finance.factory.abstractFactory.AbstractPayFactory.time;

/**
 * @author liulei
 */
@Api(tags = {"账户余额管理"})
@RestController
@RequestMapping("/accountBalance")
public class AccountBalanceController extends FssBaseController {
    @Autowired
    IAccountBalanceService accountBalanceService;
    @Resource
    private RedissonClient redissonClient;

    @PreAuthorize("@ss.hasPermi('ExchangeRate:listPage')")
    @ApiOperation(value = "分页查询账户余额信息")
    @GetMapping("/listPage")
    @AutoValue
    public R<PageInfo<AccountBalance>> listPage(AccountBalanceDTO dto) {
         String len=getLen();

        return  accountBalanceService.listPage(dto,len);
    }


    /**
     * 导出账户信息
     */
    @PreAuthorize("@ss.hasPermi('ExchangeRate:accountBalanceExport')")
    @Log(title = "导出账户信息", businessType = BusinessType.EXPORT)
    @GetMapping("/accountBalanceExport")
    @ApiOperation(value = "导出账户信息",notes = "导出账户信息")
    public void accountBalanceExport(HttpServletResponse response,AccountBalanceDTO dto) throws IOException {
        String len=getLen();

        List<AccountBalanceExcle> list = accountBalanceService.accountBalanceExport(dto,len);
        ExportParams params = new ExportParams();




        Workbook workbook = null;
        if (len.equals("zh")){
            workbook=   ExcelExportUtil.exportExcel(params, AccountBalanceExcle.class, list);

        }else if (len.equals("en")){
            List<AccountBalanceExcleEn> list1= BeanMapperUtil.mapList(list,AccountBalanceExcleEn.class);
            workbook=   ExcelExportUtil.exportExcel(params, AccountBalanceExcleEn.class, list1);

        }


        Sheet sheet= workbook.getSheet("sheet0");

        //获取第一行数据
        Row row2 =sheet.getRow(0);

        for (int i=0;i<9;i++){
            Cell deliveryTimeCell = row2.getCell(i);

            CellStyle styleMain = workbook.createCellStyle();
            styleMain.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
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
//        CellStyle style =  workbook.createCellStyle();
//        style.setFillPattern(HSSFColor.HSSFColorPredefined.valueOf(""));
//        style.setFillForegroundColor(IndexedColors.RED.getIndex());
            deliveryTimeCell.setCellStyle(styleMain);
        }



        try {
            String fileName="账户信息"+System.currentTimeMillis();
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

    @PreAuthorize("@ss.hasPermi('ExchangeRate:list')")
    @ApiOperation(value = "查询账户余额信息")
    @PostMapping("/list")
    public R<List<AccountBalance>> list(@RequestBody AccountBalanceDTO dto) {
        return R.ok(accountBalanceService.listPages(dto));
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:recordListPage')")
    @ApiOperation(value = "分页查询账户余额变动")
    @GetMapping("/recordListPage")
    public TableDataInfo recordListPage(AccountBalanceChangeDTO dto) {
        startPage();
        String len=getLen();
        List<AccountBalanceChange> list = accountBalanceService.recordListPage(dto);
        list.forEach(x-> {
            if (len.equals("en")) {
                x.setCurrencyName(x.getCurrencyCode());
            } else if (len.equals("zh")) {
                x.setCurrencyName(x.getCurrencyName());
            }
        });
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:preOnlineIncome')")
    @ApiOperation(value = "预充值")
    @PostMapping("/preOnlineIncome")
    public R preOnlineIncome(@RequestBody CustPayDTO dto) {
        return accountBalanceService.preOnlineIncome(dto);
    }

    @PostMapping("/rechargeCallback")
    @ApiOperation(value = "第三方充值接口回调")
    public R rechargeCallback(@RequestBody RechargesCallbackRequestDTO requestDTO){
        return accountBalanceService.rechargeCallback(requestDTO);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:onlineIncome')")
    @ApiOperation(value = "在线充值（仅供测试）")
    @PostMapping("/onlineIncome")
    public R onlineIncome(@RequestBody CustPayDTO dto) {
        return accountBalanceService.onlineIncome(dto);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:offlineIncome')")
    @ApiOperation(value = "线下充值（仅供测试）")
    @PostMapping("/offlineIncome")
    public R offlineIncome(@RequestBody CustPayDTO dto) {
        return accountBalanceService.offlineIncome(dto);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:withdraw')")
    @ApiOperation(value = "提现（仅供测试）")
    @PostMapping("/withdraw")
    public R withdraw(@RequestBody CustPayDTO dto) {
        return accountBalanceService.withdraw(dto);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:balanceExchange')")
    @ApiOperation(value = "余额汇率转换")
    @PostMapping("/balanceExchange")
    public R balanceExchange(@RequestBody CustPayDTO dto) {
        return accountBalanceService.balanceExchange(dto);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:warehouseFeeDeduct')")
    @ApiOperation(value = "仓储费用扣除")
    @PostMapping("/warehouseFeeDeduct")
    public R warehouseFeeDeductions(@RequestBody CustPayDTO dto){
        final String key = "cky-fss-freeze-balance-all:" + dto.getCusCode();
        RLock lock = redissonClient.getLock(key);
        log.info("仓储费用扣除-尝试获取redis锁 {}",key);
        try {
            if (lock.tryLock(time,leaseTime, TimeUnit.SECONDS)){
                log.info("仓储费用扣除-获取redis锁 {}成功",key);
                return accountBalanceService.warehouseFeeDeductions(dto);
            }else {
                log.info("仓储费用扣除-获取redis锁 {}失败",key);
                return R.failed("仓储费用扣除操作超时,请稍候重试!");
            }
        }catch (Exception e){
            log.error("仓储费用扣除操作超时，{}",e);
            return R.failed("仓储费用扣除操作超时,请稍候重试!");
        }finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                log.info("仓储费用扣除-释放redis锁 {}",key);
                lock.unlock();
            }
        }
//        return accountBalanceService.warehouseFeeDeductions(dto);
    }

    /**
     * 出库时 扣除 基本费用/扣除物料费用 接口
     * @param dto
     * @return
     */
    @PreAuthorize("@ss.hasPermi('ExchangeRate:feeDeductions')")
    @ApiOperation(value = "费用扣除")
    @PostMapping("/feeDeductions")
    public R feeDeductions(@RequestBody CustPayDTO dto){
        final String key = "cky-fss-freeze-balance-all:" + dto.getCusCode();
        RLock lock = redissonClient.getLock(key);
        log.info("费用扣除-尝试获取redis锁 {}",key);
        try {
            if (lock.tryLock(time,leaseTime, TimeUnit.SECONDS)){
                log.info("费用扣除-获取redis锁 {}成功",key);
                return accountBalanceService.feeDeductions(dto);
            }else {
                log.info("费用扣除-获取redis锁 {}失败",key);
                return R.failed("冻结操作超时,请稍候重试!");
            }
        }catch (Exception e){
            log.error("费用扣除操作超时，{}",e);
            return R.failed("费用扣除操作超时,请稍候重试!");
        }finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                log.info("费用扣除-释放redis锁 {}",key);
                lock.unlock();
            }
        }
//        return accountBalanceService.feeDeductions(dto);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:freezeBalance')")
    @ApiOperation(value = "冻结余额")
    @PostMapping("/freezeBalance")
    public R freezeBalance(@RequestBody CusFreezeBalanceDTO dto){
        final String key = "cky-fss-freeze-balance-all:" + dto.getCusCode();
        RLock lock = redissonClient.getLock(key);
        log.info("冻结余额-尝试获取redis锁 {}",key);
        try {
            if (lock.tryLock(time,leaseTime, TimeUnit.SECONDS)){
                log.info("冻结余额-获取redis锁 {}成功",key);
                return accountBalanceService.freezeBalance(dto);
            }else {
                log.info("冻结余额-获取redis锁 {}失败",key);
                return R.failed("冻结操作超时,请稍候重试!");
            }
        }catch (Exception e){
            log.error("冻结操作超时，{}",e);
            return R.failed("冻结操作超时,请稍候重试!");
        }finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                log.info("冻结余额-释放redis锁 {}",key);
                lock.unlock();
            }
        }
//        return accountBalanceService.freezeBalance(dto);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:thawBalance')")
    @ApiOperation(value = "解冻余额")
    @PostMapping("/thawBalance")
    public R thawBalance(@RequestBody CusFreezeBalanceDTO dto){
        final String key = "cky-fss-freeze-balance-all:" + dto.getCusCode();
        RLock lock = redissonClient.getLock(key);
        log.info("解冻余额-尝试获取redis锁 {}",key);
        try {
            if (lock.tryLock(time,leaseTime, TimeUnit.SECONDS)){
                log.info("解冻余额-获取redis锁 {}成功",key);
                return accountBalanceService.thawBalance(dto);
            }else {
                log.info("解冻余额-获取redis锁 {}失败",key);
                return R.failed("解冻操作超时,请稍候重试!");
            }
        }catch (Exception e){
            log.error("冻结操作超时，{}",e);
            return R.failed("解冻操作超时,请稍候重试!");
        }finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                log.info("解冻余额-释放redis锁 {}",key);
                lock.unlock();
            }
        }
//        return accountBalanceService.thawBalance(dto);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:thawBalance')")
    @ApiOperation(value = "修改用户信用额信息")
    @PostMapping("/updateUserCredit")
    public R updateUserCredit(@Validated @RequestBody UserCreditDTO userCreditDTO){
        accountBalanceService.updateUserCredit(userCreditDTO);
        return R.ok();
    }
    @PreAuthorize("@ss.hasPermi('ExchangeRate:thawBalance')")
    @ApiOperation(value = "查询用户信用额信息")
    @GetMapping("/queryUserCredit/{cusCode}")
    public R<List<UserCreditInfoVO>> queryUserCredit(@PathVariable("cusCode") String cusCode){
        return R.ok(accountBalanceService.queryUserCredit(cusCode));
    }


}