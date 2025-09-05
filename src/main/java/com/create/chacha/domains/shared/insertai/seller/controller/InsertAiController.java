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
    
    @PostMapping("/product-description")
    public ResponseEntity<Map<String, String>> generateProductDescription(@RequestBody Map<String, Object> req) {
        String name   = (String) req.getOrDefault("name", "");
        String prompt = (String) req.getOrDefault("prompt", "");

        // 선택 파라미터(있으면 힌트로 사용)
        String catL = (String) req.getOrDefault("categoryLarge", "");
        String catM = (String) req.getOrDefault("categoryMiddle", "");
        String catS = (String) req.getOrDefault("categorySmall", "");
        Number price = (Number) req.getOrDefault("price", 0);

        String system = """
            너는 전자상거래 상품 상세페이지용 소개글을 작성하는 어시스턴트야.
            - 출력은 markdown 형식
            - 말투: 친절/담백, 과장 금지, 사실 위주
            - 구성 가이드:
              # 상품명
              (짧은 인트로 1~2문장)
              ## 핵심 특징
              - 불릿 3~6개
              ## 상세 스펙
              - 소재/사이즈/무게/구성/호환 등(가능한 항목만)
              ## 사용법 & 관리
              - 사용 팁/세척/보관
              ## 추천 대상/상황
              - 이런 분께 추천
              ## 배송/교환 안내(선택)
            - 이미지 대체텍스트는 넣지 않음
            - 안전/의료/효능 등 법적 리스크 표현은 피함
            """;

        String user = """
            상품명: %s
            카테고리: %s > %s > %s
            가격(참고용): %s
            판매자 요청/참고 메모:
            %s
            """.formatted(name, catL, catM, catS, price, prompt);

        String content = openAiService.generateText(system, user);
        if (content == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "설명 생성 실패"));
        }
        return ResponseEntity.ok(Map.of("content", content));
    }
}
