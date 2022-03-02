package com.szmsd.chargerules.controller;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.chargerules.dto.CreateProductDTO;
import com.szmsd.chargerules.dto.FreightCalculationDTO;
import com.szmsd.chargerules.dto.PricedProductQueryDTO;
import com.szmsd.chargerules.dto.UpdateProductDTO;
import com.szmsd.chargerules.service.IPricedProductService;
import com.szmsd.chargerules.vo.FreightCalculationVO;
import com.szmsd.chargerules.vo.PricedProductInfoVO;
import com.szmsd.chargerules.vo.PricedServiceListVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.utils.HttpClientHelper;
import com.szmsd.common.core.utils.HttpResponseBody;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.plugin.annotation.AutoValue;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.vo.KeyValuePair;
import com.szmsd.http.vo.PricedProduct;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Api(tags = {"PricedProduct"})
@RestController
@RequestMapping("/products")
public class PricedProductController extends BaseController {

    @Resource
    private IPricedProductService iPricedProductService;

    @Autowired
    private RedisTemplate redisTemplate;

    @AutoValue
    @PreAuthorize("@ss.hasPermi('products:pricedproducts')")
    @PostMapping("/freightCalculation")
    @ApiOperation(value = "运费测算 - 根据包裹基本信息获取可下单报价产品")
    public R<List<FreightCalculationVO>> freightCalculation(@RequestBody FreightCalculationDTO freightCalculationDTO) {
        List<FreightCalculationVO> directServiceFeeData = iPricedProductService.pricedProducts(freightCalculationDTO);
        return R.ok(directServiceFeeData);
    }

    @PreAuthorize("@ss.hasPermi('products:page')")
    @GetMapping("/page")
    @ApiOperation(value = "分页查询产品列表，返回指定页面的数据，以及统计总记录数")
    public TableDataInfo<PricedProduct> page(PricedProductQueryDTO pricedProductQueryDTO) {
        return iPricedProductService.selectPage(pricedProductQueryDTO);
    }

    @PreAuthorize("@ss.hasPermi('products:keyvaluepairs')")
    @GetMapping("/keyValuePairs")
    @ApiOperation(value = "查询产品下拉列表，返回list数据")
    public R<List<KeyValuePair>> keyValuePairs() {
        List<KeyValuePair> directServiceFeeData = iPricedProductService.keyValuePairs();
        return R.ok(directServiceFeeData);
    }

    @PreAuthorize("@ss.hasPermi('products:create')")
    @PostMapping("/create")
    @ApiOperation(value = "创建报价产品信息")
    public R create(@RequestBody CreateProductDTO createProductDTO) {
        iPricedProductService.create(createProductDTO);
        return R.ok();
    }

    @GetMapping("/info/{productCode}")
    @ApiOperation(value = "根据产品代码获取计价产品信息")
    public R<PricedProductInfoVO> info(@PathVariable("productCode") String productCode) {
        PricedProductInfoVO pricedProductInfoVO = iPricedProductService.getInfo(productCode);
        String type = pricedProductInfoVO.getSupplierCalcType();
        List<PricedServiceListVO> list = new ArrayList<>();
        if ("005001".equals(type)) {
            //供应商成本价格
            list = this.queryServiceList().getData();
        } else {
            list = this.queryRoutesList().getData();
        }
        pricedProductInfoVO.processVO(list);
        return R.ok(pricedProductInfoVO);
    }

    @PreAuthorize("@ss.hasPermi('products:update')")
    @PutMapping("/update")
    @ApiOperation(value = "修改报价产品信息")
    public R update(@RequestBody UpdateProductDTO updateProductDTO) {
        updateProductDTO.processDTO();
        iPricedProductService.update(updateProductDTO);
        return R.ok();
    }

    @PostMapping("/exportFile")
    @ApiOperation(value = "导出产品信息列表")
    public void exportFile(HttpServletResponse response, @RequestBody List<String> codes) {
        FileStream fileStream = iPricedProductService.exportFile(codes);
        super.fileStreamWrite(response, fileStream);
    }

    @ApiOperation(value = "获取PRC系统已定义的物流运输商的基础信息列表")
    @GetMapping("/getCarriers")
    public R getCarriers() {
        ValueOperations operations = redisTemplate.opsForValue();
        final String KEY = "PRC:CARRIERS";
        Object result = operations.get(KEY);
        if (result != null) {
            return R.ok(JSONArray.parseArray(result.toString()));
        }
        String requestUrl = "https://open-api.trackingyee.com/tracking/v1/carriers";
        HttpResponseBody responseBody = HttpClientHelper.httpGet(requestUrl, null, new HashMap<String, String>());
        String body = responseBody.getBody();
        JSONObject resultObj = JSONObject.parseObject(body);
        if ("OK".equalsIgnoreCase(resultObj.getString("status"))) {
            JSONArray jsonArray = resultObj.getJSONArray("data");
            operations.set(KEY, jsonArray.toJSONString(), 1, TimeUnit.DAYS);
            return R.ok(jsonArray);
        }
        return R.failed("查询异常！");
    }

    private TimedCache<String, List<PricedServiceListVO>> CACHE_SERVICE = CacheUtil.newWeakCache(DateUnit.HOUR.getMillis() * 4);

    @ApiOperation(value = "获取所有物流服务的API", notes = "展示ID Name 保存时展示id 成本价格调用改接口")
    @GetMapping("/services/list")
    public R<List<PricedServiceListVO>> queryServiceList() {
        List<PricedServiceListVO> cacheService = CACHE_SERVICE.get("CACHE_SERVICE");
        if (CollectionUtils.isNotEmpty(cacheService)) return R.ok(cacheService);

        String requestUrl = "https://dmsrm-api.dsloco.com/api/services/list";
        HttpResponseBody responseBody = HttpClientHelper.httpGet(requestUrl, null, new HashMap<String, String>());
        String body = responseBody.getBody();
        JSONObject resultObj = JSONObject.parseObject(body);
        JSONArray resultJson = resultObj.getJSONArray("Data");
        if (resultJson != null) {
            List<PricedServiceListVO> data = JSONObject.parseArray(resultJson.toJSONString(), PricedServiceListVO.class);
            CACHE_SERVICE.put("CACHE_SERVICE", data);
            return R.ok(data);
        }
        return R.failed("查询异常！");
    }

    @ApiOperation(value = "获取路由数据", notes = "展示ID Name 保存时展示id 成本路由调用该接口")
    @GetMapping("/routes")
    public R<List<PricedServiceListVO>> queryRoutesList() {
        List<PricedServiceListVO> cacheService = CACHE_SERVICE.get("CACHE_ROUTES");
        if (CollectionUtils.isNotEmpty(cacheService)) return R.ok(cacheService);

        String requestUrl = "https://dmsrm-api.dsloco.com/api/routes";
        HttpResponseBody responseBody = HttpClientHelper.httpGet(requestUrl, null, new HashMap<String, String>());
        String body = responseBody.getBody();
        JSONObject resultObj = JSONObject.parseObject(body);
        JSONArray resultJson = resultObj.getJSONArray("Data");
        if (resultJson != null) {
            List<PricedServiceListVO> data = JSONObject.parseArray(resultJson.toJSONString(), PricedServiceListVO.class);
            CACHE_SERVICE.put("CACHE_ROUTES", data);
            return R.ok(data);
        }
        return R.failed("查询异常！");
    }

}
