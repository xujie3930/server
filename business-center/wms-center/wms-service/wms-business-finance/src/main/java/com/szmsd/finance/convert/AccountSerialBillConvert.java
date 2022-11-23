package com.szmsd.finance.convert;

import com.szmsd.finance.domain.AccountSerialBill;
import com.szmsd.finance.vo.AccountSerialBillExcelVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AccountSerialBillConvert {

    AccountSerialBillConvert INSTANCE = Mappers.getMapper(AccountSerialBillConvert.class);


    AccountSerialBillExcelVO toExcelVO (AccountSerialBill accountSerialBill);


    List<AccountSerialBillExcelVO> toSerialBillExcelListVO(List<AccountSerialBill> accountSerialBills);
}
