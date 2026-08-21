package com.buysell.modules.reports.service;

import com.buysell.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

@Service
@RequiredArgsConstructor
public class ReportExportService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * Streams the ResultSet directly to the HttpServletResponse output stream as CSV.
     * Memory overhead is O(1) regardless of row count.
     */
    public void exportToCsv(String sql, org.springframework.jdbc.core.namedparam.SqlParameterSource params, HttpServletResponse response, String filename) {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".csv\"");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter writer = response.getWriter()) {
            jdbcTemplate.query(sql, params, (ResultSet rs) -> {
                try {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    // Write headers
                    for (int i = 1; i <= columnCount; i++) {
                        writer.print(escapeCsv(metaData.getColumnName(i)));
                        if (i < columnCount) writer.print(",");
                    }
                    writer.println();

                    // Write rows
                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            String value = rs.getString(i);
                            writer.print(escapeCsv(value));
                            if (i < columnCount) writer.print(",");
                        }
                        writer.println();
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to stream CSV data", e);
                }
                return null;
            });
            writer.flush();
        } catch (IOException e) {
            throw new BusinessException("EXPORT_FAILED", "Failed to write CSV stream.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Enforces CSV escaping and mitigates Spreadsheet Formula Injection (CSV Injection).
     * Prefixes malicious starters (=, +, -, @) with a single quote.
     */
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        
        // CSV Injection Protection
        if (value.startsWith("=") || value.startsWith("+") || value.startsWith("-") || value.startsWith("@")) {
            value = "'" + value;
        }

        // Quote the value if it contains commas, quotes, or newlines
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            value = value.replace("\"", "\"\""); // Escape quotes
            return "\"" + value + "\"";
        }
        return value;
    }
}
