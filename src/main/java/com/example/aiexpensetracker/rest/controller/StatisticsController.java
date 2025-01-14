package com.example.aiexpensetracker.rest.controller;

import com.example.aiexpensetracker.core.service.manager.ServiceManager;
import com.example.aiexpensetracker.rest.dto.statistics.StatisticsResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

/**
 * REST controller for handling user statistics in the AI Expense Tracker application.
 * Provides an endpoint to retrieve detailed statistics for a specific user.
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final ServiceManager serviceManager;

    /**
     * Constructs a new StatisticsController with the provided ServiceManager.
     *
     * @param serviceManager the ServiceManager instance that provides access to various services.
     */
    public StatisticsController(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    /**
     * Retrieves statistical data for a specific user.
     *
     * @param email the email address of the user whose statistics are to be retrieved.
     * @return a CompletableFuture containing a ResponseEntity with the user's statistics in a StatisticsResponseDTO.
     */
    @GetMapping("/user/{email}")
    public CompletableFuture<ResponseEntity<StatisticsResponseDTO>> getStatistics(
            @PathVariable("email") String email
    ) {
        return serviceManager.getStatisticsService()
                .getStatisticsForUser(email)
                .thenApply(ResponseEntity::ok);
    }
}
