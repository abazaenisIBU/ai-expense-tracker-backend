package com.example.aiexpensetracker.core.service.statistics;

import com.example.aiexpensetracker.rest.dto.statistics.StatisticsResponseDTO;

import java.util.concurrent.CompletableFuture;

public interface StatisticsService {
    CompletableFuture<StatisticsResponseDTO> getStatisticsForUser(String email);
}
