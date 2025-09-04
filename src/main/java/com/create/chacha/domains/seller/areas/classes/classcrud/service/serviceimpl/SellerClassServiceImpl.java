package com.create.chacha.domains.seller.areas.classes.classcrud.service.serviceimpl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.create.chacha.common.util.LegacyAPIUtil;
import com.create.chacha.common.util.S3Uploader;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.request.ClassCreateRequestDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.request.ClassUpdateRequestDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.response.ClassDeletionToggleResponseDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.response.ClassListItemResponseDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.response.ClassUpdateFormResponseDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.dto.response.ClassUpdateResponseDTO;
import com.create.chacha.domains.seller.areas.classes.classcrud.repository.ClassImageRepository;
import com.create.chacha.domains.seller.areas.classes.classcrud.repository.SellerClassesRepository;
import com.create.chacha.domains.seller.areas.classes.classcrud.service.SellerClassService;
import com.create.chacha.domains.shared.constants.ImageStatusEnum;
import com.create.chacha.domains.shared.entity.classcore.ClassImageEntity;
import com.create.chacha.domains.shared.entity.classcore.ClassInfoEntity;
import com.create.chacha.domains.shared.entity.store.StoreEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerClassServiceImpl implements SellerClassService {

    private final SellerClassesRepository classRepo;
    private final ClassImageRepository imageRepo;

    private final S3Uploader s3Uploader;
    private final LegacyAPIUtil legacyAPIUtil;

    @PersistenceContext
    private EntityManager em;

    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter TIME_FMT     = DateTimeFormatter.ofPattern("HH:mm:ss");

    /** 레거시에서 storeId 얻기 (없으면 null) */
    private Long getLegacyStoreId(String storeUrl) {
        try {
            var legacy = legacyAPIUtil.getLegacyStoreData(storeUrl);
            if (legacy == null || legacy.getStoreId() == null) return null;
            return legacy.getStoreId().longValue();
        } catch (Exception e) {
            log.warn("Legacy store 조회 실패: url={}, msg={}", storeUrl, e.getMessage());
            return null;
        }
    }

    // ===== 클래스 수정 =====
    @Override
    @Transactional
    public ClassUpdateResponseDTO updateClass(String storeUrl, Long classId, ClassUpdateRequestDTO req) {
        Long storeId = getLegacyStoreId(storeUrl);
        if (storeId == null) return null;

        // 레포 시그니처에 맞춰 사용 (native query)
        ClassInfoEntity info = classRepo.findByIdAndStore_Url(classId, storeId).orElse(null);
        if (info == null) return null;

        // 본문 업데이트 (부분 수정)
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

        // 썸네일 1장 교체
        MultipartFile[] thumbs = req.getThumbnails();
        if (thumbs != null && thumbs.length > 0) {
            MultipartFile f = Arrays.stream(thumbs).filter(t -> t != null && !t.isEmpty()).findFirst().orElse(null);
            if (f != null) {
                String newOriginalKey;
                try {
                    newOriginalKey = s3Uploader.uploadImage(f);
                } catch (Exception ex) {
                    throw new RuntimeException("썸네일 업로드 실패: " + ex.getMessage(), ex);
                }
                String newThumbUrl = s3Uploader.getThumbnailUrl(newOriginalKey);

                ClassImageEntity entity = imageRepo
                        .findByClassInfo_IdAndStatusAndImageSequence(info.getId(), ImageStatusEnum.THUMBNAIL, 1)
                        .orElseGet(() -> ClassImageEntity.builder()
                                .classInfo(info).status(ImageStatusEnum.THUMBNAIL).imageSequence(1).build());

                String oldUrl = entity.getUrl();
                entity.setUrl(newThumbUrl);
                entity.setIsDeleted(false);
                entity.setDeletedAt(null);
                imageRepo.save(entity);
                updatedThumb++;

                imageRepo.markThumbnailOthersDeleted(info.getId(), 1);

                if (oldUrl != null && !oldUrl.isBlank()) {
                    deleteS3PairByUrl(oldUrl);
                }
            }
        }

        // DESCRIPTION 부분 교체
        MultipartFile[] descFiles = req.getDescriptions();
        Integer[] replaceSeqs     = req.getReplaceDescriptionSeqs();
        if (descFiles != null && descFiles.length > 0) {
            if (replaceSeqs == null || replaceSeqs.length != descFiles.length) {
                // 컨트롤러에서 BAD_REQUEST로 처리하고 싶다면, 여기서 예외 대신 null 반환 규약으로 바꿔도 됨.
                throw new IllegalArgumentException("replaceDescriptionSeqs 길이가 descriptions와 일치해야 합니다.");
            }

            Set<Integer> seen = new HashSet<>();
            for (Integer seq : replaceSeqs) {
                if (seq == null || seq < 1) throw new IllegalArgumentException("DESCRIPTION 교체 seq는 1+ 이어야 합니다. seq=" + seq);
                if (!seen.add(seq)) throw new IllegalArgumentException("DESCRIPTION seq 중복: " + seq);
            }

            List<ClassImageEntity> currentActive =
                    imageRepo.findAllByClassInfo_IdAndStatusAndIsDeletedFalse(info.getId(), ImageStatusEnum.DESCRIPTION);

            int nextSeq = imageRepo
                    .findTopByClassInfo_IdAndStatusOrderByImageSequenceDesc(info.getId(), ImageStatusEnum.DESCRIPTION)
                    .map(ClassImageEntity::getImageSequence).orElse(0) + 1;

            LocalDateTime now = LocalDateTime.now();

            for (int i = 0; i < descFiles.length; i++) {
                MultipartFile f = descFiles[i];
                Integer targetSeq = replaceSeqs[i];
                if (f == null || f.isEmpty()) continue;

                String newOriginalKey;
                try {
                    newOriginalKey = s3Uploader.uploadImage(f);
                } catch (Exception ex) {
                    throw new RuntimeException("설명 이미지 업로드 실패(index=" + i + "): " + ex.getMessage(), ex);
                }
                String newOriginalUrl = s3Uploader.getFullUrl(newOriginalKey);

                ClassImageEntity target = currentActive.stream()
                        .filter(e -> Objects.equals(e.getImageSequence(), targetSeq))
                        .findFirst().orElse(null);

                String oldUrl = null;
                if (target != null) {
                    oldUrl = target.getUrl();
                    target.setIsDeleted(true);
                    target.setDeletedAt(now);
                    imageRepo.save(target);
                    deletedDesc++;
                }

                ClassImageEntity add = ClassImageEntity.builder()
                        .classInfo(info).status(ImageStatusEnum.DESCRIPTION).imageSequence(nextSeq++).build();
                add.setUrl(newOriginalUrl);
                add.setIsDeleted(false);
                imageRepo.save(add);
                addedDesc++;

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

    // ===== 수정 폼 조회 =====
    @Override
    @Transactional
    public ClassUpdateFormResponseDTO getClassForUpdate(String storeUrl, Long classId) {
        Long storeId = getLegacyStoreId(storeUrl);
        if (storeId == null) return null;

        ClassInfoEntity info = classRepo.findByIdAndStore_Url(classId, storeId).orElse(null);
        if (info == null) return null;

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

    // ===== 클래스 논리적 삭제 토글 =====
    @Override
    @Transactional
    public ClassDeletionToggleResponseDTO toggleClassesDeletion(String storeUrl, List<Long> classIds) {
        if (classIds == null || classIds.isEmpty()) return null;

        Long storeId = getLegacyStoreId(storeUrl);
        if (storeId == null) return null;

        List<ClassInfoEntity> belongToStore = classRepo.findAllByIdInAndStore_Url(classIds, storeId);
        if (belongToStore == null) belongToStore = List.of();

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
        Long storeId = getLegacyStoreId(storeUrl);
        if (storeId == null) return List.of();

        List<ClassInfoEntity> infos = classRepo.findAllByStore_Url(storeId);
        if (infos == null || infos.isEmpty()) return List.of();

        return infos.stream().map(this::toClassListItemResponseDTO).collect(Collectors.toList());
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
                .timeInterval(info.getTimeInterval())
                .createdAt(info.getCreatedAt())
                .updatedAt(info.getUpdatedAt())
                .deletedAt(info.getDeletedAt())
                .isDeleted(Boolean.TRUE.equals(info.getIsDeleted()))
                .build();
    }

    // ===== 클래스 등록 =====
    @Override
    @Transactional
    public List<Long> createClasses(String storeUrl, List<ClassCreateRequestDTO> requests) {
        Long storeId = getLegacyStoreId(storeUrl);
        if (storeId == null) return List.of();

        StoreEntity storeRef = em.getReference(StoreEntity.class, storeId);
        List<Long> createdIds = new ArrayList<>();

        for (ClassCreateRequestDTO req : requests) {
            var c = req.getClazz();
            if (c == null) return List.of();

            ClassInfoEntity classInfo = ClassInfoEntity.builder()
                    .store(storeRef)
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

            // 썸네일: 1장 이상
            MultipartFile[] thumbs = req.getThumbnails();
            if (thumbs == null || thumbs.length < 1) {
                return List.of();
            }
            int thumbSeq = 1;
            for (MultipartFile f : thumbs) {
                if (f == null || f.isEmpty()) continue;
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

            // 설명 이미지는 최종 HTML의 URL만 저장
            int descSeq = 1;
            List<String> urlList = (c.getDetailImageUrls() == null) ? List.of() : c.getDetailImageUrls();
            for (String url : urlList) {
                if (url == null || url.isBlank()) continue;
                ClassImageEntity e = ClassImageEntity.builder()
                        .classInfo(saved)
                        .status(ImageStatusEnum.DESCRIPTION)
                        .imageSequence(descSeq++)
                        .build();
                e.setUrl(url.trim());
                e.setIsDeleted(Boolean.FALSE);
                images.add(e);
            }

            // 편집 중 업로드했지만 최종 HTML에 없는 URL은 S3에서 삭제
            List<String> uploaded = (c.getEditorUploadedUrls() == null) ? List.of() : c.getEditorUploadedUrls();
            if (!uploaded.isEmpty()) {
                Set<String> finalSet = urlList.stream()
                        .filter(u -> u != null && !u.isBlank())
                        .map(String::trim)
                        .collect(Collectors.toSet());
                for (String u : uploaded) {
                    if (u == null || u.isBlank()) continue;
                    if (!finalSet.contains(u.trim())) {
                        deleteS3PairByUrl(u);
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
        return Arrays.stream(parts)
                .filter(p -> p != null && !p.isBlank())
                .collect(Collectors.joining(" "));
    }

    private String keyFromUrl(String url) {
        if (url == null) return null;
        String marker = ".s3.amazonaws.com/";
        int idx = url.indexOf(marker);
        if (idx >= 0) return url.substring(idx + marker.length());
        return url;
    }

    private String originalKeyFromThumbKey(String thumbKey) {
        if (thumbKey == null) return null;
        return thumbKey.replace("images/thumbnail/", "images/original/")
                       .replace("_thumb.webp", ".webp");
    }

    private String thumbKeyFromOriginalKey(String originalKey) {
        if (originalKey == null) return null;
        String fileName = originalKey.substring(originalKey.lastIndexOf('/') + 1);
        String base = fileName.replace(".webp", "");
        return "images/thumbnail/" + base + "_thumb.webp";
    }

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
