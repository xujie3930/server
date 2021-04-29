package com.szmsd.report.service;

import com.szmsd.returnex.domain.vo.CusWalletVO;
import com.szmsd.returnex.domain.vo.DocumentVO;

import java.util.List;

public interface HomeService {

    List<CusWalletVO> selectCusWallet(String cusCode);

    List<DocumentVO> selectDocuments(String cusCode);

    List<DocumentVO> selectProblem(String cusCode);

    List<List<String>> queryOrder7Report(String cusCode);
}
