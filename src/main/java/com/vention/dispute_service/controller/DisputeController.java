package com.vention.dispute_service.controller;

import com.vention.dispute_service.dto.request.DisputeCreateRequestDTO;
import com.vention.dispute_service.dto.response.DisputeResponseDTO;
import com.vention.dispute_service.service.DisputeService;
import com.vention.general.lib.dto.request.PaginationRequestDTO;
import com.vention.general.lib.dto.response.ResponseWithPaginationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/disputes")
public class DisputeController {

    private final DisputeService disputeService;

    @PostMapping
    public ResponseEntity<DisputeResponseDTO> create(@RequestBody DisputeCreateRequestDTO requestDTO) {
        return new ResponseEntity<>(disputeService.create(requestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/close/{id}")
    public ResponseEntity<Void> close(@PathVariable("id") Long id) {
        disputeService.close(id);
        return new ResponseEntity<>(HttpStatus.valueOf(201));
    }

    @GetMapping
    public ResponseEntity<List<DisputeResponseDTO>> getByUserId(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(disputeService.getByUserId(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseWithPaginationDTO<DisputeResponseDTO>> getAll(PaginationRequestDTO paginationRequestDTO) {
        return ResponseEntity.ok(disputeService.getAll(paginationRequestDTO));
    }
}
