package com.example.aiexpensetracker.rest.controller;

import com.example.aiexpensetracker.core.service.manager.ServiceManager;
import com.example.aiexpensetracker.rest.dto.statistics.StatisticsResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final ServiceManager serviceManager;

    public StatisticsController(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    @GetMapping("/user/{email}")
    public CompletableFuture<ResponseEntity<StatisticsResponseDTO>> getStatistics(
            @PathVariable("email") String email
    ) {
        return serviceManager.getStatisticsService()
                .getStatisticsForUser(email)
                .thenApply(ResponseEntity::ok);
    }
}
