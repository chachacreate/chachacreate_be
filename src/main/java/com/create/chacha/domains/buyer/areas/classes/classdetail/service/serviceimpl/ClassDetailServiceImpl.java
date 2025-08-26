package com.create.chacha.domains.buyer.areas.classes.classdetail.service.serviceimpl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.create.chacha.common.util.S3Uploader;
import com.create.chacha.domains.buyer.areas.classes.classdetail.dto.response.ClassImagesResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.classdetail.dto.response.ClassScheduleResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.classdetail.dto.response.ClassSummaryResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.classdetail.service.ClassDetailService;
import com.create.chacha.domains.buyer.areas.classes.classlist.repository.ClassInfoRepository;
import com.create.chacha.domains.seller.areas.classes.classinsert.repository.ClassImageRepository;
import com.create.chacha.domains.seller.areas.classes.classinsert.repository.StoreRepository;
import com.create.chacha.domains.shared.entity.classcore.ClassInfoEntity;
import com.create.chacha.domains.shared.entity.store.StoreEntity;
import com.create.chacha.domains.shared.repository.ClassScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClassDetailServiceImpl implements ClassDetailService{
    private final ClassInfoRepository classInfoRepository;
    private final StoreRepository storeRepository;
    private final ClassImageRepository classImageRepository;
    private final ClassScheduleRepository classScheduleRepository;
    
    private final S3Uploader s3Uploader;

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



    @Override
    public ClassImagesResponseDTO getImages(Long classId) {
        return ClassImagesResponseDTO.builder()
                .classId(classId)
                .images(
                        classImageRepository.findByClassInfo_Id(classId)
                                .stream()
                                .map(img -> ClassImagesResponseDTO.Image.builder()
                                        .url(s3Uploader.getFullUrl(img.getUrl()))
                                        .thumbnailUrl(s3Uploader.getThumbnailUrl(img.getUrl()))
                                        .sequence(img.getImageSequence())
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }
    
    @Override
    public List<ClassScheduleResponseDTO> getSchedule(Long classId) {
        List<Object[]> results = classScheduleRepository.findClassSchedule(classId);
        return results.stream()
                .map(ClassScheduleResponseDTO::of)
                .toList();
    }
 

    

}
