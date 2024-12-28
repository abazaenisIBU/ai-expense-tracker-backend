package com.example.aiexpensetracker.core.api.mailsender;

import com.example.aiexpensetracker.rest.dto.report.UserReportResponseDTO;

import java.util.List;

public interface MailSender {
    void sendEmails(List<UserReportResponseDTO> reports);
}
