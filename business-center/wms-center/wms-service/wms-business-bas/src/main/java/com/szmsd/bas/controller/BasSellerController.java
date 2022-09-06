package com.szmsd.bas.controller;

import cn.hutool.json.JSONUtil;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.domain.BasSysOperationLog;
import com.szmsd.bas.dto.*;
import com.szmsd.bas.service.IBasSellerService;
import com.szmsd.bas.service.IBasSysOperationLogService;
import com.szmsd.bas.vo.BasSellerInfoVO;
import com.szmsd.bas.vo.BasSellerWrapVO;
import com.szmsd.common.core.constant.SecurityConstants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.system.api.feign.AuthClientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;


/**
* <p>
    *  前端控制器
    * </p>
*
* @author l
* @since 2021-03-09
*/


@Api(tags = {"卖家模块"})
@RestController
@RequestMapping("/bas/seller")
public class BasSellerController extends BaseController{

     @Autowired
     private IBasSellerService basSellerService;


    @Resource
    private AuthClientService authClient;
    @Resource
    private IBasSysOperationLogService basSysOperationLogService;
     /**
       * 查询模块列表
     */
      @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:list')")
      @PostMapping("/list")
      @ApiOperation(value = "分页查询模块列表",notes = "分页查询模块列表")
      public TableDataInfo list(@RequestBody BasSellerQueryDto basSeller)
     {
            return basSellerService.selectBasSellerList(basSeller);
      }

    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:list')")
    @GetMapping("/listSeller")
    @ApiOperation(value = "查询模块列表",notes = "查询模块列表")
    public R<List<BasSellerSysDto>> listSeller(BasSeller basSeller)
    {
        List<BasSellerSysDto> list = basSellerService.getBasSellerList(basSeller);
        return R.ok(list);
    }

    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:list')")
    @PostMapping("/getSellerCode")
    @ApiOperation(value = "查询模块列表",notes = "查询模块列表")
    public R<String> getSellerCode(@RequestBody BasSeller basSeller)
    {
        R r = new R();
        r.setCode(200);
        r.setData(basSellerService.getSellerCode(basSeller));
        r.setMsg("SUCCESS");
        return r;
    }

    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:list')")
    @PostMapping("/getLoginSellerCode")
    @ApiOperation(value = "查询模块列表",notes = "查询模块列表")
    public R<String> getLoginSellerCode()
    {
        R r = new R();
        r.setCode(200);
        r.setData(basSellerService.getLoginSellerCode());
        r.setMsg("SUCCESS");
        return r;
    }

    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:list')")
    @PostMapping("/getInspection")
    @ApiOperation(value = "查询验货要求",notes = "查询验货要求")
    public R<String[]> getInspection(@RequestBody String sellerCode)
    {
        R r = new R();
        r.setCode(200);
        r.setData(basSellerService.getInspection(sellerCode));
        r.setMsg("SUCCESS");
        return r;
    }


    /**
    * 导出模块列表
    */
     @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:export')")
     @Log(title = "模块", businessType = BusinessType.EXPORT)
     @GetMapping("/export")
     @ApiOperation(value = "导出模块列表",notes = "导出模块列表")
     public void export(HttpServletResponse response, BasSeller basSeller) throws IOException {
     /*List<BasSellerSysDto> list = basSellerService.selectBasSellerList(basSeller);
     ExcelUtil<BasSellerSysDto> util = new ExcelUtil<BasSellerSysDto>(BasSellerSysDto.class);
        util.exportExcel(response,list, "BasSeller");*/

     }

    /**
    * 获取模块详细信息
    */
    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:query')")
    @GetMapping(value = "getInfo/{userName}")
    @ApiOperation(value = "获取模块详细信息",notes = "获取模块详细信息")
    public R<BasSellerInfoVO> getInfo(@PathVariable("userName") String userName)
    {
    return R.ok(basSellerService.selectBasSeller(userName));
    }
    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:query')")
    @GetMapping(value = "getInfoBySellerCode/{sellerCode}")
    @ApiOperation(value = "获取模块详细信息",notes = "获取模块详细信息")
    public R<BasSellerInfoVO> getInfoBySellerCode(@PathVariable("sellerCode") String sellerCode)
    {
        return R.ok(basSellerService.selectBasSellerBySellerCode(sellerCode));
    }
    /**
    * 卖家注册模块 EmailCodeValid切面校验邮箱验证码
    */
    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("register")
    @ApiOperation(value = "注册模块",notes = "注册模块")
    public R<Boolean> register(HttpServletRequest request, @RequestBody BasSellerDto dto)
    {
    return basSellerService.insertBasSeller(request,dto);
    }

