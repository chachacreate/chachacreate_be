package com.create.chacha.domains.buyer.areas.classes.classdetail.service.serviceimpl;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.create.chacha.domains.buyer.areas.classes.classdetail.dto.response.ClassSummaryResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.classdetail.repository.ClassInfoRepository;
import com.create.chacha.domains.buyer.areas.classes.classdetail.repository.StoreRepository;
import com.create.chacha.domains.shared.entity.classcore.ClassInfoEntity;
import com.create.chacha.domains.shared.entity.store.StoreEntity;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClassDetailQueryServiceImpl implements com.create.chacha.domains.buyer.areas.classes.classdetail.service.ClassDetailQueryService {

    private final ClassInfoRepository classInfoRepository;
    private final StoreRepository storeRepository;

    @Override
    public ClassSummaryResponseDTO getSummary(Long classId) {
        ClassInfoEntity ci = classInfoRepository.findByclassId(classId)
                .orElseThrow(() -> new NoSuchElementException("클래스를 찾을 수 없습니다: " + classId));

        StoreEntity store = storeRepository.findById(ci.getStore().getId())
                .orElseThrow(() -> new NoSuchElementException("스토어를 찾을 수 없습니다: " + ci.getStore().getId()));

        return ClassSummaryResponseDTO.builder()
                .classId(ci.getId())
                .title(ci.getTitle())
                .description(ci.getDetail())
                .price(ci.getPrice())
                .postNum(ci.getPostNum())
                .addressRoad(ci.getAddressRoad())
                .addressDetail(ci.getAddressDetail())
                .addressExtra(ci.getAddressExtra())
                .storeId(store.getId())
                .storeName(store.getName())
                .build();
    }

    // 아래 다른 API 메서드가 같은 클래스에 이어집니다.
}
