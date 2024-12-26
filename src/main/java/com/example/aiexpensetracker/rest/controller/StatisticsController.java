package com.example.aiexpensetracker.rest.controller;

import com.example.aiexpensetracker.core.service.manager.IServiceManager;
import com.example.aiexpensetracker.core.service.manager.ServiceManager;
import com.example.aiexpensetracker.rest.dto.statistics.StatisticsResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final IServiceManager serviceManager;

    public StatisticsController(IServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<StatisticsResponseDTO> getStatistics(@PathVariable("email") String email) {
        StatisticsResponseDTO statistics = serviceManager.getStatisticsService().getStatisticsForUser(email);
        return ResponseEntity.ok(statistics);
    }
}