    @ApiOperation("获取验证码")
    @PostMapping("getCheckCode")
    public R getCheckCode(HttpServletRequest request) {
        return this.basSellerService.getCheckCode(request);
    }

    /**
    * 修改模块
    */
    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改模块",notes = "修改模块")
    public R edit(@RequestBody BasSellerInfoDto basSellerInfoDto) throws IllegalAccessException {
    return toOk(basSellerService.updateBasSeller(basSellerInfoDto));
    }

    /**
    * 删除模块
    */
    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @PutMapping("remove")
    @ApiOperation(value = "生效/失效模块",notes = "生效/失效模块")
    public R remove(@RequestBody ActiveDto activeDto) throws IllegalAccessException {
    return R.ok(basSellerService.deleteBasSellerByIds(activeDto));
    }

    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:queryByServiceCondition')")
    @PostMapping("/queryByServiceCondition")
    @ApiOperation(value = "查询业务经理/客服下所属的客户编码")
    public R<List<String>> queryByServiceCondition(@RequestBody ServiceConditionDto conditionDto) {
        return R.ok(this.basSellerService.queryByServiceCondition(conditionDto));
    }

    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:queryByServiceCondition')")
    @PostMapping("/queryAllSellerCodeAndEmail")
    @ApiOperation(value = "查询所有用户编码 和邮箱地址")
    public R<List<BasSellerEmailDto>> queryAllSellerCodeAndEmail() {
        return R.ok(this.basSellerService.queryAllSellerCodeAndEmail());
    }

    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:getRealState')")
    @PostMapping("/getRealState")
    @ApiOperation(value = "查询实名状态")
    public R<String> getRealState(@RequestBody String sellerCode) {
        return R.ok(this.basSellerService.getRealState(sellerCode));
    }
    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:getRealState')")
    @PostMapping("/queryCkPushFlag")
    @ApiOperation(value = "查询实名状态")
    public R<BasSellerWrapVO> queryCkPushFlag(@RequestBody String sellerCode) {
        return R.ok(this.basSellerService.queryCkPushFlag(sellerCode));
    }

    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:getRealState')")
    @GetMapping("/updateUserInfoForMan")
    @ApiOperation(value = "查询实名状态-更新用")
    public R<String> updateUserInfoForMan() {
        this.basSellerService.updateUserInfoForMan();
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('BasSeller:BasSeller:loginSellerSystem')")
    @PostMapping("/loginSellerSystem")
    @ApiOperation(value = "登录客户后台",notes = "登录客户后台")
    public R<Object> loginSellerSystem(@RequestBody BasSellerSysDto dto, HttpServletRequest request)
    {



        Object data = authClient.token(dto.getUserName(), "1", "01", "web", "password","123456", SecurityConstants.LOGIN_FREE);
        String ip = getRealIpAddress(request);
        BasSysOperationLog opnRequestLog = new BasSysOperationLog();
        opnRequestLog.setRequestUri("/bas/seller/loginSellerSystem");
        opnRequestLog.setRequestMethod("POST");
        opnRequestLog.setRequestHeader(ip);
        opnRequestLog.setRequestBody(JSONUtil.toJsonStr(dto));
        opnRequestLog.setRequestTime(new Date());
        opnRequestLog.setResponseBody(JSONUtil.toJsonStr(data));
        opnRequestLog.setResponseTime(new Date());
        basSysOperationLogService.insertBasSysOperationLog(opnRequestLog);

        return R.ok(data);
    }
    public static final String LOCAL_IP = "127.0.0.1";//本地ip地址
    public static final String DEFAULT_IP = "0:0:0:0:0:0:0:1";//默认ip地址
    public static final int DEFAULT_IP_LENGTH = 15;//默认ip地址长度
    /**
     * 获取真实ip地址，避免获取代理ip
     * @param request
     * @return
     */
    public static String getRealIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");//squid 服务代理
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");//apache服务代理
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");//weblogic 代理
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");//有些代理
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP"); //nginx代理
        }

        /*
         * 如果此时还是获取不到ip地址，那么最后就使用request.getRemoteAddr()来获取
         * */
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if(StringUtils.equals(ip,LOCAL_IP) || StringUtils.equals(ip,DEFAULT_IP)){
                //根据网卡取本机配置的IP
                InetAddress iNet = null;
                try {
                    iNet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    log.error("InetAddress getLocalHost error In HttpUtils getRealIpAddress: " ,e);
                }
                ip= iNet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        //"***.***.***.***".length() = 15
        if(!StringUtils.isEmpty(ip) && ip.length()> DEFAULT_IP_LENGTH){
            if(ip.indexOf(",") > 0){
                ip = ip.substring(0,ip.indexOf(","));
            }
        }
        return ip;
    }
}
