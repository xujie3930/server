package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasChildParentChild;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.vo.BasChildParentChildQueryVO;

import java.util.List;


/**
 * 子母单
 *
 * @author: taoJie
 * @since: 2022-07-13
 */
public interface IBasChildParentChildService extends IService<BasChildParentChild> {

    /**
     * 详情
     *
     * @param queryVO
     * @return: BasSeller
     * @author: taoJie
     * @since: 2022-07-13
     */
    BasSeller detail(BasChildParentChildQueryVO queryVO);

    /**
     * 分页查询列表
     *
     * @param queryVo
     * @return: TableDataInfo
     * @author: taoJie
     * @since: 2022-07-13
     */
    List<BasChildParentChild> pageList(BasChildParentChildQueryVO queryVo);

    /**
     * 新增,更新
     *
     * @param basChildParentChild
     * @return: TableDataInfo<DelGoodsRejected>
     * @author: taoJie
     * @since: 2022-07-13
     */
    Boolean submit(BasChildParentChild basChildParentChild);


    /**
     * 判断客户是否可以加入
     *
     * @param basSeller
     * @return: BasSeller
     * @author: taoJie
     * @since: 2022-07-13
     */
    BasSeller sellerAdd(BasChildParentChildQueryVO basSeller);


    /**
     * 处理操作
     *
     * @param basChildParentChild
     * @return: Boolean
     * @author: taoJie
     * @since: 2022-07-13
     */
    Boolean dealOperation(BasChildParentChild basChildParentChild);


    /**
     * 客户新增
     *
     * @param basSeller
     * @author: taoJie
     * @since: 2022-07-13
     */
    Boolean submitList(BasSeller basSeller);

    List<String> getChildCodeList(String sellerCode);
}
