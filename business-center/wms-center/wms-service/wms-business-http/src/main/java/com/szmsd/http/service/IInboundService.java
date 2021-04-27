package com.szmsd.http.service;

import com.szmsd.http.dto.CancelReceiptRequest;
import com.szmsd.http.dto.CreatePackageReceiptRequest;
import com.szmsd.http.dto.CreateReceiptRequest;
import com.szmsd.http.vo.CreateReceiptResponse;
import com.szmsd.http.vo.ResponseVO;

public interface IInboundService {

    CreateReceiptResponse create(CreateReceiptRequest createReceiptRequestDTO);

    ResponseVO cancel(CancelReceiptRequest cancelReceiptRequestDTO);

    ResponseVO createPackage(CreatePackageReceiptRequest createPackageReceiptRequest);
}
