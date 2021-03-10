package com.szmsd.open.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.szmsd.open.event.EventUtil;
import com.szmsd.open.event.TransactionEvent;
import com.szmsd.open.filter.RequestLogFilterContext;
import com.szmsd.open.service.IOpnTransactionService;
import com.szmsd.open.vo.ResponseVO;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangyuyuan
 * @date 2021-03-08 9:28
 */
@Component
public class TransactionHandlerInterceptor implements HandlerInterceptor, Ordered {
    private final Logger logger = LoggerFactory.getLogger(TransactionHandlerInterceptor.class);

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private IOpnTransactionService opnTransactionService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        RequestLogFilterContext currentContext = RequestLogFilterContext.getCurrentContext();
        if (StringUtils.isNotEmpty(currentContext.getRequestUri()) && StringUtils.isNotEmpty(currentContext.getTransactionId())) {
            String key = applicationName + ":transaction:" + builderOnlyKey(currentContext);
            RLock lock = redissonClient.getLock(key);
            long time = 5;
            TimeUnit timeUnit = TimeUnit.SECONDS;
            // 默认允许
            int t = 0;
            try {
                // 获取锁
                if (lock.tryLock(time, timeUnit)) {
                    // 验证有没有REP记录
                    if (this.opnTransactionService.hasRep(currentContext.getRequestUri(), currentContext.getTransactionId())) {
                        // 存在记录
                        t = 1;
                    } else {
                        // 新增记录
                        this.opnTransactionService.add(currentContext.getRequestId(), currentContext.getRequestUri(), currentContext.getTransactionId());
                    }
                }
            } catch (InterruptedException e) {
                logger.error(e.getLocalizedMessage(), e);
                // 操作失败
                t = 2;
            } finally {
                lock.unlock();
            }
            // 处理结果
            if (0 == t) {
                return true;
            } else {
                ResponseVO r;
                if (1 == t) {
                    // 返回成功
                    r = ResponseVO.ok();
                } else {
                    // 返回失败
                    r = ResponseVO.failed("执行失败，请重新再试");
                }
                response.reset();
                response.setCharacterEncoding(RequestConstant.ENCODING);
                response.setContentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8");
                PrintWriter pw = response.getWriter();
                pw.write(JSONObject.toJSONString(r, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue));
                pw.flush();
                pw.close();
                return false;
            }
        }
        // 上下文信息不存在，先不处理
        return true;
    }

    private String builderOnlyKey(RequestLogFilterContext currentContext) {
        return Base64.getEncoder().encodeToString(currentContext.getRequestUri().getBytes(StandardCharsets.UTF_8)) + "_" + currentContext.getTransactionId();
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, ModelAndView modelAndView) throws Exception {
        // 业务异常不走这个
        RequestLogFilterContext currentContext = RequestLogFilterContext.getCurrentContext();
        if (null != currentContext.getRequestId()) {
            EventUtil.publishEvent(new TransactionEvent(currentContext.getRequestId()));
        }
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) throws Exception {
    }

    @Override
    public int getOrder() {
        return 2000;
    }
}
