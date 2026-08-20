package dev.faizarfi.utility.apierrorhelper;

import dev.faizarfi.utility.apierrorhelper.annotation.SkipResponseWrapping;
import dev.faizarfi.utility.apierrorhelper.config.ApiErrorResponseAutoConfiguration;
import dev.faizarfi.utility.apierrorhelper.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ApiResponseAutoConfigurationTest.TestApplication.class)
@AutoConfigureMockMvc
public class ApiResponseAutoConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @SpringBootApplication(scanBasePackageClasses = ApiErrorResponseAutoConfiguration.class)
    @RestController
    public static class TestApplication {

        @GetMapping("/test/success")
        public Map<String, String> successEndpoint() {
            return Map.of("message", "Hello Spring Boot Starter!");
        }

        @GetMapping("/test/skip")
        @SkipResponseWrapping
        public String skipEndpoint() {
            return "Raw String Payload";
        }

        @GetMapping("/test/error")
        public String errorEndpoint() {
            throw new ApiException("Entity with ID 42 not found", HttpStatus.NOT_FOUND);
        }
    }

    @Test
    void shouldWrapSuccessResponseInEnvelope() throws Exception {
        mockMvc.perform(get("/test/success"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.message").value("Hello Spring Boot Starter!"))
                .andExpect(jsonPath("$.path").value("/test/success"));
    }

    @Test
    void shouldSkipWrappingWhenAnnotated() throws Exception {
        mockMvc.perform(get("/test/skip"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").doesNotExist());
    }

    @Test
    void shouldHandleApiExceptionAndFormatEnvelope() throws Exception {
        mockMvc.perform(get("/test/error"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Entity with ID 42 not found"))
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    }
}