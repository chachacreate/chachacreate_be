package com.create.chacha.domains.buyer.areas.mainhome.main.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.create.chacha.common.util.LegacyAPIUtil;
import com.create.chacha.common.util.dto.LegacyStoreDTO;
import com.create.chacha.domains.buyer.areas.mainhome.main.dto.response.HomeClassDTO;
import com.create.chacha.domains.buyer.areas.mainhome.main.dto.response.HomeDTO;
import com.create.chacha.domains.buyer.areas.mainhome.main.dto.response.HomeProductDTO;
import com.create.chacha.domains.buyer.areas.mainhome.main.repository.MainClassRepository;
import com.create.chacha.domains.buyer.areas.mainhome.main.repository.MainProductRepository;
import com.create.chacha.domains.buyer.areas.mainhome.main.repository.MainStoreRepository;
import com.create.chacha.domains.buyer.areas.mainhome.main.repository.projection.ClassTimeCountProjection;
import com.create.chacha.domains.shared.constants.DownCategoryEnum;
import com.create.chacha.domains.shared.constants.UpCategoryEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MainPageService {

	private final MainProductRepository productRepository;
	private final MainStoreRepository storeRepository;
	private final MainClassRepository classRepository; // 🔹 추가
	private final LegacyAPIUtil legacyAPIUtil;

	// 상품 리스트 전체 조회 (필터링 및 정렬 포함)
	public List<HomeProductDTO> getProductList(Long storeId, List<String> d, List<String> u, String keyword,
			String sort) {

		List<HomeProductDTO> products = productRepository.findProductListWithFilters(storeId, getDownCategoryId(d),
				getUpCategoryId(u));

		// 정렬 적용
		if (sort != null) {
			switch (sort) {
			case "latest":
				products.sort((p1, p2) -> p2.getProductDate().compareTo(p1.getProductDate()));
				break;
			case "popular":
				products.sort((p1, p2) -> Integer.compare(p2.getSaleCnt(), p1.getSaleCnt()));
				break;
			case "lowprice":
				products.sort(Comparator.comparing(HomeProductDTO::getPrice));
				break;
			case "highprice":
				products.sort((p1, p2) -> Integer.compare(p2.getPrice(), p1.getPrice()));
				break;
			default:
				products.sort((p1, p2) -> p2.getProductDate().compareTo(p1.getProductDate()));
				break;
			}
		}

		// 키워드가 존재하면 전용 쿼리 실행
		if (keyword != null && !keyword.isEmpty()) {
			log.info("🔍 상품명 검색 요청: {}", keyword);
			return searchByProductName(keyword);
		}

		return products;
	}

	/** 🏠 메인 홈 - 인기 스토어 + 인기 상품 + 신상품 */
	public Map<String, Object> getHomeMainProductMap() {
		return Map.of("bestStore", getBestStores(), "bestProduct", getBestProducts(null), "newProduct",
				getNewProducts());
	}

	/** 🛍️ 스토어 메인 페이지 - 인기 + 대표 상품 묶음 */
	public Map<String, List<HomeProductDTO>> getStoreMainProductMap(Long storeId) {
		return Map.of("bestProduct", getBestProducts(storeId), "mainProduct", getStoreMainProducts(storeId));
	}

	// 상품명으로 검색
	public List<HomeProductDTO> searchByProductName(String keyword) {
		return productRepository.findByProductName(keyword);
	}

	// 인기상품 조회
	public List<HomeProductDTO> getBestProducts(Long storeId) {
		int limit = (storeId != null) ? 3 : 10;
		Pageable pageable = PageRequest.of(0, limit);
		Page<HomeProductDTO> page = productRepository.findBestProducts(storeId, pageable);
		return page.getContent();
	}

	// 스토어 대표상품 조회
	public List<HomeProductDTO> getStoreMainProducts(Long storeId) {
		Pageable pageable = PageRequest.of(0, 3);
		return productRepository.findStoreMainProducts(storeId, pageable);
	}

	// 인기스토어 조회
	public List<HomeDTO> getBestStores() {
		Pageable pageable = PageRequest.of(0, 10);
		return storeRepository.findBestStores(pageable);
	}

	// 최신상품 조회
	public List<HomeProductDTO> getNewProducts() {
		Pageable pageable = PageRequest.of(0, 10);
		return productRepository.findNewProducts(pageable);
	}

	// downCategory 이름으로 id조회
	public List<Long> getDownCategoryId(List<String> d) {
		List<Long> list = new ArrayList<>();
		d.stream().forEach(dc -> {
			list.add(DownCategoryEnum.valueOf(dc).getId());
		});
		return list;
	}

	// upCategory 이름으로 id조회
	public List<Long> getUpCategoryId(List<String> u) {
		List<Long> list = new ArrayList<>();
		u.stream().forEach(uc -> {
			list.add(UpCategoryEnum.valueOf(uc).getId());
		});
		return list;
	}

	public List<HomeClassDTO> getClassesByRemainSeatAsc(int limit) {
		Pageable pageable = PageRequest.of(0, Math.max(1, Math.min(limit, 100)));
		List<HomeClassDTO> rows = classRepository.findClassesOrderByRemainSeatAsc(pageable);

		// storeName 매핑
		Map<Long, String> cache = new HashMap<>();
		for (HomeClassDTO c : rows) {
			cache.computeIfAbsent(c.getStoreId(), sid -> {
				try {
					LegacyStoreDTO legacy = legacyAPIUtil.getLegacyStoreDataById(sid);
					return legacy != null ? legacy.getStoreName() : null;
				} catch (Exception e) {
					log.warn("storeName 매핑 실패 - storeId={}", sid, e);
					return null;
				}
			});
			c.setStoreName(cache.get(c.getStoreId()));
		}
		if (rows.isEmpty())
			return rows;

		// 오늘(KST) 범위
		ZoneId zone = ZoneId.of("Asia/Seoul");
		LocalDate today = LocalDate.now(zone);
		LocalDateTime start = today.atStartOfDay();
		LocalDateTime end = start.plusDays(1);
		LocalDateTime now = LocalDateTime.now(zone);

		// 오늘 예약 시간별 카운트
		List<Long> classIds = rows.stream().map(HomeClassDTO::getId).collect(Collectors.toList());
		List<ClassTimeCountProjection> timeCounts = classRepository.findTodayTimeCountsByClassIds(classIds, start, end);

		// classId -> (HH:mm -> count)
		Map<Long, Map<String, Long>> bookMap = new HashMap<>();
		for (ClassTimeCountProjection p : timeCounts) {
			bookMap.computeIfAbsent(p.getClassId(), k -> new HashMap<>()).put(p.getTime(), p.getCount());
		}

		for (HomeClassDTO c : rows) {  
			if (today.isBefore(c.getStartDate().toLocalDate()) || today.isAfter(c.getEndDate().toLocalDate())) {
				c.setAvailableTimes(List.of());
				continue;
			}


			List<String> slots = buildTodaySlots(c.getStartDate(), c.getEndDate(), c.getTimeInterval(), zone);

			Map<String, Long> cnt = bookMap.getOrDefault(c.getId(), Map.of());
			Integer capacity = c.getParticipant(); // 정원
			if (capacity == null || capacity <= 0) {
				c.setAvailableTimes(List.of());
				continue;
			}


			List<String> available = new ArrayList<>();
			for (String hhmm : slots) {
				long booked = cnt.getOrDefault(hhmm, 0L);
				if (booked < capacity) {
					LocalTime t = LocalTime.parse(hhmm);
					LocalDateTime slotTime = LocalDateTime.of(today, t);
					if (!slotTime.isBefore(now)) { 
						available.add(hhmm);
					}
				}
			}
			if (available.isEmpty()) {
				c.setAvailableTimes(List.of("오늘은 예약 마감"));
			} else {
				c.setAvailableTimes(available);
			}
		}

		return rows;
	}

	private List<String> buildTodaySlots(LocalDateTime classStart, LocalDateTime classEnd, Integer intervalMinutes,
			ZoneId zone) {
		if (intervalMinutes == null || intervalMinutes <= 0)
			return List.of();
		LocalDate today = LocalDate.now(zone);
		// 하루 단위로 본다고 했으므로 "시간대"는 start/end의 "시:분"을 사용
		LocalTime startTime = classStart.toLocalTime();
		LocalTime endTime = classEnd.toLocalTime();
		if (endTime.isBefore(startTime))
			return List.of(); // 잘못된 데이터 방지

		List<String> slots = new ArrayList<>();
		LocalTime cur = startTime;
		while (!cur.isAfter(endTime)) {
			slots.add(String.format("%02d:%02d", cur.getHour(), cur.getMinute()));
			cur = cur.plusMinutes(intervalMinutes);
		}
		// endTime이 정확히 interval에 맞지 않으면 마지막 슬롯이 endTime을 넘어갈 수 있음 → 위 while 조건으로 방지
		return slots;
	}
}
