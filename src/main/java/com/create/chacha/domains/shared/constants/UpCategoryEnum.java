package com.create.chacha.domains.shared.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 상위 카테고리 열거형(enum) 클래스.
 * <p>
 * {@code u_category} 테이블과 매핑되며, 수공예 상품의 대분류 정보를 정의합니다.
 * 직렬화(출력) 시에는 카테고리 이름({@link #name})이 문자열로 반환되며,
 * 역직렬화(입력) 시에는 name 또는 enum 상수명(CRAFT 등) 모두 허용됩니다.
 * </p>
 *
 * <p><b>예시 JSON 입력:</b>
 * <pre>{@code
 * {
 *   "storeId": 1,
 *   "uCategoryId": "공예" // 또는 "CRAFT"
 * }
 * }</pre>
 * </p>
 *
 * <p><b>예시 JSON 출력:</b>
 * <pre>{@code
 * {
 *   "uCategoryId": "공예"
 * }
 * }</pre>
 * </p>
 */
@Getter
@AllArgsConstructor
public enum UpCategoryEnum {

    FASHION(1L, "패션잡화"),
    INTERIOR(2L, "인테리어 소품"),
    ACCESSORY(3L, "악세서리"),
    LIFESTYLE(4L, "생활잡화"),
    ETC(5L, "기타");

    /** DB의 u_category_id와 매핑되는 고유 ID */
    private final Long id;

    /** 사용자에게 보여지는 카테고리 이름 */
    private final String name;

    /**
     * JSON 역직렬화 시 문자열을 enum으로 매핑합니다.
     * 입력 문자열은 enum 이름(CRAFT) 또는 name(공예) 둘 다 허용됩니다.
     *
     * @param input 입력된 문자열
     * @return 매칭되는 {@link UpCategoryEnum}
     * @throws IllegalArgumentException 유효하지 않은 값일 경우 예외 발생
     */
    @JsonCreator
    public static UpCategoryEnum fromName(String input) {
        for (UpCategoryEnum category : values()) {
            if (category.name().equalsIgnoreCase(input) || category.name.equals(input)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Invalid UCategory name: " + input);
    }
    public static UpCategoryEnum fromId(int id) {
        for (UpCategoryEnum u : values()) {
            if (u.id == id) return u;
        }
        throw new IllegalArgumentException("Invalid DCategory id: " + id);
    }

    @Override
    public String toString() {
        return name;
    }

}
