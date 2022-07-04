package com.szmsd.delivery.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.bas.api.client.BasSubClientService;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.plugin.annotation.AutoValue;
import com.szmsd.delivery.domain.DelOutboundAddress;
import com.szmsd.delivery.domain.DelTrack;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.event.ChangeDelOutboundLatestTrackEvent;
import com.szmsd.delivery.imported.DefaultAnalysisEventListener;
import com.szmsd.delivery.imported.EasyExcelFactoryUtil;
import com.szmsd.delivery.imported.ImportMessage;
import com.szmsd.delivery.imported.ImportResult;
import com.szmsd.delivery.service.IDelOutboundAddressService;
import com.szmsd.delivery.service.IDelTrackService;
import com.szmsd.delivery.util.SHA256Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author YM
 * @since 2022-02-10
 */

@Slf4j
@Api(tags = {""})
@RestController
@RequestMapping("/del-track")
public class DelTrackController extends BaseController {

    @Resource
    private IDelTrackService delTrackService;

    @Value("${webhook.secret}")
    private String webhookSecret;

    @Resource
    private ApplicationContext applicationContext;

    @Autowired
    private BasSubClientService basSubClientService;

    @Autowired
    private IDelOutboundAddressService delOutboundAddressService;

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('DelTrack:DelTrack:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询模块列表", notes = "查询模块列表")
    @AutoValue
    public TableDataInfo list(DelTrack delTrack) {
        startPage();
        List<DelTrack> list = delTrackService.selectDelTrackList(delTrack);
        return getDataTable(list);
    }

    @PostMapping("/commonTrackList")
    @ApiOperation(value = "查询模块列表", notes = "查询模块列表")
    @AutoValue
    public R<DelTrackMainCommonDto> commonTrackList(@RequestBody List<String> orderNos) {
        Map<String, List<BasSubWrapperVO>> listMap = this.basSubClientService.getSub("099");
        List<BasSubWrapperVO> delTrackStateTypeList = listMap.get("099");
        Map<String, BasSubWrapperVO> delTrackStateTypeMap
                = delTrackStateTypeList.stream().collect(Collectors.toMap(BasSubWrapperVO::getSubValue, Function.identity()));
        List<DelTrack> list = delTrackService.commonTrackList(orderNos);
        List<DelTrackCommonDto> newList = BeanMapperUtil.mapList(list, DelTrackCommonDto.class);
        for (DelTrackCommonDto dto: newList){
            BasSubWrapperVO vo  = delTrackStateTypeMap.get(dto.getTrackingStatus());
            if (vo != null) {
                dto.setTrackingStatusName(vo.getSubName());
            }
        }

        Set<String> threeSet = new TreeSet<String>();
        for(DelTrack delTrack: list){
            threeSet.add(delTrack.getOrderNo());
        }
        orderNos.clear();
        orderNos.addAll(threeSet);


        //处理轨迹状态数量
        java.util.Map<String, List<DelTrackCommonDto>> groupBy = newList.stream().collect(Collectors.groupingBy(DelTrackCommonDto::getOrderNo));
        Map<String, Integer> delTrackStateDto = new HashMap();
        for (String ordersNo: orderNos){
            List<DelTrackCommonDto> detailList = groupBy.get(ordersNo);
            if(detailList != null){
                String trackingStatus = detailList.get(0).getTrackingStatus();
                if(delTrackStateDto.containsKey(trackingStatus)){
                    delTrackStateDto.put(trackingStatus, delTrackStateDto.get(trackingStatus) + 1);
                }else{
                    delTrackStateDto.put(trackingStatus, 1);
                }
            }
        }

        //封装主表
        List<DelTrackDetailDto> mainDetailDataList = new ArrayList();
        for (String ordersNo: orderNos){
            List<DelTrackCommonDto> detailList = groupBy.get(ordersNo);
            if(detailList != null){
                DelTrackDetailDto detailDto = new DelTrackDetailDto();
                mainDetailDataList.add(detailDto);
                BeanUtils.copyProperties(detailList.get(0), detailDto);
                detailDto.setTrackingList(detailList);

                //计算每一条数据轨迹天数
                long day = 0;
                if(detailDto.getTrackingTime() != null && detailList.get(detailList.size() - 1).getTrackingTime() != null){
                    day = DateUtil.betweenDay(detailDto.getTrackingTime(), detailList.get(detailList.size() - 1).getTrackingTime(),  true);
                    if(day < 0){
                        day = 0;
                    }
                }

                detailDto.setTrackDays(day);

            }
        }

        //地址信息处理
        List<String> orders = mainDetailDataList.stream().map(e -> e.getOrderNo()).collect(Collectors.toList());
        if(orders.size() > 0){
            LambdaQueryWrapper<DelOutboundAddress> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.in(DelOutboundAddress::getOrderNo, orders);
            List<DelOutboundAddress> addressList = delOutboundAddressService.list(queryWrapper);
            java.util.Map<String, DelOutboundAddress> addressMap =
                    addressList.stream().collect(Collectors.toMap(DelOutboundAddress::getOrderNo, account -> account));
            for (DelTrackDetailDto dto: mainDetailDataList){
                DelOutboundAddress address = addressMap.get(dto.getOrderNo());
                if(address != null){
                    BeanUtils.copyProperties(address, dto);
                }
            }

        }


        DelTrackMainCommonDto mainDto = new DelTrackMainCommonDto();
        mainDto.setDelTrackStateDto(delTrackStateDto);
        mainDto.setTrackingList(mainDetailDataList);
        mainDto.setDelTrackStateTypeList(delTrackStateTypeList);
        return R.ok(mainDto);
    }

    /**
     * 导出模块列表
     */
    @PreAuthorize("@ss.hasPermi('DelTrack:DelTrack:export')")
    @Log(title = "模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出模块列表", notes = "导出模块列表")
    public void export(HttpServletResponse response, DelTrack delTrack) throws IOException {
        List<DelTrack> list = delTrackService.selectDelTrackList(delTrack);
        ExcelUtil<DelTrack> util = new ExcelUtil<DelTrack>(DelTrack.class);
        util.exportExcel(response, list, "DelTrack");

    }

    @PreAuthorize("@ss.hasPermi('DelTrack:DelTrack:importTrackExcelTemplate')")
    @GetMapping("/importTrackExcelTemplate")
    @ApiOperation(value = "下载导入模板", position = 100)
    public void collectionExportTemplate(HttpServletResponse response) {
        String filePath = "/template/DM_track.xlsx";
        String fileName = "轨迹导入";
        this.downloadTemplate(response, filePath, fileName, "xlsx");
    }

    @Log(title = "模块", businessType = BusinessType.IMPORT)
    @ApiOperation(value = "导入", notes = "导入")
    @PostMapping("/importTrack")
    public R<ImportResult> importTrack(MultipartFile file){
        AssertUtil.notNull(file, "上传文件不存在");
        try {
            byte[] byteArray = IOUtils.toByteArray(file.getInputStream());
            DefaultAnalysisEventListener<ImportTrackDto> defaultAnalysisEventListener = EasyExcelFactoryUtil.read(new ByteArrayInputStream(byteArray), ImportTrackDto.class, 0, 1);
            if (defaultAnalysisEventListener.isError()) {
                return R.ok(ImportResult.buildFail(defaultAnalysisEventListener.getMessageList()));
            }
            List<ImportTrackDto> dataList = defaultAnalysisEventListener.getList();
            if (CollectionUtils.isEmpty(dataList)) {
                return R.ok(ImportResult.buildFail(ImportMessage.build("导入数据不能为空")));
            }
            List<ImportMessage> messageList = new ArrayList<>();
            List<DelTrack> tracks = BeanMapperUtil.mapList(dataList, DelTrack.class);
            int i = 1;
            for(ImportTrackDto track : dataList){
                if (StringUtils.isBlank(track.getOrderNo())){
                    messageList.add(new ImportMessage(i, 1, null ,"订单号不能为空" ));
                }
                if (StringUtils.isBlank(track.getTrackingNo())){
                    messageList.add(new ImportMessage(i, 2, null ,"物流跟踪号不能为空" ));
                }
                if (StringUtils.isBlank(track.getTrackingStatus())){
                    messageList.add(new ImportMessage(i, 1, null ,"轨迹状态不能为空" ));
                }
                i++;

            }
            if (CollectionUtils.isNotEmpty(messageList)) {
                return R.ok(ImportResult.buildFail(messageList));
            }
            tracks.forEach(track -> {
                track.setSource("2");
                track.setTrackingTime(new Date());
                applicationContext.publishEvent(new ChangeDelOutboundLatestTrackEvent(track));
            });
            delTrackService.saveBatch(tracks);
            return R.ok();
        } catch (IOException e) {
            e.printStackTrace();
            return R.ok(ImportResult.buildFail(ImportMessage.build(e.getMessage())));
        }
    }

    @PreAuthorize("@ss.hasPermi('DelTrack:DelTrack:addOrUpdate')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("addOrUpdate")
    public R addOrUpdate(@RequestBody DelTrack delTrack){
        delTrack.setSource("2"); // 手动新增
        delTrack.setTrackingTime(new Date());
        delTrackService.saveOrUpdate(delTrack);
        applicationContext.publishEvent(new ChangeDelOutboundLatestTrackEvent(delTrack));
        return R.ok();
    }

    /**
     * 删除模块
     */
    @PreAuthorize("@ss.hasPermi('DelTrack:DelTrack:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除模块", notes = "删除模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(delTrackService.deleteDelTrackByIds(ids));
    }


    @ApiOperation(value = "用于接收TrackingYee回调的路由信息", notes = "")
    @PostMapping("/traceCallback")
    public R traceCallback(@RequestHeader(value = "trackingyee-webhook-signature") String trackingyeeSign,@RequestBody Object params){
        if(params == null) {
            return R.failed("非法请求，参数异常！");
        }
        // 验证签名
//        String trackingyeeSign = request.getHeader("trackingyee-webhook-signature");
        String requestStr = JSONObject.toJSONString(params, SerializerFeature.WriteMapNullValue);
        String verifySign = SHA256Util.getSHA256Str(webhookSecret + requestStr);
        log.info("trackingyeeSign: {}", trackingyeeSign);
        log.info("待加密验签字符串: {}", webhookSecret + requestStr);
        log.info("verifySign: {}", verifySign);
        TrackingYeeTraceDto trackingYeeTraceDto = JSONObject.parseObject(requestStr, TrackingYeeTraceDto.class);
        if (StringUtils.isBlank(trackingyeeSign) || !trackingyeeSign.equalsIgnoreCase(verifySign)) {
            return R.failed("非法请求，验签失败！");
        }
        if (trackingYeeTraceDto == null) {
            return R.failed("非法请求，参数异常！");
        }
        // 处理数据
        delTrackService.traceCallback(trackingYeeTraceDto);
        return R.ok();
    }

    @ApiOperation(value = "获取轨迹分析", notes = "获取轨迹分析")
    @GetMapping("getTrackAnalysis")
    public R getTrackAnalysis(TrackAnalysisRequestDto requestDto, @RequestHeader("langr") String langr){
        requestDto.setLang(langr);
        return R.ok(delTrackService.getTrackAnalysis(requestDto));
    }

    @ApiOperation(value = "获取轨迹状态下的各个发货服务订单量分析", notes = "获取轨迹状态下的各个发货服务订单量分析")
    @GetMapping("getProductAnalysis")
    public R getProductAnalysis(TrackAnalysisRequestDto requestDto){
        return R.ok(delTrackService.getProductServiceAnalysis(requestDto));
    }

    /**
     * 导出轨迹分析
     */
    @PreAuthorize("@ss.hasPermi('DelTrack:DelTrack:exportTrackAnalysis')")
    @Log(title = "模块", businessType = BusinessType.EXPORT)
    @GetMapping("/exportTrackAnalysis")
    @ApiOperation(value = "导出轨迹分析", notes = "导出轨迹分析")
    public void exportTrackAnalysis(HttpServletResponse response, TrackAnalysisRequestDto requestDto, @RequestHeader("langr") String langr) throws IOException {
        requestDto.setLang(langr);
        List<TrackAnalysisExportDto> list = delTrackService.getAnalysisExportData(requestDto);
        ExcelUtil<TrackAnalysisExportDto> util = new ExcelUtil<TrackAnalysisExportDto>(TrackAnalysisExportDto.class);
        util.exportExcel(response, list, "TrackAnalysis");
    }

    /**
     * 下载模板
     *
     * @param response response
     * @param filePath 文件存放路径，${server.tomcat.basedir}配置的目录和resources目录下
     * @param fileName 文件名称
     * @param ext      扩展名
     */
    private void downloadTemplate(HttpServletResponse response, String filePath, String fileName, String ext) {
        // 先去模板目录中获取模板
        // 模板目录中没有模板再从项目中获取模板
        String basedir = SpringUtils.getProperty("server.tomcat.basedir", "/u01/www/ck1/delivery");
        File file = new File(basedir + "/" + filePath);
        InputStream inputStream = null;
        ServletOutputStream outputStream = null;
        try {
            if (file.exists()) {
                inputStream = new FileInputStream(file);
                response.setHeader("File-Source", "local");
            } else {
                org.springframework.core.io.Resource resource = new ClassPathResource(filePath);
                inputStream = resource.getInputStream();
                response.setHeader("File-Source", "resource");
            }
            outputStream = response.getOutputStream();
            //response为HttpServletResponse对象
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            //Loading plan.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            String efn = URLEncoder.encode(fileName, "utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + efn + "." + ext);
            IOUtils.copy(inputStream, outputStream);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new CommonException("400", "文件不存在，" + e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new CommonException("500", "文件流处理失败，" + e.getMessage());
        } finally {
            IoUtil.flush(outputStream);
            IoUtil.close(outputStream);
            IoUtil.close(inputStream);
        }
    }
}
