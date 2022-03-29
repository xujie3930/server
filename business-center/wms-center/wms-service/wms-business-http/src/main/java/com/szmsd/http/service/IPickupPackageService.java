package com.szmsd.http.service;

import com.szmsd.http.dto.*;
import com.szmsd.http.vo.PickupPackageService;
import com.szmsd.http.vo.ResponseVO;

import java.util.List;

/**
 * @author : wangshuai
 * @date : 2022-03-25 21:39
 * @description :
 **/
public interface IPickupPackageService {

    List<PickupPackageService> services();

    ResponseVO create(CreatePickupPackageCommand command);
}
