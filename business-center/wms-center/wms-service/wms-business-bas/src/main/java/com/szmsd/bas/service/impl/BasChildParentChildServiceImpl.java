package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.domain.BasChildParentChild;
import com.szmsd.bas.domain.BasChildParentLog;
import com.szmsd.bas.domain.BasCk1ShopifyWebhooksLog;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.enums.ChildParentStateEnum;
import com.szmsd.bas.mapper.BasChildParentChildMapper;
import com.szmsd.bas.mapper.BasCk1ShopifyWebhooksLogMapper;
import com.szmsd.bas.service.IBasChildParentChildService;
import com.szmsd.bas.service.IBasChildParentLogService;
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

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private IBasChildParentLogService basChildParentLogService;

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
        if (CollectionUtils.isNotEmpty(basChildParentChildren)) {
            seller.setChildList(basChildParentChildren);
            seller.setApplyName(basChildParentChildren.get(0).getApplyName());
            seller.setApplyTime(basChildParentChildren.get(0).getApplyTime());
        }

        return seller;
    }

    @Override
    public List<BasChildParentChild> pageList(BasChildParentChildQueryVO queryVo) {
        List<BasChildParentChild> basChildParentChildren = baseMapper.pageList(queryVo);
        if (queryVo.getChildParentStatus().equals("1")) {
            if (CollectionUtils.isNotEmpty(basChildParentChildren)) {

                List<String> sellerCodeList = basChildParentChildren.stream().map(BasChildParentChild::getParentSellerCode).collect(Collectors.toList());
                List<BasChildParentChild> list = lambdaQuery().in(BasChildParentChild::getParentSellerCode, sellerCodeList).list();
                Map<String, List<BasChildParentChild>> parentMap = list.stream().collect(Collectors.groupingBy(BasChildParentChild::getParentSellerCode));
                basChildParentChildren.stream().forEach(item -> {
                    List<BasChildParentChild> childList = parentMap.get(item.getParentSellerCode());
                    if (CollectionUtils.isNotEmpty(childList)) {
                        item.setChildCodes(childList.stream().map(BasChildParentChild::getSellerCode).collect(Collectors.joining(",")));
                    }
                });
            }

        }
        return basChildParentChildren;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean submit(BasChildParentChild basChildParentChild) {
        // 再次验证
        BasChildParentChildQueryVO selectVO = new BasChildParentChildQueryVO();
        selectVO.setSellerCode(basChildParentChild.getSellerCode());
        selectVO.setId(basChildParentChild.getId());
        selectVO.setParentSellerCode(basChildParentChild.getParentSellerCode());
        BasSeller basSeller = sellerAdd(selectVO);
        basSellerService.lambdaUpdate().eq(BasSeller::getSellerCode, basChildParentChild.getSellerCode()).set(BasSeller::getChildParentStatus, "2").update();
        basSellerService.lambdaUpdate().eq(BasSeller::getSellerCode, basChildParentChild.getParentSellerCode()).set(BasSeller::getChildParentStatus, "1").update();
        basChildParentChild.setState(ChildParentStateEnum.reviewing.getKey());
        boolean save = saveOrUpdate(basChildParentChild);

        // 日志操作
        BasChildParentLog basChildParentLog = new BasChildParentLog();
        basChildParentLog.setParentSellerCode(basChildParentChild.getParentSellerCode());
        basChildParentLog.setSellerCode(basChildParentChild.getSellerCode());
        basChildParentLog.setState(ChildParentStateEnum.reviewing.getKey());
        basChildParentLogService.save(basChildParentLog);
        return save;
    }

    @Override
    public BasSeller sellerAdd(BasChildParentChildQueryVO basSeller) {
        String sellerCode = basSeller.getSellerCode();
        if (StringUtils.isEmpty(sellerCode)) {
            throw new BaseException("客户代码不能为空");
        }
        BasChildParentChild one = null;
        // 如果没有传主单号
        if (StringUtils.isEmpty(basSeller.getParentSellerCode())) {
            one = lambdaQuery().eq(BasChildParentChild::getParentSellerCode, sellerCode).last("limit 1").one();
        } else {
            // 如果是编辑就查不是自己的子单
            if (Objects.nonNull(basSeller.getId())) {
                one = lambdaQuery().eq(BasChildParentChild::getSellerCode, sellerCode).ne(BasChildParentChild::getId, basSeller.getId()).last("limit 1").one();
            } else {
                one = lambdaQuery().eq(BasChildParentChild::getSellerCode, sellerCode).or().eq(BasChildParentChild::getParentSellerCode, sellerCode).last("limit 1").one();
            }
        }
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
        String sellerCode = basChildParentChild.getSellerCode();
        String parentSellerCode = basChildParentChild.getParentSellerCode();
        boolean result = false;

        // 日志操作
        BasChildParentLog basChildParentLog = new BasChildParentLog();
        basChildParentLog.setParentSellerCode(parentSellerCode);
        basChildParentLog.setSellerCode(sellerCode);
        basChildParentLog.setState(state);
        basChildParentLogService.save(basChildParentLog);


        LambdaUpdateChainWrapper<BasChildParentChild> updateChainWrapper = lambdaUpdate().eq(BasChildParentChild::getSellerCode, sellerCode);
        if (Objects.equals(state, ChildParentStateEnum.unbind.getKey()) || Objects.equals(state, ChildParentStateEnum.remove.getKey())) {
            int count = lambdaQuery().eq(BasChildParentChild::getParentSellerCode, parentSellerCode).count();
            basSellerService.lambdaUpdate().eq(BasSeller::getSellerCode, sellerCode).set(BasSeller::getChildParentStatus, 0).update();
            result = updateChainWrapper.remove();
            if (Objects.equals(count, 1)) {
                basChildParentLogService.lambdaUpdate().eq(BasChildParentLog::getParentSellerCode, parentSellerCode).remove();
                result = basSellerService.lambdaUpdate().eq(BasSeller::getSellerCode, parentSellerCode).set(BasSeller::getChildParentStatus, 0).update();
            }
        } else {
            result = updateChainWrapper.set(BasChildParentChild::getState, state).set(BasChildParentChild::getDealTime, new Date()).update();
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
            List<String> childCodes = childList.stream().map(BasChildParentChild::getSellerCode).collect(Collectors.toList());
            basSellerService.lambdaUpdate().in(BasSeller::getSellerCode, childCodes).set(BasSeller::getChildParentStatus, "2").update();
            childList.stream().forEach(item -> {
                item.setState(ChildParentStateEnum.reviewing.getKey());
                item.setParentSellerCode(sellerCode);
                item.setApplyName(basSeller.getApplyName());
                item.setApplyCode(basSeller.getApplyCode());
            });

            saveBatch(childList);

            // 日志操作
            List<BasChildParentLog> basChildParentLogList = childList.stream().map(item -> {
                BasChildParentLog basChildParentLog = new BasChildParentLog();
                basChildParentLog.setParentSellerCode(sellerCode);
                basChildParentLog.setSellerCode(item.getSellerCode());
                basChildParentLog.setState(ChildParentStateEnum.reviewing.getKey());
                return basChildParentLog;
            }).collect(Collectors.toList());
            basChildParentLogService.saveBatch(basChildParentLogList);
        }
        return true;
    }

    @Override
    public List<String> getChildCodeList(String sellerCode) {
        List<BasChildParentChild> list = lambdaQuery().eq(BasChildParentChild::getParentSellerCode, sellerCode).list();
        List<String> sellerCodeList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            sellerCodeList = list.stream().map(BasChildParentChild::getSellerCode).collect(Collectors.toList());
        }
        return sellerCodeList;
    }
}

