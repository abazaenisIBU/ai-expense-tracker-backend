package com.example.aiexpensetracker.core.service.statistics;

import com.example.aiexpensetracker.rest.dto.statistics.StatisticsResponseDTO;

import java.util.concurrent.CompletableFuture;

/**
 * Service interface for retrieving statistical data related to user expenses in the AI Expense Tracker application.
 * Provides an asynchronous method to calculate statistics for a user's expenses.
 */
public interface StatisticsService {

    /**
     * Retrieves statistics for a user's expenses, including categorized and monthly breakdowns.
     *
     * @param email the email of the user whose expense statistics are to be calculated.
     * @return a CompletableFuture containing a StatisticsResponseDTO object with the user's categorized and monthly expense statistics.
     */
    CompletableFuture<StatisticsResponseDTO> getStatisticsForUser(String email);
}
