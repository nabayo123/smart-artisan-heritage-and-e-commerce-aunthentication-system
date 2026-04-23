package com.korarwanda.kora.service.impl;

import com.korarwanda.kora.dto.CooperativeDto;
import com.korarwanda.kora.entity.Cooperative;
import com.korarwanda.kora.enums.VerificationStatus;
import com.korarwanda.kora.exception.BadRequestException;
import com.korarwanda.kora.exception.ResourceNotFoundException;
import com.korarwanda.kora.repository.CooperativeRepository;
import com.korarwanda.kora.service.CooperativeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CooperativeServiceImpl implements CooperativeService {

    private final CooperativeRepository cooperativeRepository;

    @Override
    @Transactional
    public CooperativeDto.Response create(CooperativeDto.Request request) {
        if (cooperativeRepository.existsByName(request.getName())) {
            throw new BadRequestException("Cooperative with name '" + request.getName() + "' already exists");
        }
        Cooperative cooperative = Cooperative.builder()
                .name(request.getName())
                .province(request.getProvince())
                .district(request.getDistrict())
                .contactPhone(request.getContactPhone())
                .build();
        return toResponse(cooperativeRepository.save(cooperative));
    }

    @Override
    public CooperativeDto.Response getById(Long id) {
        return toResponse(cooperativeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cooperative", id)));
    }

    @Override
    public List<CooperativeDto.Response> getAll() {
        return cooperativeRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<CooperativeDto.Response> getByProvince(String province) {
        return cooperativeRepository.findByProvince(province).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CooperativeDto.Response updateStatus(Long id, VerificationStatus status) {
        Cooperative cooperative = cooperativeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cooperative", id));
        cooperative.setVerificationStatus(status);
        return toResponse(cooperativeRepository.save(cooperative));
    }

    @Override
    @Transactional
    public CooperativeDto.Response update(Long id, CooperativeDto.Request request) {
        Cooperative cooperative = cooperativeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cooperative", id));
        cooperative.setName(request.getName());
        cooperative.setProvince(request.getProvince());
        cooperative.setDistrict(request.getDistrict());
        cooperative.setContactPhone(request.getContactPhone());
        return toResponse(cooperativeRepository.save(cooperative));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!cooperativeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cooperative", id);
        }
        cooperativeRepository.deleteById(id);
    }

    private CooperativeDto.Response toResponse(Cooperative c) {
        CooperativeDto.Response r = new CooperativeDto.Response();
        r.setCooperativeId(c.getCooperativeId());
        r.setName(c.getName());
        r.setProvince(c.getProvince());
        r.setDistrict(c.getDistrict());
        r.setContactPhone(c.getContactPhone());
        r.setVerificationStatus(c.getVerificationStatus());
        r.setCreatedAt(c.getCreatedAt());
        r.setArtisanCount(c.getArtisans() != null ? c.getArtisans().size() : 0);
        return r;
    }
}
