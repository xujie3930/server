package com.szmsd.common.core.web.page;

import com.github.pagehelper.ISelect;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.exception.com.LogisticsExceptionUtil;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

import static com.szmsd.common.core.web.controller.BaseController.getLen;

/**
 * @author liyingfeng
 * @date 2020/8/16 11:44
 */
public class PageHelperUtil {
    /**
     * 分页查询
     * 预防PageHelper与查询sql之间受其他selectQuery 语句影响
     * @param queryDto 自定义编辑分页参数
     * @param select query接口
     * @param <T>
     * @return pageInfo
     */
    public static  <T> PageInfo<T> pageExecute(final PageDomain queryDto, ISelect select) {
        return PageHelper.startPage(queryDto==null?getPageDomain():queryDto)
                .doSelectPageInfo(select);
    }

    /**
     * 分页查询 - 自动拉取请求参数
     * @param select query接口
     * @param <T>
     * @return
     */
    public static  <T> PageInfo<T> pageExecute(ISelect select) {
        return pageExecute(getPageDomain(),select);
    }

    public static  <T> PageInfo<T> pageExecute(ISelect select ,Class<T> t) {
        return instancePageInfo(pageExecute(getPageDomain(),select),t);
    }

    public static <T, E>PageInfo<T> instancePageInfo(Page<E> page, List<T> list) {
        PageInfo pageInfo = new PageInfo<>(list);
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPageNum(page.getPageNum());
        pageInfo.setPageSize(page.getPageSize());
        pageInfo.setPages(page.getPages());
        return pageInfo;
    }

    /**
     * page对象转换成pageInfo对象，记录total
     * @param page
     * @param t
     * @return
     */
    public static <T, E>PageInfo<T> instancePageInfo(Page<E> page, Class<T> t) {
        PageInfo pageInfo = new PageInfo<>(page);
        if(t != null){
            pageInfo.setList(BeanMapperUtil.mapList(page, t));
        }
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPageNum(page.getPageNum());
        pageInfo.setPageSize(page.getPageSize());
        pageInfo.setPages(page.getPages());
        return pageInfo;
    }

    public static <T, E> PageInfo<T> instancePageInfo(PageInfo<E> page, List<T> list) {
        PageInfo<T> pageInfo = new PageInfo<>();
        pageInfo.setList(list);
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPageNum(page.getPageNum());
        pageInfo.setPageSize(page.getPageSize());
        pageInfo.setPages(page.getPages());
        return pageInfo;
    }

    public static <T, E> PageInfo<T> instancePageInfo(PageInfo<E> pageInfo, Class<T> t) {
        return instancePageInfo(pageInfo, BeanMapperUtil.mapList(pageInfo.getList(), t));
    }

    /**
     *
     * @param list
     * @param t
     * @param <T>
     * @param <E>
     * @return
     */
    @Deprecated
    public static <T, E> PageInfo<T> instancePageInfo(List<E> list, Class<T> t) {
        if(CollectionUtils.isEmpty(list) || !(list instanceof Page)){
            throw LogisticsExceptionUtil.getException(ExceptionMessageEnum.CASTINGFAIL,getLen());
        }
       return instancePageInfo((Page)list,t);
    }

    @Deprecated
    public static <T, E> PageInfo<T> instancePageInfo(List<E> list) {
        return instancePageInfo(list,null);
    }

    public  static PageDomain getPageDomain(){
        PageDomain pageDomain = Optional.ofNullable(TableSupport.buildPageRequest())
                .orElseGet(()->new PageDomain());
        if(pageDomain.getPageNum() == null){
            pageDomain.setPageNum(new Integer(1));
        }
        if(pageDomain.getPageSize() == null){
            pageDomain.setPageSize(new Integer(10));
        }
        return pageDomain;
    }

    private static Page getPage(){
        return null;
    }

}
