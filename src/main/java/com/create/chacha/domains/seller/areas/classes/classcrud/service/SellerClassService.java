package com.create.chacha.domains.seller.areas.classes.classcrud.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.create.chacha.domains.seller.areas.classes.classcrud.dto.request.ClassCreateRequestDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.response.ClassDeletionToggleResponseDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.response.ClassListItemResponseDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.repository.ClassImageRepository;
import com.create.chacha.domains.seller.areas.classes.classcrud.repository.SellerClassesRepository;
import com.create.chacha.domains.seller.areas.classes.classcrud.repository.StoreRepository;
import com.create.chacha.domains.seller.areas.classes.classcrud.service.serviceimpl.SellerClassServiceImpl;
import com.create.chacha.domains.shared.constants.ImageStatusEnum;
import com.create.chacha.domains.shared.entity.classcore.ClassImageEntity;
import com.create.chacha.domains.shared.entity.classcore.ClassInfoEntity;
import com.create.chacha.domains.shared.entity.store.StoreEntity;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerClassService implements SellerClassServiceImpl {

    private final SellerClassesRepository classRepo;
    private final ClassImageRepository imageRepo;
    private final StoreRepository storeRepo;

    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    // 클래스 논리적 삭제 update
    @Override
    @Transactional
    public ClassDeletionToggleResponseDTO toggleClassesDeletion(String storeUrl, List<Long> classIds) {
        if (classIds == null || classIds.isEmpty()) {
            return ClassDeletionToggleResponseDTO.builder()
                    .requestedCount(0)
                    .toggledToDeletedCount(0)
                    .toggledToRestoredCount(0)
                    .toggledToDeletedIds(List.of())
                    .toggledToRestoredIds(List.of())
                    .notFoundOrMismatchedIds(List.of())
                    .build();
        }

        // 스토어 확인
        StoreEntity store = storeRepo.findByUrl(storeUrl)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스토어: " + storeUrl));

        // 소속 + ID 일치 항목 조회
        List<ClassInfoEntity> belongToStore = classRepo.findAllByIdInAndStore_Url(classIds, store.getUrl());

        // 미존재/소속불일치 계산
        Set<Long> requestedSet = new HashSet<>(classIds);
        Set<Long> foundSet = new HashSet<>();
        for (ClassInfoEntity c : belongToStore) foundSet.add(c.getId());
        List<Long> notFoundOrMismatched = new ArrayList<>();
        for (Long id : requestedSet) if (!foundSet.contains(id)) notFoundOrMismatched.add(id);

        LocalDateTime now = LocalDateTime.now();
        List<Long> toggledToDeleted = new ArrayList<>();
        List<Long> toggledToRestored = new ArrayList<>();

        // 토글: 0->1 (삭제), 1->0 (복구)
        for (ClassInfoEntity c : belongToStore) {
            boolean current = Boolean.TRUE.equals(c.getIsDeleted());
            if (current) {
                // 1 -> 0 (복구)
                c.setIsDeleted(false);
                c.setDeletedAt(null);
                toggledToRestored.add(c.getId());
            } else {
                // 0 -> 1 (삭제)
                c.setIsDeleted(true);
                c.setDeletedAt(now);
                toggledToDeleted.add(c.getId());
            }
        }

        if (!belongToStore.isEmpty()) {
            classRepo.saveAll(belongToStore);
        }

        return ClassDeletionToggleResponseDTO.builder()
                .requestedCount(classIds.size())
                .toggledToDeletedCount(toggledToDeleted.size())
                .toggledToRestoredCount(toggledToRestored.size())
                .toggledToDeletedIds(toggledToDeleted)
                .toggledToRestoredIds(toggledToRestored)
                .notFoundOrMismatchedIds(notFoundOrMismatched)
                .build();
    }
    
    // 클래스 리스트 조회
    @Override
    public List<ClassListItemResponseDTO> getClassesByStoreUrl(String storeUrl) {
        // storeUrl 소속의 모든 클래스(삭제/미삭제 포함)
        List<ClassInfoEntity> infos = classRepo.findAllByStore_Url(storeUrl);

        return infos.stream()
                .map(this::toClassListItemResponseDTO)
                .collect(Collectors.toList());
    }

    // DTO 매핑
    private ClassListItemResponseDTO toClassListItemResponseDTO(ClassInfoEntity info) {
        // 대표 썸네일 URL (THUMBNAIL & seq=1 & 이미지 미삭제)
        String thumbnailUrl = imageRepo
                .findFirstByClassInfo_IdAndStatusAndImageSequenceAndIsDeletedFalseOrderByIdAsc(
                        info.getId(), ImageStatusEnum.THUMBNAIL, 1
                )
                .map(ClassImageEntity::getUrl)
                .orElse(null);

        // 주소 한 줄(널/빈값 제외, 스페이스 1칸)
        String location = joinWithSpace(
                info.getPostNum(),
                info.getAddressRoad(),
                info.getAddressDetail(),
                info.getAddressExtra()
        );

        // 기간 문자열: "yyyy-MM-dd ~ yyyy-MM-dd"
        String period = null;
        if (info.getStartDate() != null && info.getEndDate() != null) {
            period = info.getStartDate().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    + " ~ "
                    + info.getEndDate().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        return ClassListItemResponseDTO.builder()
                .classId(info.getId())
                .thumbnailUrl(thumbnailUrl)
                .title(nvl(info.getTitle()))
                .location(location)
                .participant(info.getParticipant())
                .price(info.getPrice())
                .period(period)
                .createdAt(info.getCreatedAt())
                .updatedAt(info.getUpdatedAt())
                .deletedAt(info.getDeletedAt())
                .isDeleted(Boolean.TRUE.equals(info.getIsDeleted()))
                .build();
    }

    // 유틸: 공백 1칸으로 연결
    private String joinWithSpace(String... parts) {
        return java.util.Arrays.stream(parts)
                .filter(p -> p != null && !p.isBlank())
                .collect(Collectors.joining(" "));
    }
    
    // 클래스 등록
    @Override
    @Transactional
    public List<Long> createClasses(String storeUrl, List<ClassCreateRequestDTO> requests) {
        // Store 찾기 (URL 기준)
        StoreEntity store = storeRepo.findByUrl(storeUrl)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스토어: " + storeUrl));

        List<Long> createdIds = new ArrayList<>();

        for (ClassCreateRequestDTO req : requests) {
            var c = req.getClazz();

            // 클래스 기본 정보 생성
            ClassInfoEntity classInfo = ClassInfoEntity.builder()
                    .store(store)
                    .title(nvl(c.getTitle()))
                    .detail(nvl(c.getDetail()))
                    .price(c.getPrice())
                    .guideline(nvl(c.getGuideline()))
                    .participant(c.getParticipant())
                    .postNum(nvl(c.getPostNum()))
                    .addressRoad(nvl(c.getAddressRoad()))
                    .addressDetail(nvl(c.getAddressDetail()))
                    .addressExtra(nvl(c.getAddressExtra()))
                    .startDate(parseDateTime(c.getStartDate()))
                    .endDate(parseDateTime(c.getEndDate()))
                    .startTime(parseTime(c.getStartTime()))
                    .endTime(parseTime(c.getEndTime()))
                    .timeInterval(c.getTimeInterval())
                    .build();

            if (classInfo.getIsDeleted() == null) {
                classInfo.setIsDeleted(Boolean.FALSE);
            }

            ClassInfoEntity saved = classRepo.save(classInfo);

            // ===== 이미지 저장 (자동 시퀀스) =====
            if (req.getImages() != null && !req.getImages().isEmpty()) {
                // 상태별로 분리
                List<ClassCreateRequestDTO.ClassImagePayload> thumbs = new ArrayList<>();
                List<ClassCreateRequestDTO.ClassImagePayload> descs  = new ArrayList<>();

                for (var img : req.getImages()) {
                    if (img.getStatus() == null) {
                        throw new IllegalArgumentException("이미지 status는 필수입니다. (THUMBNAIL 또는 DESCRIPTION)");
                    }
                    if (isBlank(img.getUrl())) {
                        throw new IllegalArgumentException("이미지 URL은 필수입니다.");
                    }
                    if (img.getStatus() == ImageStatusEnum.THUMBNAIL) {
                        thumbs.add(img);
                    } else if (img.getStatus() == ImageStatusEnum.DESCRIPTION) {
                        descs.add(img);
                    } else {
                        throw new IllegalArgumentException("알 수 없는 이미지 상태: " + img.getStatus());
                    }
                }

                // 클라가 보낸 imageSequence가 있으면 그 순서대로 정렬, 없으면 입력 순서 유지
                Comparator<ClassCreateRequestDTO.ClassImagePayload> byClientSeq =
                        Comparator.comparing(ClassCreateRequestDTO.ClassImagePayload::getImageSequence,
                                Comparator.nullsLast(Integer::compareTo));

                boolean thumbsHasSeq = thumbs.stream().anyMatch(i -> i.getImageSequence() != null);
                boolean descsHasSeq  = descs.stream().anyMatch(i -> i.getImageSequence() != null);

                if (thumbsHasSeq) thumbs.sort(byClientSeq);
                if (descsHasSeq)  descs.sort(byClientSeq);

                // 썸네일 제한 및 자동 부여 1..3
                if (thumbs.size() > 3) {
                    throw new IllegalArgumentException("썸네일 이미지는 최대 3장까지만 등록할 수 있습니다.");
                }

                List<ClassImageEntity> images = new ArrayList<>();

                int thumbSeq = 1;
                for (var img : thumbs) {
                    ClassImageEntity e = ClassImageEntity.builder()
                            .classInfo(saved)
                            .url(img.getUrl().trim())
                            .status(ImageStatusEnum.THUMBNAIL)
                            .imageSequence(thumbSeq++)   // 1,2,3 자동
                            .build();
                    e.setIsDeleted(Boolean.FALSE);
                    images.add(e);
                }

                // 설명 이미지는 제한 없음, 자동 부여 1..N
                int descSeq = 1;
                for (var img : descs) {
                    ClassImageEntity e = ClassImageEntity.builder()
                            .classInfo(saved)
                            .url(img.getUrl().trim())
                            .status(ImageStatusEnum.DESCRIPTION)
                            .imageSequence(descSeq++)    // 1,2,3,4,5...
                            .build();
                    e.setIsDeleted(Boolean.FALSE);
                    images.add(e);
                }

                imageRepo.saveAll(images);
            }

            createdIds.add(saved.getId());
        }

        return createdIds;
    }

    // ===== 유틸 =====
    private static String nvl(String s) { return (s == null) ? "" : s; }

    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    private static LocalDateTime parseDateTime(String s) {
        if (s == null || s.isBlank()) return null;
        return LocalDateTime.parse(s.trim(), DATETIME_FMT);
    }

    private static LocalTime parseTime(String s) {
        if (s == null || s.isBlank()) return null;
        return LocalTime.parse(s.trim(), TIME_FMT);
    }
}