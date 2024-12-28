package com.example.aiexpensetracker.api.impl.sendgridsender;

import com.example.aiexpensetracker.core.api.mailsender.MailSender;
import com.example.aiexpensetracker.rest.dto.report.UserReportResponseDTO;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class SendGridSender implements MailSender {

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Override
    public void sendEmails(List<UserReportResponseDTO> reports) {
        SendGrid sendGrid = new SendGrid(sendGridApiKey);

        for (UserReportResponseDTO report : reports) {
            String emailContent = generateEmailContent(report);

            com.sendgrid.helpers.mail.objects.Email fromEmail = new com.sendgrid.helpers.mail.objects.Email("enis.abaza@stu.ibu.edu.ba");
            com.sendgrid.helpers.mail.objects.Email toEmail = new com.sendgrid.helpers.mail.objects.Email(report.getUserEmail());
            Content content = new Content("text/html", emailContent);

            Mail mail = new Mail(fromEmail, "Monthly Expense Report", toEmail, content);

            try {
                Request request = new Request();
                request.setMethod(Method.POST);
                request.setEndpoint("mail/send");
                request.setBody(mail.build());
                Response response = sendGrid.api(request);

                System.out.println("Email sent to " + report.getUserEmail() + ": " + response.getStatusCode());
            } catch (IOException ex) {
                System.err.println("Failed to send email to " + report.getUserEmail() + ": " + ex.getMessage());
            }
        }
    }

    private String generateEmailContent(UserReportResponseDTO report) {
        StringBuilder content = new StringBuilder();
        content.append("<h1>Monthly Expense Report</h1>");
        content.append("<p>Total Expenses: ").append(report.getTotalAmount()).append("</p>");
        content.append("<h2>Expenses by Category</h2>");
        content.append("<ul>");
        for (var category : report.getCategoryTotals()) {
            content.append("<li>")
                    .append(category.getCategoryName())
                    .append(": ")
                    .append(category.getTotalAmount())
                    .append("</li>");
        }
        content.append("</ul>");
        content.append("<h2>All Expenses</h2>");
        content.append("<ul>");
        for (var expense : report.getExpenses()) {
            content.append("<li>")
                    .append("Date: ").append(expense.getDate())
                    .append(", Amount: ").append(expense.getAmount())
                    .append(", Description: ").append(expense.getDescription())
                    .append("</li>");
        }
        content.append("</ul>");
        return content.toString();
    }
}

