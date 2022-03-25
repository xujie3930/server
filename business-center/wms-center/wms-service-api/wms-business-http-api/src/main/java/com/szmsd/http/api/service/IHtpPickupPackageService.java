package com.szmsd.http.api.service;

import com.szmsd.http.dto.CreatePickupPackageCommand;
import com.szmsd.http.dto.CreatePricedSheetCommand;
import com.szmsd.http.vo.CarrierService;
import com.szmsd.http.vo.PickupPackageService;
import com.szmsd.http.vo.ResponseVO;

import java.util.List;

public interface IHtpPickupPackageService {

    List<PickupPackageService> services();

    ResponseVO create(CreatePickupPackageCommand createPickupPackageCommand);

}
