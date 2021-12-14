package com.szmsd.http.service;

import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.vo.HttpResponseVO;

public interface RemoteInterfaceService {

    HttpResponseVO rmi(HttpRequestDto dto);
}
