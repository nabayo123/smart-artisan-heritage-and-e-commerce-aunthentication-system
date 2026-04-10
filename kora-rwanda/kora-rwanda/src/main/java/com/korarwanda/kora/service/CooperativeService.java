package com.korarwanda.kora.service;

import com.korarwanda.kora.dto.CooperativeDto;
import com.korarwanda.kora.enums.VerificationStatus;

import java.util.List;

public interface CooperativeService {
    CooperativeDto.Response create(CooperativeDto.Request request);
    CooperativeDto.Response getById(Long id);
    List<CooperativeDto.Response> getAll();
    List<CooperativeDto.Response> getByProvince(String province);
    CooperativeDto.Response updateStatus(Long id, VerificationStatus status);
    CooperativeDto.Response update(Long id, CooperativeDto.Request request);
    void delete(Long id);
}
