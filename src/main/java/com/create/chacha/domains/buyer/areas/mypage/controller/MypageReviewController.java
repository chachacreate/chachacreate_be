package com.create.chacha.domains.buyer.areas.mypage.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.create.chacha.common.ApiResponse;
import com.create.chacha.common.constants.ResponseCode;
import com.create.chacha.config.security.SecurityUser;
import com.create.chacha.domains.buyer.areas.mypage.service.MypageReviewService;
import com.create.chacha.domains.seller.areas.reviews.dto.response.ReviewListItemDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 구매자-마이페이지: 내가 쓴 리뷰 조회 API
 */
@Slf4j
@RestController
@RequestMapping("/api/mypage/reviews")
@RequiredArgsConstructor
public class MypageReviewController {
	
    private final MypageReviewService mypageReviewService;

    @GetMapping("/reviews")
    public ApiResponse<List<ReviewListItemDTO>> getMyReviews(
            @AuthenticationPrincipal SecurityUser principal
    ) {
        if (principal == null || principal.getMemberId() == null) {
            return new ApiResponse<>(ResponseCode.UNAUTHORIZED, null);
        }

        Long memberId = principal.getMemberId();
        log.info("[mypage-review] 리뷰 조회 요청 - memberId={}", memberId);

        List<ReviewListItemDTO> list = mypageReviewService.getReviewsByMember(memberId);
        return new ApiResponse<>(ResponseCode.OK, list);
    }
}
