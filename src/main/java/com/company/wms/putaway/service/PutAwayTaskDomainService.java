package com.company.wms.putaway.service;

import com.company.wms.putaway.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

@Service
public class PutAwayTaskDomainService {

	public void validateCreate(Integer quantity, Integer eligibleQuantity) {
		if (quantity == null || quantity <= 0) {
			throw new BusinessRuleException("Quantity must be greater than zero");
		}
		if (eligibleQuantity != null && quantity > eligibleQuantity) {
			throw new BusinessRuleException("Put-away quantity cannot exceed eligible received quantity");
		}
	}
}