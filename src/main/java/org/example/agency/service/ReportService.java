package org.example.agency.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.agency.model.Property;
import org.example.agency.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private PropertyRepository propertyRepository;

    public byte[] generatePropertyReport() throws IOException {
        List<Property> properties = propertyRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Properties");

            // Header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Название", "Адрес", "Город", "Район", "Тип", "Цена", "Статус"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            int rowNum = 1;
            for (Property p : properties) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(p.getId());
                row.createCell(1).setCellValue(p.getTitle());
                row.createCell(2).setCellValue(p.getAddress());
                row.createCell(3).setCellValue(p.getCity());
                row.createCell(4).setCellValue(p.getDistrict() != null ? p.getDistrict().getName() : "");
                row.createCell(5).setCellValue(p.getPropertyType() != null ? p.getPropertyType().getName() : "");
                row.createCell(6).setCellValue(p.getPrice().doubleValue());
                row.createCell(7).setCellValue(p.getStatus());
            }

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
}
