package com.korarwandasystem.korarwanda.service;

import com.korarwandasystem.korarwanda.model.Artisan;
import com.korarwandasystem.korarwanda.model.VerificationStatus;
import com.korarwandasystem.korarwanda.repository.ArtisanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ArtisanService {

    private final ArtisanRepository artisanRepository;

    public ArtisanService(ArtisanRepository artisanRepository) {
        this.artisanRepository = artisanRepository;
    }

    public List<Artisan> getAllArtisans() {
        return artisanRepository.findAll();
    }

    public Optional<Artisan> getArtisanById(Long id) {
        return artisanRepository.findById(id);
    }

    public Artisan createArtisan(Artisan artisan) {
        if (artisan == null) {
            throw new IllegalArgumentException("Artisan cannot be null");
        }
        if (artisan.getEmail() != null && existsByEmail(artisan.getEmail())) {
            throw new IllegalArgumentException("Artisan with email " + artisan.getEmail() + " already exists");
        }
        return artisanRepository.save(artisan);
    }

    public Artisan updateArtisan(Long id, Artisan details) {
        if (details == null) {
            throw new IllegalArgumentException("Artisan details cannot be null");
        }
        
        return artisanRepository.findById(id)
                .map(artisan -> {
                    if (details.getFullName() != null) {
                        artisan.setFullName(details.getFullName());
                    }
                    if (details.getEmail() != null) {
                        artisan.setEmail(details.getEmail());
                    }
                    if (details.getSpecialization() != null) {
                        artisan.setSpecialization(details.getSpecialization());
                    }
                    if (details.getProvince() != null) {
                        artisan.setProvince(details.getProvince());
                    }
                    if (details.getDistrict() != null) {
                        artisan.setDistrict(details.getDistrict());
                    }
                    if (details.getVerificationStatus() != null) {
                        artisan.setVerificationStatus(details.getVerificationStatus());
                    }
                    artisan.setCooperative(details.getCooperative());
                    return artisanRepository.save(artisan);
                })
                .orElseThrow(() -> new IllegalArgumentException("Artisan not found with id: " + id));
    }

    public boolean deleteArtisan(Long id) {
        if (artisanRepository.existsById(id)) {
            artisanRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Artisan> getArtisansByCooperativeId(Long coopId) {
        return artisanRepository.findByCooperativeCooperativeId(coopId);
    }

    public List<Artisan> getArtisansBySpecialization(String spec) {
        return artisanRepository.findBySpecialization(spec);
    }

    public List<Artisan> getArtisansByVerificationStatus(VerificationStatus status) {
        return artisanRepository.findByVerificationStatus(status);
    }

    public List<Artisan> getVerifiedArtisans() {
        return artisanRepository.findByVerificationStatus(VerificationStatus.APPROVED);
    }

    public List<Artisan> getArtisansByProvince(String province) {
        return artisanRepository.findByProvince(province);
    }

    public List<Artisan> getArtisansByDistrict(String district) {
        return artisanRepository.findByDistrict(district);
    }

    public long getTotalArtisansCount() {
        return artisanRepository.count();
    }

    public long getVerifiedArtisansCount() {
        return artisanRepository.countByVerificationStatus(VerificationStatus.APPROVED);
    }

    public boolean existsByEmail(String email) {
        return artisanRepository.existsByEmail(email);
    }

    public Optional<Artisan> getArtisanByEmail(String email) {
        return artisanRepository.findByEmail(email);
    }

    public boolean isOwner(Long artisanId, String email) {
        Optional<Artisan> artisan = artisanRepository.findById(artisanId);
        if (artisan.isPresent()) {
            Artisan foundArtisan = artisan.get();
            return email.equals(foundArtisan.getEmail());
        }
        return false;
    }
}