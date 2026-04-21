package com.hypo.appstoreprice.logging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
@Import(ApiLogInterceptorTest.TestLoggingController.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class ApiLogInterceptorTest {

    private final MockMvc mockMvc;

    ApiLogInterceptorTest(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldLogRequestResponseAndCost(CapturedOutput output) throws Exception {
        mockMvc.perform(post("/test/logging/echo")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"codex\"}"))
            .andExpect(status().isOk());

        assertThat(output.getOut()).contains("/test/logging/echo");
        assertThat(output.getOut()).contains("\"name\":\"codex\"");
        assertThat(output.getOut()).contains("\"message\":\"成功\"");
        assertThat(output.getOut()).contains("costMs");
    }

    @Test
    void shouldSkipMultipartFileContent(CapturedOutput output) throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "secret".getBytes());

        mockMvc.perform(multipart("/test/logging/upload")
                .file(file)
                .param("name", "codex"))
            .andExpect(status().isOk());

        assertThat(output.getOut()).contains("/test/logging/upload");
        assertThat(output.getOut()).contains("name");
        assertThat(output.getOut()).doesNotContain("secret");
        assertThat(output.getOut()).doesNotContain("hello.txt");
    }

    @Controller
    @RequestMapping("/test/logging")
    static class TestLoggingController {

        @PostMapping("/echo")
        @ResponseBody
        EchoResponse echo(@RequestBody EchoRequest request) {
            return new EchoResponse(request.name());
        }

        @PostMapping("/upload")
        @ResponseBody
        String upload(@RequestParam("name") String name, @RequestParam("file") MultipartFile file) {
            return name + "-" + file.getSize();
        }
    }

    record EchoRequest(String name) {
    }

    record EchoResponse(String name) {
    }
}
