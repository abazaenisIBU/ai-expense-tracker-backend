package com.example.aiexpensetracker.rest.controller;

import com.example.aiexpensetracker.core.service.ServiceManager;
import com.example.aiexpensetracker.core.service.contracts.IExpenseService;
import com.example.aiexpensetracker.rest.dto.statistics.StatisticsResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final ServiceManager serviceManager;

    public StatisticsController(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @GetMapping("/api/statistics")
    public ResponseEntity<StatisticsResponseDTO> getStatistics(@RequestParam("email") String email) {
        StatisticsResponseDTO statistics = serviceManager.getExpenseService().getStatisticsForUser(email);
        return ResponseEntity.ok(statistics);
    }
}
