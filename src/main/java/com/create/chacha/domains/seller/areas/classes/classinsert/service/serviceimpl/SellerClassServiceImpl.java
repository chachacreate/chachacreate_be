package com.create.chacha.domains.seller.areas.classes.classinsert.service.serviceimpl;

import java.util.List;

import com.create.chacha.domains.seller.areas.classes.classinsert.dto.request.ClassCreateRequestDTO;

public interface SellerClassServiceImpl {
	List<Long> createClasses(String storeUrl, List<ClassCreateRequestDTO> requests);
}
