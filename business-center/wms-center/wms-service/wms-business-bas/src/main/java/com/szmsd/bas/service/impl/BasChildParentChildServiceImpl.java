package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.domain.BasChildParentChild;
import com.szmsd.bas.domain.BasCk1ShopifyWebhooksLog;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.enums.ChildParentStateEnum;
import com.szmsd.bas.mapper.BasChildParentChildMapper;
import com.szmsd.bas.mapper.BasCk1ShopifyWebhooksLogMapper;
import com.szmsd.bas.service.IBasChildParentChildService;
import com.szmsd.bas.service.IBasCk1ShopifyWebhooksLogService;
import com.szmsd.bas.service.IBasSellerService;
import com.szmsd.bas.vo.BasChildParentChildQueryVO;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.utils.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 子母单
 *
 * @author: taoJie
 * @since: 2022-07-13
 */
@Service
public class BasChildParentChildServiceImpl extends ServiceImpl<BasChildParentChildMapper, BasChildParentChild> implements IBasChildParentChildService {

    @Autowired
    private IBasSellerService basSellerService;

    @Override
    public BasSeller detail(BasChildParentChildQueryVO queryVO) {
        String sellerCode = queryVO.getParentSellerCode();
        if (StringUtils.isEmpty(sellerCode)) {
            throw new BaseException("客户代码不能为空");
        }
        BasSeller seller = basSellerService.lambdaQuery().eq(BasSeller::getSellerCode, sellerCode).last("limit 1").one();
        if (Objects.isNull(seller)) {
            throw new BaseException("该客户代码不存在");
        }
        BasChildParentChildQueryVO detailVO = new BasChildParentChildQueryVO();
        detailVO.setChildParentStatus("2");
        detailVO.setParentSellerCode(sellerCode);
        List<BasChildParentChild> basChildParentChildren = baseMapper.pageList(detailVO);
        seller.setChildList(basChildParentChildren);
        return seller;
    }

    @Override
    public List<BasChildParentChild> pageList(BasChildParentChildQueryVO queryVo) {
        return baseMapper.pageList(queryVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean submit(BasChildParentChild basChildParentChild) {
        // 再次验证
        BasChildParentChildQueryVO selectVO = new BasChildParentChildQueryVO();
        selectVO.setSellerCode(basChildParentChild.getSellerCode());
        selectVO.setParentSellerCode(basChildParentChild.getParentSellerCode());
        BasSeller basSeller = sellerAdd(selectVO);
        basSellerService.lambdaUpdate().eq(BasSeller::getSellerCode, basChildParentChild.getSellerCode()).set(BasSeller::getChildParentStatus, "2").update();
        basSellerService.lambdaUpdate().eq(BasSeller::getSellerCode, basChildParentChild.getParentSellerCode()).set(BasSeller::getChildParentStatus, "1").update();
        basChildParentChild.setState(ChildParentStateEnum.reviewing.getKey());
        boolean save = saveOrUpdate(basChildParentChild);
        return save;
    }

    @Override
    public BasSeller sellerAdd(BasChildParentChildQueryVO basSeller) {
        String sellerCode = basSeller.getSellerCode();
        if (StringUtils.isEmpty(sellerCode)) {
            throw new BaseException("客户代码不能为空");
        }
        BasChildParentChild one = lambdaQuery().eq(BasChildParentChild::getSellerCode, sellerCode).ne(BasChildParentChild::getParentSellerCode, basSeller.getParentSellerCode()).last("limit 1").one();
        if (Objects.nonNull(one)) {
            throw new BaseException("该客户代码已有关联");
        }
        BasSeller seller = basSellerService.lambdaQuery().eq(BasSeller::getSellerCode, sellerCode).last("limit 1").one();
        if (Objects.isNull(seller)) {
            throw new BaseException("该客户代码不存在");
        }
        return seller;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean dealOperation(BasChildParentChild basChildParentChild) {
        String state = basChildParentChild.getState();
        boolean result = false;
        LambdaUpdateChainWrapper<BasChildParentChild> updateChainWrapper = lambdaUpdate().eq(BasChildParentChild::getSellerCode, basChildParentChild.getSellerCode());
        if (Objects.equals(state, ChildParentStateEnum.unbind.getKey())) {
            int count = lambdaQuery().eq(BasChildParentChild::getParentSellerCode, basChildParentChild.getParentSellerCode()).count();
            basSellerService.lambdaUpdate().eq(BasSeller::getSellerCode, basChildParentChild.getSellerCode()).set(BasSeller::getChildParentStatus, 0).update();
            result = updateChainWrapper.remove();
            if (Objects.equals(count, 1)) {
                result = basSellerService.lambdaUpdate().eq(BasSeller::getSellerCode, basChildParentChild.getParentSellerCode()).set(BasSeller::getChildParentStatus, 0).update();
            }
        } else {
            result = updateChainWrapper.set(BasChildParentChild::getState, state).update();
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean submitList(BasSeller basSeller) {
        String sellerCode = basSeller.getSellerCode();

        // 修改客户为主
        basSellerService.lambdaUpdate().eq(BasSeller::getSellerCode, sellerCode).set(BasSeller::getChildParentStatus, "1").update();
        List<BasChildParentChild> childList = basSeller.getChildList();
        if (CollectionUtils.isNotEmpty(childList)) {
            childList.stream().forEach(item -> {
                item.setState(ChildParentStateEnum.reviewing.getKey());
            });
            saveBatch(childList);
        }
        return true;
    }
}

