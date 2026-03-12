package org.example.agency.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

@Component
public class DatabaseObjectInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        ClassPathResource resource = new ClassPathResource("db_objects.sql");
        String sql = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        
        // Execute the entire script
        // Note: In a real app we might want to split by ';' but PostgreSQL handles multi-statement strings
        jdbcTemplate.execute(sql);
        System.out.println("Database objects (functions, procedures, triggers) initialized successfully.");
    }
}
