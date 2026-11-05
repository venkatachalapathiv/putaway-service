package com.company.wms.putaway.service;

import com.company.wms.putaway.dto.CompletePutAwayRequest;
import com.company.wms.putaway.dto.CreatePutAwayTaskRequest;
import com.company.wms.putaway.dto.PutAwayTaskResponse;

import java.util.UUID;

public interface PutAwayTaskService {

    PutAwayTaskResponse create(CreatePutAwayTaskRequest request);

    PutAwayTaskResponse get(UUID id);

    PutAwayTaskResponse complete(UUID id, CompletePutAwayRequest request);
}