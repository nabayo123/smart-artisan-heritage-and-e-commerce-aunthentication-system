package com.korarwandasystem.korarwanda.service;

import com.korarwandasystem.korarwanda.model.Cooperative;
import com.korarwandasystem.korarwanda.repository.CooperativeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CooperativeService {

    @Autowired
    private CooperativeRepository cooperativeRepository;

    public List<Cooperative> getAllCooperatives() {
        return cooperativeRepository.findAll();
    }

    public Optional<Cooperative> getCooperativeById(Long id) {
        return cooperativeRepository.findById(id);
    }

    public Cooperative createCooperative(Cooperative cooperative) {
        return cooperativeRepository.save(cooperative);
    }

    public Cooperative updateCooperative(Long id, Cooperative cooperativeDetails) {
        Optional<Cooperative> optionalCooperative = cooperativeRepository.findById(id);
        if (optionalCooperative.isPresent()) {
            Cooperative cooperative = optionalCooperative.get();
            cooperative.setName(cooperativeDetails.getName());
            cooperative.setProvince(cooperativeDetails.getProvince());
            cooperative.setDistrict(cooperativeDetails.getDistrict());
            cooperative.setContactPhone(cooperativeDetails.getContactPhone());
            return cooperativeRepository.save(cooperative);
        }
        return null;
    }

    public boolean deleteCooperative(Long id) {
        if (cooperativeRepository.existsById(id)) {
            cooperativeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Cooperative> getCooperativesByProvince(String province) {
        return cooperativeRepository.findByProvince(province);
    }

    public List<Cooperative> getCooperativesByDistrict(String district) {
        return cooperativeRepository.findByDistrict(district);
    }

    public long getTotalCooperativesCount() {
        return cooperativeRepository.count();
    }
}
