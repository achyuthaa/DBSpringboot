package com.dis.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties.Jdbc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dis.Entity.Holiday;
import com.dis.Repository.HolidayRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/holiday")
public class HolidayController {

    /*
     * @Autowired
     * private HolidayRepository holidayRepository;
     * 
     * @GetMapping("/execute")
     * public ResponseEntity<List<Holiday>> getAllHolidays() {
     * try {
     * List<Holiday> users = holidayRepository.findAll();
     * return ResponseEntity.ok(users);
     * } catch (Exception e) {
     * e.printStackTrace();
     * return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
     * }
     * }
     */

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/executeQuery")
    public ResponseEntity<List<Map<String, Object>>> executeQuery(@RequestBody Map<String, String> requestBody) {
        String query = requestBody.get("query");
        try {
            List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(query);
            return ResponseEntity.ok(queryResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
