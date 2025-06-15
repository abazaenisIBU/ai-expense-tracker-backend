package com.example.aiexpensetracker.unit.rest.configuration.GlobalCorsConfiguration;

import com.example.aiexpensetracker.rest.configuration.GlobalCorsConfiguration.GlobalCorsConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalCorsConfigurationTest {

    @Test
    void corsFilterPermissiveConfig() throws Exception {
        GlobalCorsConfiguration config = new GlobalCorsConfiguration();
        CorsFilter filter = config.corsFilter();
        assertNotNull(filter);

        // Grab the private 'configSource' field
        Field srcField = CorsFilter.class.getDeclaredField("configSource");
        srcField.setAccessible(true);
        UrlBasedCorsConfigurationSource source =
                (UrlBasedCorsConfigurationSource) srcField.get(filter);

        Map<String, CorsConfiguration> allConfigs =
                (Map<String, CorsConfiguration>) source.getCorsConfigurations();

        // There should be an entry for "/**"
        assertTrue(allConfigs.containsKey("/**"), "Should have registered /**");
        CorsConfiguration corsConfig = allConfigs.get("/**");
        assertNotNull(corsConfig);

        List<String> origins = corsConfig.getAllowedOrigins();
        assertTrue(origins.contains("https://ai-expense-tracker-frontend.onrender.com"));
        assertTrue(origins.contains("http://localhost:3000"));

        assertEquals(List.of("*"), corsConfig.getAllowedHeaders());
        assertEquals(List.of("*"), corsConfig.getAllowedMethods());
    }
}
