chachacreate_be
<h2 align="left"> <img src="https://github.com/twitter/twemoji/raw/master/assets/svg/1f4cc.svg" width="26" alt="pin" /> 소개 </h2>

chachacreate_be는 Spring Boot + Gradle 기반의 백엔드 프로젝트입니다.
수공예 커뮤니티/커머스 플랫폼의 서버 역할을 담당하며, 회원·스토어·상품·주문·정산 등 핵심 도메인 API를 제공합니다.
프런트엔드 레포(chachacreate_fe)와 표준 응답 규격을 통해 안정적으로 연동됩니다.

ℹ️ 이 저장소는 Git Submodule을 사용합니다. 클론 후 반드시 서브모듈 초기화를 수행하세요.
- git submodule update --init --recursive

<h2> <img src="https://github.com/twitter/twemoji/raw/master/assets/svg/1f680.svg" width="26" alt="rocket" /> 주요 기능 </h2>

🔑 회원가입 / 로그인 / (JWT 중심) 인증·인가

🏪 개인 판매자 / 스토어 판매자 구분 및 관리

🛍️ 상품 등록·수정·삭제·조회

📦 주문 및 결제 연동(외부 PG 연계는 환경에 따라 분리)

💬 메시지/알림(선택 기능) API

📊 판매자 대시보드 / 정산(월별/일별) 통계 API

🧩 레거시(1차) 시스템 연동 브리지

🖼️ 파일 업로드 / 이미지 생성 / 문자·본인인증 등 외부 API 연동(환경변수 설정 필요)

<h2> <img src="https://github.com/twitter/twemoji/raw/master/assets/svg/1f6e0.svg" width="26" alt="tools" /> 기술 스택 </h2>

Language: Java 17+

Framework: Spring Boot (Web, Validation, Security)

Build: Gradle

DB: MySQL 8.x (JPA/Hibernate)

Auth: JWT (Stateless), CORS 설정

Infra(옵션): AWS S3, Redis, 외부 API(SMS/이미지 등)

Test: JUnit5, Mockito

<h2> <img src="https://github.com/twitter/twemoji/raw/master/assets/svg/1f4c1.svg" width="26" alt="folder" /> 폴더 구조 </h2>

