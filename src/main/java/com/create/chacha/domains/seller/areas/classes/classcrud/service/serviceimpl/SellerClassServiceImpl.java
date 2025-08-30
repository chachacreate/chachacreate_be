package com.create.chacha.domains.seller.areas.classes.classcrud.service.serviceimpl;

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
import org.springframework.web.multipart.MultipartFile;

import com.create.chacha.common.util.S3Uploader;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.request.ClassCreateRequestDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.request.ClassUpdateRequestDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.response.ClassDeletionToggleResponseDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.response.ClassListItemResponseDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.response.ClassUpdateFormResponseDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.response.ClassUpdateResponseDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.repository.ClassImageRepository;
import com.create.chacha.domains.seller.areas.classes.classcrud.repository.SellerClassesRepository;
import com.create.chacha.domains.seller.areas.classes.classcrud.repository.StoreRepository;
import com.create.chacha.domains.seller.areas.classes.classcrud.service.SellerClassService;
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
public class SellerClassServiceImpl implements SellerClassService {

    private final SellerClassesRepository classRepo;
    private final ClassImageRepository imageRepo;
    private final StoreRepository storeRepo;

    private final S3Uploader s3Uploader;

    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter TIME_FMT     = DateTimeFormatter.ofPattern("HH:mm:ss");

