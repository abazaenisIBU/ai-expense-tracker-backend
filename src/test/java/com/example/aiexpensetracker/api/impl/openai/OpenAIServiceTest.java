package com.example.aiexpensetracker.api.impl.openai;

import com.example.aiexpensetracker.rest.dto.category.CategorySuggestionResponseDTO;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.service.OpenAiService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OpenAIServiceTest {

    @Test
    void suggestCategory_parsesJsonAndReturnsDto() {
        //Arrange
        OpenAiService oaMock = mock(OpenAiService.class);

        String json = """
                {"categoryName":"Food","isNew":false,"status":200}""";
        CompletionChoice choice = new CompletionChoice();
        choice.setText(json);
        CompletionResult result = new CompletionResult();
        result.setChoices(List.of(choice));

        when(oaMock.createCompletion(any(CompletionRequest.class))).thenReturn(result);

        OpenAIService service = new OpenAIService(oaMock);

        List<String> categories = List.of("Food", "Travel");
        String description = "Bought pizza";

        //Act
        CategorySuggestionResponseDTO dto = service.suggestCategory(description, categories);

        //Assert
        assertEquals("Food", dto.getCategoryName());
        assertFalse(dto.getIsNew());
        assertEquals(200, dto.getStatus());

        ArgumentCaptor<CompletionRequest> captor = ArgumentCaptor.forClass(CompletionRequest.class);
        verify(oaMock).createCompletion(captor.capture());
        String actualPrompt = captor.getValue().getPrompt();
        assertTrue(actualPrompt.contains("Food") && actualPrompt.contains("Travel"));
        assertTrue(actualPrompt.contains(description));
    }

    @Test
    void suggestCategory_whenOpenAIThrows_returnsDefaultErrorDto() {
        //Arrange
        OpenAiService oaMock = mock(OpenAiService.class);
        when(oaMock.createCompletion(any())).thenThrow(new RuntimeException("OpenAI down"));

        OpenAIService service = new OpenAIService(oaMock);

        //Act
        CategorySuggestionResponseDTO dto =
                service.suggestCategory("Whatever", List.of("Foo"));

        //Assert
        assertNull(dto.getCategoryName());
        assertNull(dto.getIsNew());
        assertEquals(400, dto.getStatus());
    }
}