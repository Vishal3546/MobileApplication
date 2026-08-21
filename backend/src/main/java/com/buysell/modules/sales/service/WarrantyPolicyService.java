package com.buysell.modules.sales.service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WarrantyPolicyService {

    @Value("${sale.warranty.default-months:12}")
    private int defaultWarrantyMonths;

    public ZonedDateTime calculateWarrantyEndDate(ZonedDateTime startDate) {
        if (startDate == null) {
            return null;
        }
        return startDate.plus(defaultWarrantyMonths, ChronoUnit.MONTHS);
    }
}
