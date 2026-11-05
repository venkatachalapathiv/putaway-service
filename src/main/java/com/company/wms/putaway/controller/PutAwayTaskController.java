package com.company.wms.putaway.controller;

import com.company.wms.putaway.dto.CompletePutAwayRequest;
import com.company.wms.putaway.dto.CreatePutAwayTaskRequest;
import com.company.wms.putaway.dto.PutAwayTaskResponse;
import com.company.wms.putaway.service.PutAwayTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/putaway/tasks")
@Tag(name = "Put-Away Tasks", description = "Create, view, and complete put-away tasks")
public class PutAwayTaskController {

    private final PutAwayTaskService service;

    public PutAwayTaskController(PutAwayTaskService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Create a put-away task")
    public ResponseEntity<PutAwayTaskResponse> create(
            @Valid @RequestBody CreatePutAwayTaskRequest request) {

        PutAwayTaskResponse body = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a put-away task by id")
    public ResponseEntity<PutAwayTaskResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.get(id));
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete a put-away task")
    public ResponseEntity<PutAwayTaskResponse> complete(
            @PathVariable UUID id,
            @Valid @RequestBody CompletePutAwayRequest request) {

        return ResponseEntity.ok(service.complete(id, request));
    }
}