    // ===== 클래스 수정 (부분 교체 규칙 적용) =====
    @Override
    @Transactional
    public ClassUpdateResponseDTO updateClass(String storeUrl, Long classId, ClassUpdateRequestDTO req) {
        ClassInfoEntity info = classRepo.findByIdAndStore_Url(classId, storeUrl)
                .orElseThrow(() -> new IllegalArgumentException("해당 스토어에 속한 클래스가 없습니다. id=" + classId));

        // 본문 업데이트 (부분 수정 가능)
        var c = req.getClazz();
        if (c != null) {
            if (c.getTitle()        != null) info.setTitle(nvl(c.getTitle()));
            if (c.getDetail()       != null) info.setDetail(nvl(c.getDetail()));
            if (c.getPrice()        != null) info.setPrice(c.getPrice());
            if (c.getGuideline()    != null) info.setGuideline(nvl(c.getGuideline()));
            if (c.getParticipant()  != null) info.setParticipant(c.getParticipant());
            if (c.getPostNum()      != null) info.setPostNum(nvl(c.getPostNum()));
            if (c.getAddressRoad()  != null) info.setAddressRoad(nvl(c.getAddressRoad()));
            if (c.getAddressDetail()!= null) info.setAddressDetail(nvl(c.getAddressDetail()));
            if (c.getAddressExtra() != null) info.setAddressExtra(nvl(c.getAddressExtra()));
            if (c.getStartDate()    != null) info.setStartDate(parseDateTime(c.getStartDate()));
            if (c.getEndDate()      != null) info.setEndDate(parseDateTime(c.getEndDate()));
            if (c.getStartTime()    != null) info.setStartTime(parseTime(c.getStartTime()));
            if (c.getEndTime()      != null) info.setEndTime(parseTime(c.getEndTime()));
            if (c.getTimeInterval() != null) info.setTimeInterval(c.getTimeInterval());
            classRepo.save(info);
        }

        int updatedThumb = 0, addedDesc = 0, deletedDesc = 0;

        // ---------- 썸네일 부분 교체 ----------
        MultipartFile[] thumbs = req.getThumbnails();
        Integer[] thumbSeqs = req.getThumbnailSeqs();

        if (thumbs != null && thumbs.length > 0) {
            if (thumbSeqs == null || thumbSeqs.length != thumbs.length) {
                throw new IllegalArgumentException("thumbnailSeqs 길이가 thumbnails와 일치해야 합니다.");
            }
            // 중복/범위 체크
            Set<Integer> seen = new HashSet<>();
            for (Integer seq : thumbSeqs) {
                if (seq == null || seq < 1 || seq > 3) {
                    throw new IllegalArgumentException("썸네일 시퀀스는 1~3이어야 합니다. seq=" + seq);
                }
                if (!seen.add(seq)) {
                    throw new IllegalArgumentException("썸네일 시퀀스에 중복이 있습니다. seq=" + seq);
                }
            }

            for (int i = 0; i < thumbs.length; i++) {
                MultipartFile f = thumbs[i];
                Integer seq = thumbSeqs[i];
                if (f == null || f.isEmpty()) {
                    throw new IllegalArgumentException("썸네일 파일이 비어 있습니다. index=" + i);
                }

                // 1) 새 업로드
                String newOriginalKey;
                try {
                    newOriginalKey = s3Uploader.uploadImage(f);
                } catch (Exception ex) {
                    throw new RuntimeException("썸네일 업로드 실패(index=" + i + "): " + ex.getMessage(), ex);
                }
                String newThumbUrl = s3Uploader.getThumbnailUrl(newOriginalKey);

                // 2) DB 교체 (status/sequence 유지, url만 교체)
                ClassImageEntity entity = imageRepo
                        .findByClassInfo_IdAndStatusAndImageSequence(info.getId(), ImageStatusEnum.THUMBNAIL, seq)
                        .orElseGet(() -> ClassImageEntity.builder()
                                .classInfo(info)
                                .status(ImageStatusEnum.THUMBNAIL)
                                .imageSequence(seq)
                                .build());

                String oldUrl = entity.getUrl();
                entity.setUrl(newThumbUrl);
                entity.setIsDeleted(false);
                entity.setDeletedAt(null);
                imageRepo.save(entity);
                updatedThumb++;

                // 3) 이전 S3 실제 삭제(원본+썸네일) - best effort
                if (oldUrl != null && !oldUrl.isBlank()) {
                    deleteS3PairByUrl(oldUrl);
                }
            }
        }

        // ---------- DESCRIPTION 부분 교체 ----------
        MultipartFile[] descFiles = req.getDescriptions();
        Integer[] replaceSeqs    = req.getReplaceDescriptionSeqs();

        if (descFiles != null && descFiles.length > 0) {
            if (replaceSeqs == null || replaceSeqs.length != descFiles.length) {
                throw new IllegalArgumentException("replaceDescriptionSeqs 길이가 descriptions와 일치해야 합니다.");
            }
            // 중복 체크
            Set<Integer> seen = new HashSet<>();
            for (Integer seq : replaceSeqs) {
                if (seq == null || seq < 1) {
                    throw new IllegalArgumentException("DESCRIPTION 교체 시퀀스는 1 이상의 정수여야 합니다. seq=" + seq);
                }
                if (!seen.add(seq)) {
                    throw new IllegalArgumentException("DESCRIPTION 시퀀스에 중복이 있습니다. seq=" + seq);
                }
            }

            // 현재 활성 DESCRIPTION 캐시
            List<ClassImageEntity> currentActive = imageRepo
                    .findAllByClassInfo_IdAndStatusAndIsDeletedFalse(info.getId(), ImageStatusEnum.DESCRIPTION);

            // 다음 append 시퀀스
            int nextSeq = imageRepo
                    .findTopByClassInfo_IdAndStatusOrderByImageSequenceDesc(info.getId(), ImageStatusEnum.DESCRIPTION)
                    .map(ClassImageEntity::getImageSequence).orElse(0) + 1;

            LocalDateTime now = LocalDateTime.now();

            for (int i = 0; i < descFiles.length; i++) {
                MultipartFile f = descFiles[i];
                Integer targetSeq = replaceSeqs[i];
                if (f == null || f.isEmpty()) continue;

                // 1) 새 이미지 S3 업로드
                String newOriginalKey;
                try {
                    newOriginalKey = s3Uploader.uploadImage(f);
                } catch (Exception ex) {
                    throw new RuntimeException("설명 이미지 업로드 실패(index=" + i + "): " + ex.getMessage(), ex);
                }
                String newOriginalUrl = s3Uploader.getFullUrl(newOriginalKey);

                // 2) 대상 seq 활성 레코드 찾기 (없을 수도 있음)
                ClassImageEntity target = currentActive.stream()
                        .filter(e -> e.getImageSequence() != null && e.getImageSequence().equals(targetSeq))
                        .findFirst()
                        .orElse(null);

                String oldUrl = null;
                if (target != null) {
                    // 2-1) 대상 기존 레코드 논리삭제
                    oldUrl = target.getUrl();
                    target.setIsDeleted(true);
                    target.setDeletedAt(now);
                    imageRepo.save(target);
                    deletedDesc++;
                }

                // 3) 새 레코드 append (max+1)
                ClassImageEntity add = ClassImageEntity.builder()
                        .classInfo(info)
                        .status(ImageStatusEnum.DESCRIPTION)
                        .imageSequence(nextSeq++)
                        .build();
                add.setUrl(newOriginalUrl);
                add.setIsDeleted(false);
                imageRepo.save(add);
                addedDesc++;

                // 4) 이전 S3 실제 삭제(원본+썸네일) - best effort
                if (oldUrl != null && !oldUrl.isBlank()) {
                    deleteS3PairByUrl(oldUrl);
                }
            }
        }

        return ClassUpdateResponseDTO.builder()
                .classId(info.getId())
                .updatedThumbnailCount(updatedThumb)
                .addedDescriptionCount(addedDesc)
                .deletedDescriptionCount(deletedDesc)
                .build();
    }

