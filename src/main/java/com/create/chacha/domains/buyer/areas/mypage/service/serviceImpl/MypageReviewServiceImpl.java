package com.create.chacha.domains.buyer.areas.mypage.service.serviceImpl;

import com.create.chacha.common.util.LegacyAPIUtil;
import com.create.chacha.common.util.dto.LegacyProductDTO;
import com.create.chacha.domains.buyer.areas.mypage.service.MypageReviewService;
import com.create.chacha.domains.seller.areas.reviews.dto.response.ReviewListItemDTO;
import com.create.chacha.domains.shared.repository.MypageReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MypageReviewServiceImpl implements MypageReviewService {

    private final MypageReviewRepository reviewRepository;
    private final LegacyAPIUtil legacyAPIUtil;

    @Override
    @Transactional(readOnly = true)
    public List<ReviewListItemDTO> getReviewsByMember(Long memberId) {
        // DB에서 DTO 바로 수신 (레거시 필드만 null)
        List<ReviewListItemDTO> items = reviewRepository.findReviewsByMemberId(memberId);

        // productId 상품당 1회만 레거시 호출
        Map<Long, LegacyProductDTO> productMap = items.stream()
                .map(ReviewListItemDTO::getProductId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toMap(
                        pid -> pid,
                        pid -> {
                            try {
                                return legacyAPIUtil.getLegacyProductData(pid);
                            } catch (Exception e) {
                                log.error("[mypage-review] 레거시 상품 조회 실패 - productId={}", pid, e);
                                return null;
                            }
                        }
                ));

        //  레거시 결과 DTO에 채워넣기
        for (ReviewListItemDTO dto : items) {
            LegacyProductDTO p = productMap.get(dto.getProductId());
            if (p != null) {
                dto.setProductName(p.getProductName());
                dto.setProductThumbnailUrl(p.getThumbnailUrl());
            }
            if (dto.getLikeCount() == null) dto.setLikeCount(0);
        }

        log.info("[mypage-review] memberId={} 리뷰 {}건", memberId, items.size());
        return items;
    }
}
