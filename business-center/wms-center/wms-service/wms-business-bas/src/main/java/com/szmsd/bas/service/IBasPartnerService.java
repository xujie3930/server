package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasPartner;

public interface IBasPartnerService extends IService<BasPartner> {

    boolean save(BasPartner entity);

    boolean update(BasPartner entity);

    boolean delete(BasPartner entity);

    BasPartner getByCode(BasPartner entity);
}
