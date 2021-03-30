package com.szmsd.chargerules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.chargerules.domain.BasSpecialOperation;
import com.szmsd.chargerules.vo.BasSpecialOperationVo;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;


public interface BaseInfoMapper extends BaseMapper<BasSpecialOperation> {

    @Results({
            @Result(column="operation_type",property="specialOperationList",
            many = @Many(select = "com.szmsd.chargerules.mapper.SpecialOperationMapper.detailsByOperationType"))
    })
    @Select("SELECT id,operator,operate_on,warehouse_code,transaction_id," +
            "operation_order_no,order_no,order_type,operation_type,qty," +
            "coefficient,unit,oms_remark,status,create_by,update_by," +
            "create_by_name,create_time,update_by_name,update_time,remark " +
            "FROM bas_special_operation WHERE id=#{id}")
    BasSpecialOperationVo selectDetails(int id);


}
