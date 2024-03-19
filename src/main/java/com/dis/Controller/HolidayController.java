package com.dis.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;

import com.dis.Entity.Holiday;
import com.dis.Repository.HolidayRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/holiday")
public class HolidayController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/login")
    public ResponseEntity<?> loginFunction(@RequestBody Map<String, String> loginRequest, HttpSession session) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        if (isValid(username, password)) {
            session.setAttribute("username", username);
            String redirectUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/query.html")
                    .toUriString();
            return ResponseEntity.status(HttpStatus.OK).body(redirectUrl);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    private boolean isValid(String username, String password) {

        String query = "SELECT COUNT(*) FROM Users WHERE Name = ? AND Password = ?";
        int val = jdbcTemplate.queryForObject(query, Integer.class, username, password);

        return val == 1;

    }

    @PostMapping("/signup")
    public ResponseEntity<?> signupFunction(@RequestBody Map<String, String> signupRequest) {
        String username = signupRequest.get("username");
        String password = signupRequest.get("password");
        String email = signupRequest.get("email");

        if (usernameExists(username)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists");
        }

        String insertQuery = "INSERT INTO Users (Name, Email, Password, Userpermissions) VALUES (?, ?, ?, ?)";
        try {
            jdbcTemplate.update(insertQuery, username, email, password, 0);
            return ResponseEntity.status(HttpStatus.CREATED).body("Sign up successful");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to sign up");
        }
    }

    private boolean usernameExists(String username) {
        String query = "SELECT COUNT(*) FROM Users WHERE Name = ?";
        int count = jdbcTemplate.queryForObject(query, Integer.class, username);
        return count > 0;
    }

    @GetMapping("/isLoggedout")
    public ResponseEntity<String> isLoggedout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // Do not create a new session if it doesn't exist
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("Session has been invalidated");
    }

    @GetMapping("/isLoggedIn")
    public ResponseEntity<String> isLoggedIn(HttpSession session) {
        if (session.getAttribute("username") != null) {
            return ResponseEntity.ok("User is logged in");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not logged in");
        }
    }

    @PostMapping("/executeQuery")
    public ResponseEntity<?> executeQuery(@RequestBody Map<String, String> requestBody, HttpSession session) {
        if (session.getAttribute("username") != null) {
            String query = requestBody.get("query");
            String queryType = getQueryType(query);
            String addDetails = "INSERT INTO users_expenditure (Name, Query, Date) VALUES (?, ?, ?)";
            try {
                switch (queryType) {
                    case "SELECT":
                        List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(query);
                        String username = (String) session.getAttribute("username");
                        String currentDate = LocalDateTime.now().toString();
                        jdbcTemplate.update(addDetails, username, query, currentDate);
                        return ResponseEntity.ok(queryResult);
                    case "UPDATE":
                    case "INSERT":
                    case "DELETE":
                        int rowsAffected = jdbcTemplate.update(query);
                        Map<String, Object> response = new HashMap<>();
                        response.put("message", "Rows affected: " + rowsAffected);
                        response.put("data", Collections.emptyList());
                        return ResponseEntity.ok(response);
                    default:
                        Map<String, Object> errorResponse = new HashMap<>();
                        errorResponse.put("error", "Unsupported query type");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Internal server error");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not logged in");
        }
    }

    private String getQueryType(String query) {
        String[] parts = query.trim().split("\\s+");
        if (parts.length > 0) {
            return parts[0].toUpperCase();
        }
        return "";

    }

}