```
backend/
├─ gradlew
├─ gradlew.bat
├─ gradle/
│  └─ wrapper/
├─ build.gradle
├─ settings.gradle
├─ .editorconfig
├─ .gitignore
├─ README.md
└─ src/
   ├─ main/
   │  ├─ java/com/example/app/
   │  │  ├─ CreateApplication.java
   │  │  ├─ common/                 # 공통 유틸/응답/에러/필터/검증
   │  │  │  ├─ error/               # 표준 에러 모델 · 전역 예외 처리(Advice)
   │  │  │  ├─ validation/          # 커스텀 Bean Validation 애노테이션/Validator
   │  │  │  ├─ util/                # Date/Masking/Page 등 범용 유틸
   │  │  │  └─ web/                 # 공통 Filter/Interceptor(RequestId 등)
   │  │  ├─ config/                 # @Configuration (환경/프레임워크 설정)
   │  │  │  ├─ security/            # Stateless 보안, CORS, (JWT 필터 등)
   │  │  │  ├─ jackson/             # JSON 직렬화/타임존 정책
   │  │  │  ├─ openapi/             # Swagger(OpenAPI) 설정 (선택)
   │  │  │  └─ app/                 # WebMvc, Tx, Actuator, Converter 등
   │  │  └─ domains/
   │  │     ├─ shared/              # [공통 비즈니스 규칙]
   │  │     │  ├─ repository/
   │  │     │  ├─ entity/
   │  │     │  ├─ constants/        # entity에서 사용하는 상수
   │  │     │  ├─ product/          # [도메인]
   │  │     │  │  ├─ controller/
   │  │     │  │  ├─ service/
   │  │     │  │  │  └─ serviceimpl/
   │  │     │  │  └─ vo/
   │  │     │  ├─ order/            # [도메인]
   │  │     │  │  ├─ controller/
   │  │     │  │  ├─ service/
   │  │     │  │  │  └─ serviceimpl/
   │  │     │  │  └─ vo/
   │  │     │  └─ member/           # [도메인]
   │  │     │     ├─ controller/
   │  │     │     ├─ service/
   │  │     │     │  └─ serviceimpl/
   │  │     │     └─ vo/
   │  │     ├─ seller/              # [도메인: 판매자]
   │  │     │  ├─ areas/
   │  │     │  │  ├─ product/
   │  │     │  │  │  └─ productlist # [feature]
   │  │     │  │  │     ├─ controller/
   │  │     │  │  │     ├─ dto/
   │  │     │  │  │     │  ├─ request/
   │  │     │  │  │     │  └─ response/
   │  │     │  │  │     ├─ service/
   │  │     │  │  │     │  └─ serviceimpl/
   │  │     │  │  └─ store/
   │  │     │  │     └─ storeinfo   # [feature]
   │  │     │  │        ├─ controller/
   │  │     │  │        ├─ dto/
   │  │     │  │        │  ├─ request/
   │  │     │  │        │  └─ response/
   │  │     │  │        ├─ service/
   │  │     │  │        │  └─ serviceimpl/
   │  │     │  │        └─ validator/   # (선택)
   │  │     ├─ admin/               # [도메인: 관리자]
   │  │     │  ├─ areas/
   │  │     │  │  ├─ report/
   │  │     │  │  │  └─ reportlist  # [feature]
   │  │     │  │  │     ├─ controller/
   │  │     │  │  │     ├─ dto/
   │  │     │  │  │     │  ├─ request/
   │  │     │  │  │     │  └─ response/
   │  │     │  │  │     ├─ service/
   │  │     │  │  │     │  └─ serviceimpl/
   │  │     │  │  └─ member/
   │  │     │  │     └─ memberlist  # [feature]
   │  │     │  │        ├─ controller/
   │  │     │  │        ├─ dto/
   │  │     │  │        │  ├─ request/
   │  │     │  │        │  └─ response/
   │  │     │  │        ├─ service/
   │  │     │  │        │  └─ serviceimpl/
   │  │     │  │        └─ validator/   # (선택)
   │  │     └─ buyer/               # [도메인: 구매자]
   │  │        ├─ areas/
   │  │        │  ├─ order/
   │  │        │  │  └─ orderdetail # [feature]
   │  │        │  │     ├─ controller/
   │  │        │  │     ├─ dto/
   │  │        │  │     │  ├─ request/
   │  │        │  │     │  └─ response/
   │  │        │  │     ├─ service/
   │  │        │  │     │  └─ serviceimpl/
   │  │        │  └─ member/
   │  │        │     └─ memberinfo  # [feature]
   │  │        │        ├─ controller/
   │  │        │        ├─ dto/
   │  │        │        │  ├─ request/
   │  │        │        │  └─ response/
   │  │        │        ├─ service/
   │  │        │        │  └─ serviceimpl/
   │  │        │        └─ validator/   # (선택)
   │  │        │        └─ checkout/    # [area]
   │  │        │           ├─ controller/
   │  │        │           │  └─ checkout_controller.java
   │  │        │           ├─ dto/
   │  │        │           │  ├─ request/
   │  │        │           │  │  ├─ checkout_start_request.java
   │  │        │           │  │  ├─ checkout_confirm_request.java
   │  │        │           │  │  └─ apply_coupon_request.java
   │  │        │           │  └─ response/
   │  │        │           │     ├─ checkout_session_response.java
   │  │        │           │     ├─ pricing_breakdown_response.java
   │  │        │           │     ├─ payment_intent_response.java
   │  │        │           │     └─ order_id_response.java
   │  │        │           ├─ service/
   │  │        │           │  ├─ checkout_facade.java
   │  │        │           │  └─ serviceimpl/
   │  │        │           │     └─ checkout_facade_impl.java
   │  │        │           ├─ client/
   │  │        │           │  └─ payment_gateway_client.java
   │  │        │           ├─ validator/
   │  │        │           │  └─ checkout_validator.java
   │  │        │           └─ mapper/
   │  │        │              └─ checkout_mapper.java
   └─ resources/
      ├─ application.yml
      ├─ application-dev.yml
      ├─ application-prod.yml
      ├─ logback-spring.xml
      └─ db/
         └─ migration/            # (권장) Flyway 스크립트 V1__...sql

test/
└─ java/com/example/app/
   ├─ unit/                       # 단위 테스트(JUnit5 + Mockito)
   └─ integration/                # 통합 테스트(@SpringBootTest, Testcontainers)
```

