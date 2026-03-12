package org.example.agency.controller;

import org.example.agency.service.AuditLogService;
import org.example.agency.service.BackupService;
import org.example.agency.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ReportService reportService;

    @Autowired
    private BackupService backupService;

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Query 1: Average price per district (JOIN + Aggregate)
        List<Map<String, Object>> avgPricePerDistrict = jdbcTemplate.queryForList(
                "SELECT d.name, AVG(p.price) as avg_price FROM properties p JOIN districts d ON p.district_id = d.id GROUP BY d.id, d.name"
        );
        model.addAttribute("avgPricePerDistrict", avgPricePerDistrict);

        // Query 2: Total sales volume by agent (JOIN + Aggregate)
        List<Map<String, Object>> salesByAgent = jdbcTemplate.queryForList(
                "SELECT a.first_name, a.last_name, SUM(d.final_price) as total_sales " +
                "FROM deals d JOIN agents a ON d.agent_id = a.id " +
                "WHERE d.status = 'COMPLETED' GROUP BY a.id, a.first_name, a.last_name"
        );
        model.addAttribute("salesByAgent", salesByAgent);

        // Query 3: Most popular property types (JOIN + Aggregate)
        List<Map<String, Object>> popularTypes = jdbcTemplate.queryForList(
                "SELECT pt.name, COUNT(*) as count FROM properties p JOIN property_types pt ON p.property_type_id = pt.id GROUP BY pt.id, pt.name"
        );
        model.addAttribute("popularTypes", popularTypes);

        // Query 4: Clients with more than 1 deal (Subquery or HAVING)
        List<Map<String, Object>> repeatClients = jdbcTemplate.queryForList(
                "SELECT c.first_name, c.last_name, COUNT(d.id) as deal_count " +
                "FROM clients c JOIN deals d ON c.id = d.client_id " +
                "GROUP BY c.id, c.first_name, c.last_name HAVING COUNT(d.id) > 1"
        );
        model.addAttribute("repeatClients", repeatClients);

        // Query 5: Revenue by month (Aggregate)
        List<Map<String, Object>> revenueByMonth = jdbcTemplate.queryForList(
                "SELECT TO_CHAR(deal_date, 'YYYY-MM') as month, SUM(final_price) as revenue " +
                "FROM deals WHERE status = 'COMPLETED' GROUP BY month ORDER BY month"
        );
        model.addAttribute("revenueByMonth", revenueByMonth);

        // Query 6: Properties with most features (JOIN + Aggregate)
        List<Map<String, Object>> propertiesByFeatures = jdbcTemplate.queryForList(
                "SELECT p.title, COUNT(pf.feature_id) as feature_count " +
                "FROM properties p JOIN property_features pf ON p.id = pf.property_id " +
                "GROUP BY p.id, p.title ORDER BY feature_count DESC LIMIT 5"
        );
        model.addAttribute("propertiesByFeatures", propertiesByFeatures);

        // Query 7: Latest viewings with property and agent (JOIN)
        List<Map<String, Object>> latestViewings = jdbcTemplate.queryForList(
                "SELECT v.viewing_date, p.title as property, a.last_name as agent, c.last_name as client " +
                "FROM viewings v JOIN properties p ON v.property_id = p.id " +
                "JOIN agents a ON v.agent_id = a.id JOIN clients c ON v.client_id = c.id " +
                "ORDER BY v.viewing_date DESC LIMIT 10"
        );
        model.addAttribute("latestViewings", latestViewings);

        // Query 8: Average discount on properties (JOIN + Aggregate + Math)
        List<Map<String, Object>> avgDiscount = jdbcTemplate.queryForList(
                "SELECT AVG((p.price - d.final_price) / p.price * 100) as avg_discount " +
                "FROM deals d JOIN properties p ON d.property_id = p.id WHERE d.status = 'COMPLETED'"
        );
        model.addAttribute("avgDiscount", avgDiscount);

        // Query 9: Properties with no viewings (LEFT JOIN + IS NULL)
        List<Map<String, Object>> propertiesNoViewings = jdbcTemplate.queryForList(
                "SELECT p.title, p.price FROM properties p LEFT JOIN viewings v ON p.id = v.property_id " +
                "WHERE v.id IS NULL AND p.status = 'AVAILABLE'"
        );
        model.addAttribute("propertiesNoViewings", propertiesNoViewings);

        // Query 10: Agents with highest ratings from reviews (JOIN + Aggregate)
        List<Map<String, Object>> topAgentsByRating = jdbcTemplate.queryForList(
                "SELECT a.first_name, a.last_name, AVG(r.rating) as avg_rating " +
                "FROM agents a JOIN reviews r ON a.id = r.agent_id " +
                "GROUP BY a.id, a.first_name, a.last_name ORDER BY avg_rating DESC LIMIT 5"
        );
        model.addAttribute("topAgentsByRating", topAgentsByRating);

        return "admin/dashboard";
    }

    @GetMapping("/queries")
    public String queriesPage() {
        return "admin/queries";
    }

    @PostMapping("/execute-query")
    public String executeQuery(@RequestParam String sql, Model model) {
        try {
            if (sql.trim().toUpperCase().startsWith("SELECT")) {
                List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
                model.addAttribute("results", results);
                if (!results.isEmpty()) {
                    model.addAttribute("columns", results.get(0).keySet());
                }
            } else {
                jdbcTemplate.execute(sql);
                model.addAttribute("message", "Query executed successfully.");
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("sql", sql);
        return "admin/queries";
    }

    @GetMapping("/backup")
    public String backupPage() {
        return "admin/backup";
    }

    @PostMapping("/backup/create")
    public String createBackup(Model model) {
        try {
            String fileName = backupService.createBackup();
            model.addAttribute("message", "Backup created: " + fileName);
        } catch (Exception e) {
            model.addAttribute("error", "Backup failed: " + e.getMessage());
        }
        return "admin/backup";
    }

    @PostMapping("/backup/restore")
    public String restoreBackup(@RequestParam String fileName, Model model) {
        try {
            backupService.restoreBackup(fileName);
            model.addAttribute("message", "Restore completed from: " + fileName);
        } catch (Exception e) {
            model.addAttribute("error", "Restore failed: " + e.getMessage());
        }
        return "admin/backup";
    }

    @GetMapping("/logs")
    public String logsPage(Model model) {
        model.addAttribute("logs", auditLogService.getAllLogs());
        return "admin/logs";
    }

    @GetMapping("/export/properties")
    public ResponseEntity<byte[]> exportProperties() throws IOException {
        byte[] data = reportService.generatePropertyReport();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=properties_report.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}
