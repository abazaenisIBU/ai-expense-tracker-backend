package com.example.aiexpensetracker.rest.dto.statistics;

import java.util.List;

public class StatisticsResponseDTO {
    private List<CategoryStatisticsDTO> categoryStatistics;
    private List<MonthlyStatisticsDTO> monthlyStatistics;

    // Constructor
    public StatisticsResponseDTO(List<CategoryStatisticsDTO> categoryStatistics, List<MonthlyStatisticsDTO> monthlyStatistics) {
        this.categoryStatistics = categoryStatistics;
        this.monthlyStatistics = monthlyStatistics;
    }

    // Default Constructor
    public StatisticsResponseDTO() {}

    // Getters and Setters
    public List<CategoryStatisticsDTO> getCategoryStatistics() {
        return categoryStatistics;
    }

    public void setCategoryStatistics(List<CategoryStatisticsDTO> categoryStatistics) {
        this.categoryStatistics = categoryStatistics;
    }

    public List<MonthlyStatisticsDTO> getMonthlyStatistics() {
        return monthlyStatistics;
    }

    public void setMonthlyStatistics(List<MonthlyStatisticsDTO> monthlyStatistics) {
        this.monthlyStatistics = monthlyStatistics;
    }
}
