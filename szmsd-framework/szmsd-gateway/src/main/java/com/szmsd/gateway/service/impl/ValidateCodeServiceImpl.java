package com.szmsd.gateway.service.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import com.szmsd.common.core.constant.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;

import com.google.code.kaptcha.Producer;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.utils.IdUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.sign.Base64;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.redis.service.RedisService;
import com.szmsd.gateway.service.ValidateCodeService;

/**
 * 验证码实现处理
 *
 * @author szmsd
 */
@Service
public class ValidateCodeServiceImpl implements ValidateCodeService {
    @Resource
    private Producer producer;

    @Resource
    private RedisService redisService;

    /**
     * 生成验证码
     */
    @Override
    public R createCapcha() throws Exception {
        // 生成验证码
        String capText = producer.createText();
        String capStr = capText.substring(0, capText.lastIndexOf("@"));
        String verifyCode = capText.substring(capText.lastIndexOf("@") + 1);
        BufferedImage image = producer.createImage(capStr);
        // 保存验证码信息
        String uuid = IdUtils.simpleUUID();
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;

        redisService.setCacheObject(verifyKey, verifyCode, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            return R.failed(e.getMessage());
        }
        R r = new R();
        Map map = new HashMap<>();
        map.put("uuid", uuid);
        map.put("img", Base64.encode(os.toByteArray()));
        r.setCode(HttpStatus.SUCCESS);
        r.setMsg("success");
        r.setData(map);
        return r;
    }

    /**
     * 校验验证码
     */
    @Override
    public void checkCapcha(String code, String uuid) throws Exception {
        /*if (StringUtils.isEmpty(code)) {
            throw new Exception("验证码不能为空");
        }
        if (StringUtils.isEmpty(uuid)) {
            throw new Exception("验证码已失效");
        }
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        String captcha = redisService.getCacheObject(verifyKey);


        redisService.deleteObject(verifyKey);

        if (!code.equalsIgnoreCase(captcha)) {
            throw new Exception("验证码错误");
        }*/
    }

}
