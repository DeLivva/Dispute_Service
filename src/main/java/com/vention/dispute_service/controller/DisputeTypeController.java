package com.vention.dispute_service.controller;

import com.vention.dispute_service.dto.response.DisputeTypeResponseDTO;
import com.vention.dispute_service.service.DisputeTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dispute-types")
public class DisputeTypeController {

    public final DisputeTypeService disputeTypeService;

    @GetMapping
    public ResponseEntity<List<DisputeTypeResponseDTO>> getAll() {
        return ResponseEntity.ok(disputeTypeService.getAll());
    }
}
