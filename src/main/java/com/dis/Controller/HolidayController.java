package com.dis.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dis.Entity.Holiday;
import com.dis.Repository.HolidayRepository;

@RestController
@RequestMapping("/api/holiday")
public class HolidayController {

    @Autowired
    private HolidayRepository holidayRepository;

    @GetMapping("/execute")
    public ResponseEntity<List<Holiday>> getAllHolidays() {
        try {
            List<Holiday> users = holidayRepository.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