    // ===== 수정 폼 조회 (URL만 내려줌) =====
    @Override
    @Transactional
    public ClassUpdateFormResponseDTO getClassForUpdate(String storeUrl, Long classId) {
        ClassInfoEntity info = classRepo.findByIdAndStore_Url(classId, storeUrl)
                .orElseThrow(() -> new IllegalArgumentException("해당 스토어에 속한 클래스가 없습니다. id=" + classId));

        var core = new ClassUpdateFormResponseDTO.Core();
        core.setTitle(info.getTitle());
        core.setDetail(info.getDetail());
        core.setPrice(info.getPrice());
        core.setGuideline(info.getGuideline());
        core.setParticipant(info.getParticipant());
        core.setPostNum(info.getPostNum());
        core.setAddressRoad(info.getAddressRoad());
        core.setAddressDetail(info.getAddressDetail());
        core.setAddressExtra(info.getAddressExtra());
        core.setStartDate(info.getStartDate() == null ? null : info.getStartDate().format(DATETIME_FMT));
        core.setEndDate(info.getEndDate() == null ? null : info.getEndDate().format(DATETIME_FMT));
        core.setStartTime(info.getStartTime() == null ? null : info.getStartTime().format(TIME_FMT));
        core.setEndTime(info.getEndTime() == null ? null : info.getEndTime().format(TIME_FMT));
        core.setTimeInterval(info.getTimeInterval());

        // is_deleted = false 만, THUMBNAIL 먼저(1..), 그 다음 DESCRIPTION(1..)
        List<ClassImageEntity> images = imageRepo.findAllByClassInfo_IdAndIsDeletedFalse(info.getId());
        images.sort(Comparator
                .comparing((ClassImageEntity e) -> e.getStatus() == ImageStatusEnum.THUMBNAIL ? 0 : 1)
                .thenComparing(e -> e.getImageSequence() == null ? Integer.MAX_VALUE : e.getImageSequence()));

        List<ClassUpdateFormResponseDTO.ImageItem> thumbs = new ArrayList<>();
        List<ClassUpdateFormResponseDTO.ImageItem> descs  = new ArrayList<>();

        for (ClassImageEntity e : images) {
            var item = new ClassUpdateFormResponseDTO.ImageItem();
            item.setUrl(e.getUrl());
            item.setImageSequence(e.getImageSequence());
            if (e.getStatus() == ImageStatusEnum.THUMBNAIL) thumbs.add(item);
            else descs.add(item);
        }

        var dto = new ClassUpdateFormResponseDTO();
        dto.setCore(core);
        dto.setThumbnails(thumbs);
        dto.setDescriptions(descs);
        return dto;
    }

