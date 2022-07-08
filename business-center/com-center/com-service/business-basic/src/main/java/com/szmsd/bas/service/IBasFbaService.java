package com.szmsd.bas.service;

import com.szmsd.bas.api.domain.BasFba;
import com.szmsd.bas.api.domain.BasRegion;
import com.szmsd.bas.api.domain.dto.BasFbaDTO;
import com.szmsd.common.core.domain.R;

import java.util.List;

public interface IBasFbaService {
    R  insertBasFba(BasFba basFba);

    R<List<BasFba>>  selectBasFba(BasFbaDTO basFbaDTO);

    R deleteBasFba(Integer id);
}
