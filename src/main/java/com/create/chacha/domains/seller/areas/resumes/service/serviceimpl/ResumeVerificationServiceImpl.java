package com.create.chacha.domains.seller.areas.resumes.service.serviceimpl;

import com.create.chacha.common.util.S3Uploader;
import com.create.chacha.domains.seller.areas.classes.classcrud.repository.StoreRepository;
import com.create.chacha.domains.seller.areas.resumes.dto.request.ResumeImageDTO;
import com.create.chacha.domains.seller.areas.resumes.dto.request.ResumeRequestDTO;
import com.create.chacha.domains.seller.areas.resumes.dto.response.ResumeImageResponseDTO;
import com.create.chacha.domains.seller.areas.resumes.dto.response.ResumeResponseDTO;
import com.create.chacha.domains.seller.areas.resumes.exception.ResumeUploadException;
import com.create.chacha.domains.seller.areas.resumes.repository.ResumeImageRepository;
import com.create.chacha.domains.seller.areas.resumes.repository.StoreResumeRepository;
import com.create.chacha.domains.seller.areas.resumes.service.ResumeVerificationService;
import com.create.chacha.domains.shared.constants.AcceptStatusEnum;
import com.create.chacha.domains.shared.entity.store.ResumeImageEntity;
import com.create.chacha.domains.shared.entity.store.StoreEntity;
import com.create.chacha.domains.shared.entity.store.StoreResumeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ResumeVerificationServiceImpl implements ResumeVerificationService {

    private final StoreResumeRepository resumeRepository;
    private final ResumeImageRepository imageRepository;
    private final StoreRepository storeRepository; // storeUrl → storeId 조회용
    private final S3Uploader s3Uploader;

    @Override
    @Transactional
    public ResumeResponseDTO createResume(String storeUrl, ResumeRequestDTO request) {
        try {
            // 1. store 조회
            StoreEntity store = storeRepository.findByUrl(storeUrl)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스토어 URL"));

            // 2. StoreResumeEntity 생성
            StoreResumeEntity resume = new StoreResumeEntity();
            resume.setStore(store);
            resumeRepository.save(resume);

            // 3. 이미지 업로드 & ResumeImageEntity 저장
            List<ResumeImageResponseDTO> imageResponses = new ArrayList<>();
            for (ResumeImageDTO imageDTO : request.getImages()) {
                String key = s3Uploader.uploadImage(imageDTO.getFile());
                String url = s3Uploader.getFullUrl(key);

                ResumeImageEntity image = new ResumeImageEntity();
                image.setResume(resume);
                image.setUrl(url);
                image.setContent(imageDTO.getContent());
                imageRepository.save(image);

                imageResponses.add(new ResumeImageResponseDTO(
                        image.getId(),
                        url,
                        imageDTO.getContent()
                ));
            }

            // 4. 이미지 업로드 완료 후 상태 수정 - 관리자 페이지가 없으므로 임시 코드
            resume.setStatus(AcceptStatusEnum.APPROVED);
            resumeRepository.save(resume);

            // 5. 응답 반환
            ResumeResponseDTO response = new ResumeResponseDTO();
            response.setResumeId(resume.getId());
            response.setStatus(resume.getStatus());
            response.setImages(imageResponses);

            return response;

        } catch (Exception e) {
            throw new RuntimeException("이력 인증 중 오류가 발생했습니다.", e);
        }
    }
}
