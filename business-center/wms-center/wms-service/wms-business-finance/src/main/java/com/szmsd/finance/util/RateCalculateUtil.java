package com.szmsd.finance.util;

import com.szmsd.finance.domain.ExchangeRate;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author liulei
 */
public class RateCalculateUtil {

    private RateNode head;
    private RateNode tail;
    private static int scale=4;

    public static RateCalculateUtil buildRateTree(String fromCurrency, String toCurrency, List<ExchangeRate> exchangeRateList){
        RateCalculateUtil util=new RateCalculateUtil();
        RateNode head=new RateNode(null,null,fromCurrency);
        util.head=head;
        if(exchangeRateList.size()==0) return util;
        fillLeaf(toCurrency,head,exchangeRateList,util);
        return util;
    }

    private static void fillLeaf(String toCurrency, RateNode head, List<ExchangeRate> exchangeRateList, RateCalculateUtil util) {
        String fromCurrency=head.currency;
        Iterator<ExchangeRate> iterator = exchangeRateList.iterator();
        while(iterator.hasNext()){
            ExchangeRate exchangeRate = iterator.next();
            if(StringUtils.equals(fromCurrency,exchangeRate.getExchangeFromCode())){
                head.hasNext=true;
                BigDecimal rate=exchangeRate.getRate();
                RateNode node=new RateNode(head,rate,exchangeRate.getExchangeToCode());
                if(StringUtils.equals(toCurrency,exchangeRate.getExchangeToCode())){
                    util.tail=node;
                }
                iterator.remove();
            }
            if(StringUtils.equals(fromCurrency,exchangeRate.getExchangeToCode())){
                head.hasNext=true;
                BigDecimal rate=BigDecimal.ONE.divide(exchangeRate.getRate(),4,BigDecimal.ROUND_FLOOR);
                RateNode node=new RateNode(head,rate,exchangeRate.getExchangeFromCode());
                if(StringUtils.equals(toCurrency,exchangeRate.getExchangeFromCode())){
                    util.tail=node;
                }
                iterator.remove();
            }
        }
        if(exchangeRateList.size()==0||!head.hasNext) return;
        for(RateNode node:head.children){
            fillLeaf(toCurrency,node,exchangeRateList,util);
        }
    }

    public BigDecimal getFromToRate(){
        if(tail!=null){
            RateNode node=tail;
            BigDecimal rate=tail.rate;
            while(node.prev!=head){
                node=node.prev;
                rate=rate.multiply(node.rate).setScale(scale,BigDecimal.ROUND_FLOOR);
            }
            return rate;
        }
        return null;
    }

    static class RateNode{
        RateNode prev;
        BigDecimal rate;
        String currency;
        boolean hasNext;
        List<RateNode> children;

        public RateNode(RateNode prev, BigDecimal rate, String currency) {
            this.prev = prev;
            if(prev!=null) prev.children.add(this);
            this.rate = rate;
            this.currency = currency;
            this.hasNext = false;
            children=new ArrayList<>();
        }
    }
}
