package com.create.chacha.domains.buyer.areas.mypage.service;


import java.util.List;

import com.create.chacha.domains.seller.areas.reviews.dto.response.ReviewListItemDTO;

/**
 * 구매자-마이페이지: 리뷰 조회 서비스
 */
public interface MypageReviewService {

    /**
     * 회원이 작성한 리뷰 목록 조회
     */
    List<ReviewListItemDTO> getReviewsByMember(Long memberId);
}
