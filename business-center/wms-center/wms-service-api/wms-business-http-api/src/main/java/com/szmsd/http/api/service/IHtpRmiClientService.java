package com.szmsd.http.api.service;

import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.vo.HttpResponseVO;

public interface IHtpRmiClientService {

    HttpResponseVO rmi(HttpRequestDto dto);
}
