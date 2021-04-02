package com.szmsd.returnex.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.returnex.config.BeanCopyUtil;
import com.szmsd.returnex.domain.ReturnExpressGood;
import com.szmsd.returnex.dto.ReturnExpressGoodAddDTO;
import com.szmsd.returnex.mapper.ReturnExpressGoodMapper;
import com.szmsd.returnex.service.IReturnExpressGoodService;
import com.szmsd.returnex.vo.ReturnExpressGoodVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * return_express - 退货单sku详情表 服务实现类
 * </p>
 *
 * @author 11
 * @since 2021-03-29
 */
@Slf4j
@Service
public class ReturnExpressGoodServiceImpl extends ServiceImpl<ReturnExpressGoodMapper, ReturnExpressGood> implements IReturnExpressGoodService {

    @Resource
    private ReturnExpressGoodMapper returnExpressGoodMapper;

    /**
     * 根据退件单id查询关联的商品列表
     *
     * @param id
     * @return
     */
    @Override
    public List<ReturnExpressGoodVO> queryGoodListByExId(@NotNull Integer id) {
        AssertUtil.isTrue(null != id && id > 0, "数据异常！");
        List<ReturnExpressGood> returnExpressGoods = returnExpressGoodMapper.selectList(Wrappers.<ReturnExpressGood>lambdaQuery()
                .eq(ReturnExpressGood::getAssociationId, id)
                .eq(ReturnExpressGood::getDelFlag, 0)
        );
        return BeanCopyUtil.copyListProperties(returnExpressGoods, ReturnExpressGoodVO::new);
    }


    /**
     * 批量 新增/修改 商品信息
     *
     * @param addOrUpdateList 商品列表
     * @param batchId         关联的退件单id
     */
    @Override
    public void addOrUpdateGoodInfoBatch(List<ReturnExpressGoodAddDTO> addOrUpdateList, Integer batchId) {
        if (CollectionUtils.isEmpty(addOrUpdateList)) {
            log.info("无需要新增/修改的商品数据");
            return;
        }
        addOrUpdateList.forEach(x -> x.setAssociationId(batchId));
        List<ReturnExpressGood> returnExpressGoods = BeanCopyUtil.copyListProperties(addOrUpdateList, ReturnExpressGood::new);
        saveOrUpdateBatch(returnExpressGoods);
    }
}

