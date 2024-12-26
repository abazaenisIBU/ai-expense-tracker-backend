package com.example.aiexpensetracker.core.service.statistics;

import com.example.aiexpensetracker.rest.dto.statistics.StatisticsResponseDTO;

public interface IStatisticsService {
    StatisticsResponseDTO getStatisticsForUser(String email);
}