<h2> <img src="https://github.com/twitter/twemoji/raw/master/assets/svg/2699.svg" width="26" alt="gear" /> 실행 방법 </h2>

설치
- git clone https://github.com/chachacreate/chachacreate_be.git
- cd chachacreate_be
# 저장소 클론 시 서브모듈도 함께 받기
- git clone --recurse-submodules https://github.com/chachacreate/chachacreate_be.git
- git clone --recurse-submodules https://github.com/chachacreate/chachacreate_legacy.git
- git clone https://github.com/chachacreate/chachacreate_fe.git

# 또는 이미 클론한 경우
- git submodule update --init --recursive

# Submodule 업데이트 받기
서브모듈 변경사항이 있을 때마다 실행
- git submodule update --remote
- git submodule foreach git pull origin main

환경 변수 / YAML 설정

민감정보는 환경변수로 관리

# 개발 서버 실행
- ./gradlew clean bootRun
- Windows: gradlew.bat clean bootRun
- 기본 URL: http://localhost:9999

<h2> <img src="https://github.com/twitter/twemoji/raw/master/assets/svg/2705.svg" width="26" alt="check" /> 코드 컨벤션 </h2>

레이어드 아키텍처: Controller → Service → Repository 분리

DTO/VO 분리: 요청/응답 전용 DTO와 엔티티 책임 분리

예외/로그 표준화: 전역 예외 처리(ControllerAdvice), 구조화된 로깅 포맷

응답 규격 통일: 상태코드/메시지/데이터 3요소 고정 (팀 ResponseCode 기준)

네이밍/패키징: 도메인/area/feature 규칙 준수(폴더 구조 참조)

DB 컨벤션: 스키마 네이밍, 인덱스/제약 설정, 마이그레이션(Flyway) 권장

보안: Secrets은 환경변수로, CORS/보안 헤더/JWT 만료·재발급 정책 준수

(프런트의 ESLint/Prettier/타입체킹처럼) 백엔드는 Checkstyle/Spotless(선택), Null-safety, Validation 권장

<h2> <img src="https://github.com/twitter/twemoji/raw/master/assets/svg/1f517.svg" width="26" alt="link" /> 연동 & 환경 </h2>

프런트 도메인/CORS: chachacreate_fe 개발 포트와 매칭

레거시 브리지: 1차 시스템(Oracle/JSP 등)과의 Gateway/DTO 변환 계층

외부 서비스: 솔라피(SMS), S3, 이미지 생성 등은 환경별로 활성화

<h2> <img src="https://github.com/twitter/twemoji/raw/master/assets/svg/1f4d6.svg" width="26" alt="book" /> 라이선스 </h2>

이 프로젝트는 Apache-2.0 라이선스를 따릅니다.

<h2>👥 팀 소개</h2>

- 차민건: PM, 로그인/채팅 구현, 서비스 배포 및 관리
- 천희찬: 주문 상태 처리 구현, 상품 판매 통계 그래프 구현
- 안세현: 상품 정산/매출 구현, 클래스 예약 조회/ 통계 구현
- 김지민: 반응형 디자인, React 페이지 제작 및 라우팅
- 최윤정: 가격추천 인공지능 학습, 상품/클래스 등록/수정/삭제 구현
- 이재희: RDBMS/NoSQL/S3 배포 및 관리, 상품/스토어 결제 및 주문 구현
