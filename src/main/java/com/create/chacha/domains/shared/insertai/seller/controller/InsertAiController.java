package com.create.chacha.domains.shared.insertai.seller.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.create.chacha.domains.shared.insertai.seller.service.OpenAiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class InsertAiController {

    private final OpenAiService openAiService;

    @PostMapping("/class-description")
    public ResponseEntity<Map<String, String>> generateClassDescription(@RequestBody Map<String, Object> req) {
        String prompt = (String) req.getOrDefault("prompt", "");
        String title  = (String) req.getOrDefault("title", "");
        // 필요하면 더 받기: capacity, price, tone, length 등

        String system = """
            너는 공방/클래스 소개글을 써 주는 어시스턴트야.
            - markdown 형식으로 작성
            - 문단/리스트를 적절히 섞고, 과장 없이 친절하게
            - 초보자도 이해할 수 있게 커리큘럼, 난이도, 준비물, 주의사항 등을 포함
            """;

        String user = """
            클래스명: %s
            판매자 요청: %s
            """.formatted(title, prompt);

        String content = openAiService.generateText(system, user);
        if (content == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "설명 생성 실패"));
        }
        // 프론트 공통 응답 포맷을 쓰는 경우:
        // return ResponseEntity.ok(Map.of("data", content, "status", "200", "message", "OK"));
        return ResponseEntity.ok(Map.of("content", content));
    }
}
