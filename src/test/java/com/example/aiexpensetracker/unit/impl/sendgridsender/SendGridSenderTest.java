package com.example.aiexpensetracker.unit.impl.sendgridsender;

import com.example.aiexpensetracker.api.impl.sendgridsender.SendGridSender;
import com.example.aiexpensetracker.rest.dto.report.CategoryTotalDTO;
import com.example.aiexpensetracker.rest.dto.report.UserReportResponseDTO;
import com.example.aiexpensetracker.rest.dto.statistics.ExpenseDTO;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SendGridSenderTest {

    private SendGridSender sender;

    @BeforeEach
    void setUp() throws Exception {
        sender = new SendGridSender();

        var f = SendGridSender.class.getDeclaredField("sendGridApiKey");
        f.setAccessible(true);
        f.set(sender, "dummy-key");
    }

    @Test
    void sendEmails_callsSendGridOncePerReport_andGeneratesHtml() throws Exception {

        //Arrange
        Response ok = new Response();
        ok.setStatusCode(202);

        try (MockedConstruction<SendGrid> mocked = mockConstruction(
                SendGrid.class,
                (mock, context) -> when(mock.api(any(Request.class))).thenReturn(ok))) {

            UserReportResponseDTO report = getUserReportResponseDTO();

            //Act
            sender.sendEmails(List.of(report));

            //Assert:
            assertEquals(1, mocked.constructed().size(),
                    "Exactly one SendGrid instance should have been constructed");

            SendGrid constructed = mocked.constructed().getFirst();

            var captor = mocked.constructed().getFirst();
            verify(constructed, times(1)).api(any(Request.class));

            ArgumentCaptor<Request> reqCaptor = ArgumentCaptor.forClass(Request.class);
            verify(constructed).api(reqCaptor.capture());
            Request req = reqCaptor.getValue();

            assertEquals(Method.POST, req.getMethod());
            assertEquals("mail/send", req.getEndpoint());

            // Basic sanity
            String body = req.getBody();
            assertTrue(body.contains("<h1>Monthly Expense Report</h1>"));
            assertTrue(body.contains("Total Expenses: 24.50"));
            assertTrue(body.contains("Pizza"), "Body should list expense description");
            assertTrue(body.contains("Food: 24.50"), "Body should list category totals");
        }
    }

    private static UserReportResponseDTO getUserReportResponseDTO() {
        CategoryTotalDTO cat = new CategoryTotalDTO("Food", new BigDecimal("24.50"));

        ExpenseDTO expense = new ExpenseDTO(
                1L,
                new BigDecimal(100),
                LocalDate.of(2025, 6, 1),
                "Pizza"
        );

        return new UserReportResponseDTO(
                "alice@example.com",
                List.of(expense),
                new BigDecimal("24.50"),
                List.of(cat)
        );
    }

    @Test
    void sendEmails_whenApiThrows_isSwallowedAndLoopContinues() throws Exception {

        try (MockedConstruction<SendGrid> mocked = mockConstruction(
                SendGrid.class,
                (mock, ctx) -> when(mock.api(any(Request.class)))
                        .thenThrow(new IOException("SendGrid down"))
        )) {

            UserReportResponseDTO report = new UserReportResponseDTO(
                    "enis@example.com",
                    List.of(),
                    BigDecimal.ZERO,
                    List.of()
            );

            // Act & Assert
            assertDoesNotThrow(() -> sender.sendEmails(List.of(report)));

            SendGrid sg = mocked.constructed().getFirst();
            verify(sg, times(1)).api(any(Request.class));
        }
    }
}
