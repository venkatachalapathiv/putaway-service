
package com.company.wms.putaway.service;

import com.company.wms.putaway.exception.BusinessRuleException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PutAwayTaskDomainServiceTest {

    private final PutAwayTaskDomainService domainService =
            new PutAwayTaskDomainService();

    @Test
    void validateCreate_ShouldAcceptValidQuantity() {

        assertDoesNotThrow(
                () -> domainService.validateCreate(100, null)
        );
    }

    @Test
    void validateCreate_ShouldRejectNullQuantity() {

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> domainService.validateCreate(null, null)
        );

        assertEquals(
                "Quantity must be greater than zero",
                exception.getMessage()
        );
    }

    @Test
    void validateCreate_ShouldRejectZeroQuantity() {

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> domainService.validateCreate(0, null)
        );

        assertEquals(
                "Quantity must be greater than zero",
                exception.getMessage()
        );
    }

    @Test
    void validateCreate_ShouldRejectNegativeQuantity() {

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> domainService.validateCreate(-10, null)
        );

        assertEquals(
                "Quantity must be greater than zero",
                exception.getMessage()
        );
    }

    @Test
    void validateCreate_ShouldAcceptQuantityEqualToEligibleQuantity() {

        assertDoesNotThrow(
                () -> domainService.validateCreate(100, 100)
        );
    }

    @Test
    void validateCreate_ShouldAcceptQuantityLessThanEligibleQuantity() {

        assertDoesNotThrow(
                () -> domainService.validateCreate(50, 100)
        );
    }

    @Test
    void validateCreate_ShouldRejectQuantityGreaterThanEligibleQuantity() {

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> domainService.validateCreate(150, 100)
        );

        assertEquals(
                "Put-away quantity cannot exceed eligible received quantity",
                exception.getMessage()
        );
    }
}
