package com.spotify.util;

// Author: OOP Final Exam Group – ITE23005 HK252
// Date: 2026-05-23
// Purpose: ExcelExporter – exports track data to .xlsx using Apache POI

import com.spotify.model.Track;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.List;

/**
 * REST/Export Integration – Option 3: Excel Export.
 * Uses Apache POI with column headers, auto-fit widths, summary row at bottom.
 */
public class ExcelExporter {

    public static void exportTracks(List<Track> tracks, File outputFile) throws IOException {
        try (var wb = new XSSFWorkbook()) {
            var sheet = wb.createSheet("Spotify Tracks");

            // ── Styles ──────────────────────────────────────────────
            var headerStyle = createHeaderStyle(wb);
            var summaryStyle = createSummaryStyle(wb);
            var evenStyle  = wb.createCellStyle();
            var oddStyle   = wb.createCellStyle();
            evenStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            evenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            oddStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            oddStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // ── Header row ──────────────────────────────────────────
            String[] headers = {"#", "Track Name", "Artist", "Genre", "Popularity",
                    "Duration", "Danceability", "Energy", "Valence", "Tempo", "Explicit"};
            var headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                var cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // ── Data rows ───────────────────────────────────────────
            int rowNum = 1;
            double totalPop = 0;
            for (Track t : tracks) {
                var row = sheet.createRow(rowNum);
                var style = (rowNum % 2 == 0) ? evenStyle : oddStyle;
                row.createCell(0).setCellValue(rowNum);
                row.createCell(1).setCellValue(t.getTrackName());
                row.createCell(2).setCellValue(t.getArtist() != null ? t.getArtist().getName() : "");
                row.createCell(3).setCellValue(t.getTrackGenre() != null ? t.getTrackGenre() : "");
                row.createCell(4).setCellValue(t.getPopularity());
                row.createCell(5).setCellValue(t.getDurationFormatted());
                row.createCell(6).setCellValue(t.getDanceability());
                row.createCell(7).setCellValue(t.getEnergy());
                row.createCell(8).setCellValue(t.getValence());
                row.createCell(9).setCellValue(t.getTempo());
                row.createCell(10).setCellValue(t.isExplicit() ? "Yes" : "No");
                for (int c = 0; c < 11; c++) row.getCell(c).setCellStyle(style);
                totalPop += t.getPopularity();
                rowNum++;
            }

            // ── Summary row ─────────────────────────────────────────
            var sumRow = sheet.createRow(rowNum);
            var sumCell = sumRow.createCell(0);
            sumCell.setCellValue("SUMMARY: " + tracks.size() + " tracks | Avg Popularity: " +
                    String.format("%.1f", tracks.isEmpty() ? 0 : totalPop / tracks.size()));
            sumCell.setCellStyle(summaryStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 10));

            // ── Auto-fit column widths ──────────────────────────────
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

            // ── Write to file ───────────────────────────────────────
            try (var fos = new FileOutputStream(outputFile)) { wb.write(fos); }
        }
    }

    private static CellStyle createHeaderStyle(Workbook wb) {
        var style = wb.createCellStyle();
        var font  = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private static CellStyle createSummaryStyle(Workbook wb) {
        var style = wb.createCellStyle();
        var font  = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}