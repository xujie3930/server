package com.szmsd.chargerules.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.cache.Ehcache;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.szmsd.chargerules.config.AnalysisListenerAbstract;
import com.szmsd.chargerules.config.DownloadTemplateUtil;
import com.szmsd.chargerules.config.IRemoteApi;
import com.szmsd.chargerules.config.LocalDateTimeConvert;
import com.szmsd.chargerules.dto.ChaOperationDetailsDTO;
import com.szmsd.chargerules.dto.OperationDTO;
import com.szmsd.chargerules.dto.OperationQueryDTO;
import com.szmsd.chargerules.enums.DelOutboundOrderEnum;
import com.szmsd.chargerules.enums.OperationConstant;
import com.szmsd.chargerules.enums.OrderTypeEnum;
import com.szmsd.chargerules.service.IChaOperationDetailsService;
import com.szmsd.chargerules.service.IChaOperationService;
import com.szmsd.chargerules.service.IOperationService;
import com.szmsd.chargerules.vo.ChaOperationDetailsVO;
import com.szmsd.chargerules.vo.ChaOperationListVO;
import com.szmsd.chargerules.vo.ChaOperationVO;
import com.szmsd.chargerules.vo.OrderTypeLabelVo;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.plugin.annotation.AutoValue;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.vo.DelOutboundOperationVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.*;
import javax.validation.groups.Default;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
    private IChaOperationDetailsService iChaOperationDetailsService;
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
    @AutoValue
    public TableDataInfo<ChaOperationListVO> listPage(@RequestBody OperationQueryDTO dto) {
        startPage(dto);
        return getDataTable(iChaOperationService.queryOperationList(dto));
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:details')")
    @ApiOperation(value = "业务计费逻辑 - 详情")
    @GetMapping("/details/{id}")
    @AutoValue
    public R<ChaOperationVO> details(@PathVariable("id") Long id) {
        return R.ok(iChaOperationService.queryDetails(id));
    }

    List<OrderTypeLabelVo> orderTypeLabelVoList = Arrays.stream(DelOutboundOrderEnum.values()).map(value ->
            new OrderTypeLabelVo(value.getCode(), value.getName())).collect(Collectors.toList());

    @PreAuthorize("@ss.hasPermi('Operation:Operation:getOrderTypeList')")
    @ApiOperation(value = "业务计费逻辑 - 查询订单类型")
    @GetMapping("/getOrderTypeList")
    public R<List<OrderTypeLabelVo>> getOrderTypeList() {
        return R.ok(orderTypeLabelVoList);
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:delOutboundFreeze')")
    @ApiOperation(value = "业务计费 - 出库冻结余额")
    @PostMapping("/delOutboundFreeze")
    public R delOutboundFreeze(@RequestBody DelOutboundOperationVO delOutboundVO) {
        log.info("请求---------------------------- {}", delOutboundVO);
        RLock lock = redissonClient.getLock(genKey());
        try {
            if (lock.tryLock(OperationConstant.LOCK_TIME, OperationConstant.LOCK_TIME_UNIT)) {
                return operationService.delOutboundFreeze(delOutboundVO);
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

    @PreAuthorize("@ss.hasPermi('Operation:Operation:delOutboundThaw')")
    @ApiOperation(value = "业务计费 - 出库解冻余额")
    @PostMapping("/delOutboundThaw")
    public R delOutboundThaw(@RequestBody DelOutboundOperationVO delOutboundVO) {
        log.info("请求---------------------------- {}", delOutboundVO);
        RLock lock = redissonClient.getLock(genKey());
        try {
            if (lock.tryLock(OperationConstant.LOCK_TIME, OperationConstant.LOCK_TIME_UNIT)) {
                return operationService.delOutboundThaw(delOutboundVO);
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

    @PreAuthorize("@ss.hasPermi('Operation:Operation:delOutboundCharge')")
    @ApiOperation(value = "业务计费 - 出库扣款")
    @PostMapping("/delOutboundCharge")
    public R delOutboundDeductions(@RequestBody DelOutboundOperationVO delOutboundVO) {
        log.info("请求---------------------------- {}", delOutboundVO);
        RLock lock = redissonClient.getLock(genKey());
        try {
            if (lock.tryLock(OperationConstant.LOCK_TIME, OperationConstant.LOCK_TIME_UNIT)) {
                return operationService.delOutboundDeductions(delOutboundVO);
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

    @PreAuthorize("@ss.hasPermi('Operation:Operation:delOutboundCharge')")
    @ApiOperation(value = "业务计费 - 下载模版")
    @PostMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse httpServletResponse) {
        DownloadTemplateUtil downloadTemplateUtil = DownloadTemplateUtil.getInstance();
        downloadTemplateUtil.getResourceByName(httpServletResponse, "ChargeOperation");
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:delOutboundCharge')")
    @ApiOperation(value = "业务计费 - 下载")
    @PostMapping("/download")
    public void download(HttpServletResponse httpServletResponse, OperationQueryDTO operationQueryDTO) {
        ExcelWriter excelWriter = null;
        try (ServletOutputStream outputStream = httpServletResponse.getOutputStream()) {
            String fileName = "ChargeOperation" + System.currentTimeMillis();
            String efn = URLEncoder.encode(fileName, "utf-8");
            httpServletResponse.setContentType("application/vnd.ms-excel");
            httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + efn + ".xlsx");
            excelWriter = EasyExcel.write(outputStream).build();
            WriteSheet build1 = EasyExcel.writerSheet(0, "基础信息").head(OperationDTO.class).registerConverter(new LocalDateTimeConvert()).build();
            WriteSheet build2 = EasyExcel.writerSheet(1, "详细信息").head(ChaOperationDetailsDTO.class).build();

            List<ChaOperationListVO> chaOperationListVOS = iChaOperationService.queryOperationList(operationQueryDTO);
            List<OperationDTO> collect = chaOperationListVOS.stream().map(x -> {
                String subCode = iRemoteApi.getSubNameBySubCode("098", x.getCusTypeCode());
                OperationDTO operationDTO = new OperationDTO();
                BeanUtil.copyProperties(x, operationDTO);
                operationDTO.setRowId(x.getId());
                operationDTO.setCusTypeName(subCode);
                return operationDTO;
            }).collect(Collectors.toList());
            excelWriter.write(collect, build1);


            List<Long> idList = chaOperationListVOS.stream().map(ChaOperationListVO::getId).collect(Collectors.toList());
            List<ChaOperationDetailsVO> chaOperationDetailsVOList = iChaOperationDetailsService.queryDetailByOpeIdList(idList);
            List<ChaOperationDetailsDTO> chaOperationDetailsDTOList = chaOperationDetailsVOList.stream().map(x -> {
                ChaOperationDetailsDTO chaOperationDetailsDTO = new ChaOperationDetailsDTO();
                BeanUtils.copyProperties(x, chaOperationDetailsDTO);
                chaOperationDetailsDTO.setRowId(x.getOperationId());
                return chaOperationDetailsDTO;
            }).collect(Collectors.toList());
            excelWriter.write(chaOperationDetailsDTOList, build2);

            excelWriter.finish();
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (null != excelWriter)
                excelWriter.finish();
        }
    }

    @Resource
    private IRemoteApi iRemoteApi;

    @SneakyThrows
    @PreAuthorize("@ss.hasPermi('Operation:Operation:delOutboundCharge')")
    @ApiOperation(value = "业务计费 - 导入")
    @PostMapping("/upload")
    public R upload(@RequestPart("file") MultipartFile multipartFile) {
        ExcelReader excelReader = null;
        try (InputStream inputStream = multipartFile.getInputStream()) {
            excelReader = EasyExcel.read(inputStream).readCache(new Ehcache(5)).build();

            AnalysisListenerAbstract<OperationDTO> listener0 = new AnalysisListenerAbstract<>();
            ReadSheet readSheet = EasyExcel.readSheet(0).head(OperationDTO.class)
                    .registerConverter(new LocalDateTimeConvert()).registerReadListener(listener0).build();
            AnalysisListenerAbstract<ChaOperationDetailsDTO> listener1 = new AnalysisListenerAbstract<>();

            ReadSheet readSheet1 = EasyExcel.readSheet(1).head(ChaOperationDetailsDTO.class).registerReadListener(listener1).build();

            excelReader.read(readSheet, readSheet1);
            excelReader.finish();
            List<OperationDTO> operationDTOList = listener0.getResultList();
            List<ChaOperationDetailsDTO> chaOperationDetailsDTOList = listener1.getResultList();
            Map<Long, List<ChaOperationDetailsDTO>> detailMap = chaOperationDetailsDTOList.stream().collect(Collectors.groupingBy(ChaOperationDetailsDTO::getRowId));
            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
            StringBuilder errorMsg = new StringBuilder();
            AtomicInteger index = new AtomicInteger(1);

            operationDTOList.forEach(x -> {
                x.setCusTypeCode(iRemoteApi.getSubNameBySubCode("098", x.getCusTypeCode()));
                x.setOperationType(DelOutboundOrderEnum.getCode(x.getOperationTypeName()));
                x.setOrderType(OrderTypeEnum.getEn(x.getOrderTypeName()));
                // 设置替换参数
                int indexThis = index.getAndIncrement();
                x.setChaOperationDetailList(detailMap.get(x.getRowId()));
                Set<ConstraintViolation<OperationDTO>> validate = validator.validate(x, Default.class);
                String error = validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(","));
                if (StringUtils.isNotBlank(error)) {
                    errorMsg.append(String.format("请检查第%s条数据:%s\r", indexThis, error));
                    return;
                }
                try {
                    this.save(x);
                } catch (Exception e) {
                    e.printStackTrace();
                    String message = e.getMessage();
                    errorMsg.append(String.format("第%s条数据业务异常:%s\r", indexThis, message));
                }
            });
            AssertUtil.isTrue(StringUtils.isBlank(errorMsg.toString()), errorMsg.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (null != excelReader)
                excelReader.finish();
        }
        return R.ok();
    }

}
