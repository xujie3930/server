package com.szmsd.putinstorage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.putinstorage.domain.InboundReceiptRecord;
import com.szmsd.putinstorage.domain.dto.InboundReceiptRecordQueryDTO;
import com.szmsd.putinstorage.mapper.InboundReceiptRecordMapper;
import com.szmsd.putinstorage.service.IInboundReceiptRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
@Slf4j
public class InboundReceiptRecordServiceImpl extends ServiceImpl<InboundReceiptRecordMapper, InboundReceiptRecord> implements IInboundReceiptRecordService {

    @Override
    public List<InboundReceiptRecord> selectList(InboundReceiptRecordQueryDTO queryDTO,String le) {
        LambdaQueryWrapper<InboundReceiptRecord> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(StringUtils.isNotEmpty(queryDTO.getWarehouseNo()), InboundReceiptRecord::getWarehouseNo, queryDTO.getWarehouseNo());
        queryWrapper.eq(StringUtils.isNotEmpty(queryDTO.getType()), InboundReceiptRecord::getType, queryDTO.getType());
        queryWrapper.ne(InboundReceiptRecord::getType, "修改");
        queryWrapper.orderByDesc(InboundReceiptRecord::getCreateTime);
        List<InboundReceiptRecord>  inboundReceiptRecordList=baseMapper.selectList(queryWrapper);
        String li="上架数量";
        if (le.equals("en")) {
            inboundReceiptRecordList.forEach(x -> {
                if (x.getRemark().contains(li)) {
                    //拼接*,把敏感词替换成Quantity put on shelves
                    String s = "Quantity put on shelves";
//                for (int j = 0; j <li.length(); j++) {
//                    s+="Quantity put on shelves";
//                }
                    x.setRemark(x.getRemark().replace(li, s));
                }
            });
        }
        return inboundReceiptRecordList;
    }

//    public static void main(String[] args) {
//        List li =new ArrayList();
//        li.add("易烊千玺");
//        li.add("朱一龙");
//        li.add("周雨彤");
//        li.add("张子枫");
//
//        Scanner sc  = new Scanner(System.in);
//        System.out.println("请输入评论内容:");
//        String content = sc.nextLine();
//
//        for (int i = 0; i < li.size(); i++) {
//            String ci = (String) li.get(i);//拿到敏感词，强转
//            if(content.contains(ci))
//            {
//                //拼接*,把敏感词替换成*
//                String s = "";
//                for (int j = 0; j <ci.length(); j++) {
//                    s+="*";
//                }
//                content=content.replace(ci,s);
//            }
//        }
//
//        System.out.println(content);
//    }

}

