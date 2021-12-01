package com.szmsd.chargerules.controller;

import com.szmsd.chargerules.domain.Operation;
import com.szmsd.chargerules.dto.OperationDTO;
import com.szmsd.chargerules.dto.OperationQueryDTO;
import com.szmsd.chargerules.enums.DelOutboundOrderEnum;
import com.szmsd.chargerules.enums.OperationConstant;
import com.szmsd.chargerules.service.IChaOperationService;
import com.szmsd.chargerules.service.IOperationService;
import com.szmsd.chargerules.vo.ChaOperationListVO;
import com.szmsd.chargerules.vo.ChaOperationVO;
import com.szmsd.chargerules.vo.OrderTypeLabelVo;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.vo.DelOutboundOperationVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Api(tags = {"业务计费规则"})
@RestController
@RequestMapping("/operation")
public class OperationController extends BaseController {

    @Resource
    private IOperationService operationService;
    @Resource
    private IChaOperationService iChaOperationService;
    @Resource
    private RedissonClient redissonClient;

    private String genKey() {
        String lockKey = Optional.ofNullable(SecurityUtils.getLoginUser()).map(LoginUser::getSellerCode).orElse("");
        String className = Thread.currentThread().getStackTrace()[2].getClassName();
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        return className + methodName + "#" + lockKey;
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:add')")
    @ApiOperation(value = "业务计费逻辑 - 保存")
    @PostMapping("/save")
    public R<Integer> save(@Valid @RequestBody OperationDTO dto) {
        RLock lock = redissonClient.getLock(genKey());
        try {
            if (lock.tryLock(OperationConstant.LOCK_TIME, OperationConstant.LOCK_TIME_UNIT)) {
                return R.ok(iChaOperationService.save(dto));
            } else {
                return R.failed("请求超时");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return R.failed("请求失败");
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:edit')")
    @ApiOperation(value = "业务计费逻辑 - 修改")
    @PutMapping("/update")
    public R<Integer> update(@RequestBody OperationDTO dto) {
        RLock lock = redissonClient.getLock(genKey());
        try {
            if (lock.tryLock(OperationConstant.LOCK_TIME, OperationConstant.LOCK_TIME_UNIT)) {
                return R.ok(iChaOperationService.update(dto));
            } else {
                return R.failed("请求超时");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return R.failed("请求失败");
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:edit')")
    @ApiOperation(value = "业务计费逻辑 - 删除")
    @DeleteMapping("/deleteById/{id}")
    public R<Integer> deleteById(@PathVariable("id") Integer id) {
        return R.ok(iChaOperationService.deleteById(id));
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:list')")
    @ApiOperation(value = "业务计费逻辑 - 分页查询")
    @PostMapping("/list")
    public TableDataInfo<ChaOperationListVO> listPage(@RequestBody OperationQueryDTO dto) {
        startPage();
        return getDataTable(iChaOperationService.queryOperationList(dto));
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:details')")
    @ApiOperation(value = "业务计费逻辑 - 详情")
    @GetMapping("/details/{id}")
    public R<ChaOperationVO> details(@PathVariable("id") Long id) {
        return R.ok(iChaOperationService.queryDetails(id));
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:queryDetails')")
    @ApiOperation(value = "业务计费逻辑 - 详情")
    @PostMapping("/queryDetails")
    public R<Operation> queryDetails(@RequestBody OperationDTO operationDTO) {
        return R.ok(operationService.queryDetails(operationDTO));
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:getOrderTypeList')")
    @ApiOperation(value = "业务计费逻辑 - 查询订单类型")
    @GetMapping("/getOrderTypeList")
    public R<List<OrderTypeLabelVo>> getOrderTypeList() {
        List<OrderTypeLabelVo> collect = Arrays.stream(DelOutboundOrderEnum.values()).map(value ->
                new OrderTypeLabelVo(value.getCode(), value.getName())).collect(Collectors.toList());
        return R.ok(collect);
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:delOutboundFreeze')")
    @ApiOperation(value = "业务计费 - 出库冻结余额")
    @PostMapping("/delOutboundFreeze")
    public R delOutboundFreeze(@RequestBody DelOutboundOperationVO delOutboundVO) {
        log.info("请求---------------------------- {}", delOutboundVO);
        return operationService.delOutboundFreeze(delOutboundVO);
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:delOutboundThaw')")
    @ApiOperation(value = "业务计费 - 出库解冻余额")
    @PostMapping("/delOutboundThaw")
    public R delOutboundThaw(@RequestBody DelOutboundOperationVO delOutboundVO) {
        return operationService.delOutboundThaw(delOutboundVO);
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:delOutboundCharge')")
    @ApiOperation(value = "业务计费 - 出库扣款")
    @PostMapping("/delOutboundCharge")
    public R delOutboundDeductions(@RequestBody DelOutboundOperationVO delOutboundVO) {
        return operationService.delOutboundDeductions(delOutboundVO);
    }

}
