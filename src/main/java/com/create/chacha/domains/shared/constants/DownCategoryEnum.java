package com.create.chacha.domains.shared.constants;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 하위 카테고리 열거형(enum) 클래스.
 * <p>
 * d_category 테이블과 매핑되며, 상위 카테고리({@link UpCategoryEnum})에 속한 세부 카테고리 정보를 정의합니다.
 * 각각의 항목은 u_category_id(외래키)를 통해 {@link UpCategoryEnum}과 연결됩니다.
 * </p>
 *
 * <pre>
 * 예시 JSON 응답:
 * {
 *   "id": 3,
 *   "name": "가방",
 *   "uCategory": {
 *     "id": 2,
 *     "name": "패션잡화"
 *   }
 * }
 * </pre>
 */
@Getter
@AllArgsConstructor
//@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum DownCategoryEnum {

    // 패션잡화
    TOP(1L, "상의", UpCategoryEnum.FASHION),
    BOTTOM(2L, "하의", UpCategoryEnum.FASHION),
    BAG(3L, "가방", UpCategoryEnum.FASHION),
    WALLET(4L, "지갑", UpCategoryEnum.FASHION),
    FASHION_ETC(5L, "기타(목도리, 모자, 벨트 등)", UpCategoryEnum.FASHION),

    // 인테리어 소품
    DIFFUSER(6L, "디퓨저, 캔들", UpCategoryEnum.INTERIOR),
    MOOD_LIGHT(7L, "무드등", UpCategoryEnum.INTERIOR),
    FLOWER_PLANT(8L, "꽃, 식물", UpCategoryEnum.INTERIOR),
    FURNITURE(9L, "가구", UpCategoryEnum.INTERIOR),

    // 악세서리
    RING(10L, "반지", UpCategoryEnum.ACCESSORY),
    BRACELET(11L, "팔찌", UpCategoryEnum.ACCESSORY),
    NECKLACE(12L, "목걸이", UpCategoryEnum.ACCESSORY),
    KEYRING(13L, "키링", UpCategoryEnum.ACCESSORY),

    // 생활잡화
    SOAP(14L, "비누", UpCategoryEnum.LIFESTYLE),
    DISH(15L, "그릇", UpCategoryEnum.LIFESTYLE),
    TABLEWARE(16L, "식기류", UpCategoryEnum.LIFESTYLE),
    CUP(17L, "컵", UpCategoryEnum.LIFESTYLE),
    CASE(18L, "케이스", UpCategoryEnum.LIFESTYLE),

    // 기타
    PERFUME(19L, "향수", UpCategoryEnum.ETC),
    DOLL(20L, "인형", UpCategoryEnum.ETC),
    PET(21L, "반려동물", UpCategoryEnum.ETC),
    STATIONERY(22L, "문구", UpCategoryEnum.ETC);

    /** 하위 카테고리 고유 ID (d_category 테이블의 기본 키) */
    private final Long id;

    /** 하위 카테고리 이름 */
    private final String name;

    /** 연결된 상위 카테고리 (u_category 테이블의 외래키 매핑) */
    private final UpCategoryEnum ucategory;

    /**
     * 주어진 상위 카테고리에 해당하는 하위 카테고리 목록을 반환합니다.
     *
     * @return 해당 상위 카테고리에 속하는 {@link DownCategoryEnum} 리스트
     */
    public UpCategoryEnum getUcategory() { return ucategory; }

    public static List<DownCategoryEnum> getByUCategory(UpCategoryEnum uCategory) {
        return Arrays.stream(values())
                .filter(d -> d.ucategory == uCategory)
                .collect(Collectors.toList());
    }

    /**
     * 주어진 ID에 해당하는 하위 카테고리 enum을 반환합니다.
     *
//     * @param id 하위 카테고리 ID
     * @return ID에 해당하는 {@link DownCategoryEnum} 값
     * @throws IllegalArgumentException 해당 ID에 대응하는 enum이 없는 경우 예외 발생
     */
    @JsonCreator
    public static DownCategoryEnum fromJson(Object input) {
        if (input instanceof Integer) {
            return fromId((Integer) input);
        }
        if (input instanceof String) {
            String str = (String) input;
            // 숫자 문자열이면 숫자로 변환 시도
            try {
                int id = Integer.parseInt(str);
                return fromId(id);
            } catch (NumberFormatException e) {
                // 이름 비교
                for (DownCategoryEnum d : values()) {
                    if (d.name().equalsIgnoreCase(str) || d.name.equals(str)) {
                        return d;
                    }
                }
            }
        }
        throw new IllegalArgumentException("Invalid DCategory input: " + input);
    }

    public static DownCategoryEnum fromId(int id) {
        for (DownCategoryEnum d : values()) {
            if (d.id == id) return d;
        }
        throw new IllegalArgumentException("Invalid DCategory id: " + id);
    }

//    @Override
//    public String toString() {
//        return name;
//    }

}

