package com.company.wms.putaway.controller;

import com.company.wms.putaway.dto.OutboxRetryCountResponse;
import com.company.wms.putaway.service.OutboxRetryCountService;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/putaway/outbox/retries")
public class OutboxRetryController {

	private final OutboxRetryCountService retryCountService;

	public OutboxRetryController(OutboxRetryCountService retryCountService) {
		this.retryCountService = retryCountService;
	}

	@GetMapping
	public Page<OutboxRetryCountResponse> getRetryCounts(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {

		return retryCountService.getRetryCounts(page, size);
	}

	@GetMapping("/{id}")
	public OutboxRetryCountResponse getRetryCount(@PathVariable UUID id) {

		return retryCountService.getRetryCount(id);
	}

	@PostMapping("/{id}")
	public OutboxRetryCountResponse retryFailedEvent(@PathVariable UUID id) {

		return retryCountService.retryFailedEvent(id);
	}

}
