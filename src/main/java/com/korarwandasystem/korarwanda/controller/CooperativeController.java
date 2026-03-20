package com.korarwandasystem.korarwanda.controller;

import com.korarwandasystem.korarwanda.model.Cooperative;
import com.korarwandasystem.korarwanda.service.CooperativeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cooperatives")
public class CooperativeController {

    @Autowired
    private CooperativeService cooperativeService;

    @GetMapping
    public ResponseEntity<List<Cooperative>> getAllCooperatives() {
        List<Cooperative> cooperatives = cooperativeService.getAllCooperatives();
        return ResponseEntity.ok(cooperatives);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cooperative> getCooperativeById(@PathVariable Long id) {
        return cooperativeService.getCooperativeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Cooperative> createCooperative(@Valid @RequestBody Cooperative cooperative) {
        Cooperative createdCooperative = cooperativeService.createCooperative(cooperative);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCooperative);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cooperative> updateCooperative(@PathVariable Long id, @Valid @RequestBody Cooperative cooperative) {
        Cooperative updatedCooperative = cooperativeService.updateCooperative(id, cooperative);
        if (updatedCooperative != null) {
            return ResponseEntity.ok(updatedCooperative);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCooperative(@PathVariable Long id) {
        boolean deleted = cooperativeService.deleteCooperative(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/province/{province}")
    public ResponseEntity<List<Cooperative>> getCooperativesByProvince(@PathVariable String province) {
        List<Cooperative> cooperatives = cooperativeService.getCooperativesByProvince(province);
        return ResponseEntity.ok(cooperatives);
    }

    @GetMapping("/district/{district}")
    public ResponseEntity<List<Cooperative>> getCooperativesByDistrict(@PathVariable String district) {
        List<Cooperative> cooperatives = cooperativeService.getCooperativesByDistrict(district);
        return ResponseEntity.ok(cooperatives);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalCooperativesCount() {
        long count = cooperativeService.getTotalCooperativesCount();
        return ResponseEntity.ok(count);
    }
}
