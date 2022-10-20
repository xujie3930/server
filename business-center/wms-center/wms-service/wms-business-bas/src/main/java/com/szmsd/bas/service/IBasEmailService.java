package com.szmsd.bas.service;

import com.szmsd.bas.dto.EmailDto;
import com.szmsd.common.core.domain.R;

public interface IBasEmailService {

    R sendEmail(EmailDto emailDto);
}
