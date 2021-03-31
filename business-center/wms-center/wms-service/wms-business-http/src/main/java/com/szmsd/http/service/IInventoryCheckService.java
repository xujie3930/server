package com.szmsd.http.service;

import com.szmsd.http.dto.*;
import com.szmsd.http.vo.ResponseVO;

public interface IInventoryCheckService {

    ResponseVO counting(CountingRequest countingRequest);
}
