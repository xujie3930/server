package com.szmsd.bas.service;


import com.szmsd.bas.domain.BasChannels;
import com.szmsd.bas.dto.BasChannelsDTO;
import com.szmsd.common.core.domain.R;

import java.util.List;

public interface IBasChannelsService {
    R  insertBasChannels(BasChannels basChannels);

    R<List<BasChannels>>  selectBasChannels(BasChannelsDTO basChannelsDTO);

    R  deleteBasChannels(BasChannelsDTO basChannelsDTO);
}