    // ===== 클래스 논리적 삭제 토글 (S3는 유지) =====
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

        StoreEntity store = storeRepo.findByUrl(storeUrl)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스토어: " + storeUrl));

        List<ClassInfoEntity> belongToStore = classRepo.findAllByIdInAndStore_Url(classIds, store.getUrl());

        Set<Long> foundSet = belongToStore.stream().map(ClassInfoEntity::getId).collect(Collectors.toSet());
        List<Long> notFoundOrMismatched = classIds.stream()
                .filter(id -> !foundSet.contains(id)).collect(Collectors.toList());

        LocalDateTime now = LocalDateTime.now();
        List<Long> toggledToDeleted = new ArrayList<>();
        List<Long> toggledToRestored = new ArrayList<>();

        for (ClassInfoEntity c : belongToStore) {
            boolean current = Boolean.TRUE.equals(c.getIsDeleted());
            if (current) {
                c.setIsDeleted(false);
                c.setDeletedAt(null);
                toggledToRestored.add(c.getId());
            } else {
                c.setIsDeleted(true);
                c.setDeletedAt(now);
                toggledToDeleted.add(c.getId());
            }
        }

        if (!belongToStore.isEmpty()) classRepo.saveAll(belongToStore);

        return ClassDeletionToggleResponseDTO.builder()
                .requestedCount(classIds.size())
                .toggledToDeletedCount(toggledToDeleted.size())
                .toggledToRestoredCount(toggledToRestored.size())
                .toggledToDeletedIds(toggledToDeleted)
                .toggledToRestoredIds(toggledToRestored)
                .notFoundOrMismatchedIds(notFoundOrMismatched)
                .build();
    }

    // ===== 클래스 리스트 조회 =====
    @Override
    public List<ClassListItemResponseDTO> getClassesByStoreUrl(String storeUrl) {
        List<ClassInfoEntity> infos = classRepo.findAllByStore_Url(storeUrl);

        return infos.stream()
                .map(this::toClassListItemResponseDTO)
                .collect(Collectors.toList());
    }

    private ClassListItemResponseDTO toClassListItemResponseDTO(ClassInfoEntity info) {
        String thumbnailUrl = imageRepo
                .findFirstByClassInfo_IdAndStatusAndImageSequenceAndIsDeletedFalseOrderByIdAsc(
                        info.getId(), ImageStatusEnum.THUMBNAIL, 1
                )
                .map(ClassImageEntity::getUrl)
                .orElse(null);

        String location = joinWithSpace(
                info.getPostNum(),
                info.getAddressRoad(),
                info.getAddressDetail(),
                info.getAddressExtra()
        );

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

    // ===== 클래스 등록 (멀티파트 + S3) =====
    @Override
    @Transactional
    public List<Long> createClasses(String storeUrl, List<ClassCreateRequestDTO> requests) {
        StoreEntity store = storeRepo.findByUrl(storeUrl)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스토어: " + storeUrl));

        List<Long> createdIds = new ArrayList<>();

        for (ClassCreateRequestDTO req : requests) {
            var c = req.getClazz();
            if (c == null) throw new IllegalArgumentException("clazz(핵심 정보)는 필수입니다.");

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
            classInfo.setIsDeleted(Boolean.FALSE);

            ClassInfoEntity saved = classRepo.save(classInfo);

            List<ClassImageEntity> images = new ArrayList<>();

            // 썸네일 3장 필수
            MultipartFile[] thumbs = req.getThumbnails();
            if (thumbs == null || thumbs.length != 3) {
                throw new IllegalArgumentException("썸네일 이미지는 정확히 3장이어야 합니다.");
            }
            int thumbSeq = 1;
            for (MultipartFile f : thumbs) {
                if (f == null || f.isEmpty()) throw new IllegalArgumentException("썸네일 파일이 비어 있습니다.");
                try {
                    String originalKey  = s3Uploader.uploadImage(f);
                    String thumbnailUrl = s3Uploader.getThumbnailUrl(originalKey);

                    ClassImageEntity e = ClassImageEntity.builder()
                            .classInfo(saved)
                            .status(ImageStatusEnum.THUMBNAIL)
                            .imageSequence(thumbSeq++)
                            .build();
                    e.setUrl(thumbnailUrl);
                    e.setIsDeleted(Boolean.FALSE);
                    images.add(e);
                } catch (Exception ex) {
                    throw new RuntimeException("썸네일 업로드 실패: " + ex.getMessage(), ex);
                }
            }

            // 설명 이미지 0..N
            MultipartFile[] descs = req.getDescriptions();
            if (descs != null && descs.length > 0) {
                int descSeq = 1;
                for (MultipartFile f : descs) {
                    if (f == null || f.isEmpty()) continue;
                    try {
                        String originalKey = s3Uploader.uploadImage(f);
                        String originalUrl = s3Uploader.getFullUrl(originalKey);

                        ClassImageEntity e = ClassImageEntity.builder()
                                .classInfo(saved)
                                .status(ImageStatusEnum.DESCRIPTION)
                                .imageSequence(descSeq++)
                                .build();
                        e.setUrl(originalUrl);
                        e.setIsDeleted(Boolean.FALSE);
                        images.add(e);
                    } catch (Exception ex) {
                        throw new RuntimeException("설명 이미지 업로드 실패: " + ex.getMessage(), ex);
                    }
                }
            }

            if (!images.isEmpty()) imageRepo.saveAll(images);

            createdIds.add(saved.getId());
        }
        return createdIds;
    }

    // ===== 유틸 =====
    private static String nvl(String s) { return (s == null) ? "" : s; }

    private static LocalDateTime parseDateTime(String s) {
        if (s == null || s.isBlank()) return null;
        return LocalDateTime.parse(s.trim(), DATETIME_FMT);
    }

    private static LocalTime parseTime(String s) {
        if (s == null || s.isBlank()) return null;
        return LocalTime.parse(s.trim(), TIME_FMT);
    }

    private String joinWithSpace(String... parts) {
        return java.util.Arrays.stream(parts)
                .filter(p -> p != null && !p.isBlank())
                .collect(Collectors.joining(" "));
    }

    // ===== S3 삭제 보조 =====
    /** URL -> S3 key 추출 (https://{bucket}.s3.amazonaws.com/...) */
    private String keyFromUrl(String url) {
        if (url == null) return null;
        String marker = ".s3.amazonaws.com/";
        int idx = url.indexOf(marker);
        if (idx >= 0) {
            return url.substring(idx + marker.length());
        }
        return url; // 이미 key만 들어온 경우
    }

    /** 썸네일 key -> 원본 key */
    private String originalKeyFromThumbKey(String thumbKey) {
        if (thumbKey == null) return null;
        return thumbKey.replace("images/thumbnail/", "images/original/")
                .replace("_thumb.webp", ".webp");
    }

    /** 원본 key -> 썸네일 key */
    private String thumbKeyFromOriginalKey(String originalKey) {
        if (originalKey == null) return null;
        String fileName = originalKey.substring(originalKey.lastIndexOf('/') + 1); // abc.webp
        String base = fileName.replace(".webp", ""); // abc
        return "images/thumbnail/" + base + "_thumb.webp";
    }

    /** URL 기준으로 원본/썸네일 한 쌍을 모두 삭제 */
    private void deleteS3PairByUrl(String url) {
        String key = keyFromUrl(url);
        if (key == null || key.isBlank()) return;

        try {
            if (key.startsWith("images/thumbnail/")) {
                String originalKey = originalKeyFromThumbKey(key);
                s3Uploader.delete(key);
                if (originalKey != null) s3Uploader.delete(originalKey);
            } else if (key.startsWith("images/original/")) {
                String thumbKey = thumbKeyFromOriginalKey(key);
                s3Uploader.delete(key);
                if (thumbKey != null) s3Uploader.delete(thumbKey);
            } else {
                s3Uploader.delete(key);
            }
        } catch (Exception e) {
            log.warn("S3 삭제 실패(무시): key={}, msg={}", key, e.getMessage());
        }
    }
}
