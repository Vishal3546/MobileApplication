package com.buysell.modules.reports.service;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;

@Service
public class BusinessDateTimeService {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Kolkata");

    public static class DateRange {
        public final ZonedDateTime start;
        public final ZonedDateTime end; // Exclusive end: [start, end)

        public DateRange(ZonedDateTime start, ZonedDateTime end) {
            this.start = start;
            this.end = end;
        }
    }

    public DateRange resolveDateRange(String rangeStr, String customStart, String customEnd) {
        if (rangeStr == null) {
            return getRangeFor("today");
        }

        switch (rangeStr.toLowerCase()) {
            case "yesterday":
                return getRangeFor("yesterday");
            case "this_week":
                return getRangeFor("this_week");
            case "this_month":
                return getRangeFor("this_month");
            case "last_7_days":
                return getRangeFor("last_7_days");
            case "last_30_days":
                return getRangeFor("last_30_days");
            case "custom_range":
                return resolveCustomRange(customStart, customEnd);
            case "today":
            default:
                return getRangeFor("today");
        }
    }

    private DateRange getRangeFor(String range) {
        ZonedDateTime nowInBusinessZone = ZonedDateTime.now(BUSINESS_ZONE);
        LocalDate today = nowInBusinessZone.toLocalDate();

        switch (range) {
            case "yesterday": {
                LocalDate yesterday = today.minusDays(1);
                return new DateRange(yesterday.atStartOfDay(BUSINESS_ZONE), today.atStartOfDay(BUSINESS_ZONE));
            }
            case "this_week": {
                LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                LocalDate startOfNextWeek = startOfWeek.plusWeeks(1);
                return new DateRange(startOfWeek.atStartOfDay(BUSINESS_ZONE), startOfNextWeek.atStartOfDay(BUSINESS_ZONE));
            }
            case "this_month": {
                LocalDate startOfMonth = today.withDayOfMonth(1);
                LocalDate startOfNextMonth = startOfMonth.plusMonths(1);
                return new DateRange(startOfMonth.atStartOfDay(BUSINESS_ZONE), startOfNextMonth.atStartOfDay(BUSINESS_ZONE));
            }
            case "last_7_days": {
                LocalDate startOf7DaysAgo = today.minusDays(6); // 6 days ago + today = 7 days
                LocalDate startOfTomorrow = today.plusDays(1);
                return new DateRange(startOf7DaysAgo.atStartOfDay(BUSINESS_ZONE), startOfTomorrow.atStartOfDay(BUSINESS_ZONE));
            }
            case "last_30_days": {
                LocalDate startOf30DaysAgo = today.minusDays(29);
                LocalDate startOfTomorrow = today.plusDays(1);
                return new DateRange(startOf30DaysAgo.atStartOfDay(BUSINESS_ZONE), startOfTomorrow.atStartOfDay(BUSINESS_ZONE));
            }
            case "today":
            default: {
                LocalDate tomorrow = today.plusDays(1);
                return new DateRange(today.atStartOfDay(BUSINESS_ZONE), tomorrow.atStartOfDay(BUSINESS_ZONE));
            }
        }
    }

    private DateRange resolveCustomRange(String customStart, String customEnd) {
        if (customStart == null || customEnd == null) {
            throw new IllegalArgumentException("Custom date range requires customStart and customEnd (YYYY-MM-DD)");
        }
        LocalDate startDate = LocalDate.parse(customStart);
        LocalDate endDate = LocalDate.parse(customEnd);

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate cannot be after endDate");
        }

        // Add safety limit for custom ranges (e.g. 1 year)
        if (startDate.isBefore(endDate.minusYears(1))) {
            throw new IllegalArgumentException("Date range cannot exceed 1 year");
        }

        // [start, end+1 day)
        return new DateRange(startDate.atStartOfDay(BUSINESS_ZONE), endDate.plusDays(1).atStartOfDay(BUSINESS_ZONE));
    }
}
