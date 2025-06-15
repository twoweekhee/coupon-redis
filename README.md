# Coupon Redis System

## 📋 프로젝트 개요

Redis를 활용한 고성능 쿠폰 발급 시스템입니다. 대용량 트래픽 환경에서 안정적으로 선착순 쿠폰을 발급할 수 있도록 설계되었습니다.

## 🎯 요구사항

- **1계정 1쿠폰**: 한 사용자당 최대 1개의 쿠폰만 발급 가능
- **선착순 100명**: 총 100개의 쿠폰을 선착순으로 발급
- **고성능**: 대량의 동시 요청을 안정적으로 처리
- **미리 생성된 쿠폰**: 쿠폰을 사전에 생성하여 매핑 방식으로 빠른 발급

## 🏗️ 시스템 아키텍처

### 핵심 설계 원칙
1. **쿠폰 사전 생성**: 100개의 쿠폰을 미리 Redis에 저장
2. **원자적 연산**: Redis의 원자적 연산을 활용하여 동시성 문제 해결
3. **중복 발급 방지**: 사용자별 발급 이력 관리
4. **빠른 응답**: 인메모리 데이터베이스의 특성을 활용한 고속 처리

### Redis 데이터 구조
```
available_coupons (LIST): 발급 가능한 쿠폰 ID 목록
issued_coupons (SET): 발급된 쿠폰 ID 집합
user_coupons (HASH): 사용자별 발급받은 쿠폰 정보
coupon_details (HASH): 쿠폰 상세 정보
```

## 🚀 주요 기능

### 1. 쿠폰 초기화
- 100개의 고유한 쿠폰 생성
- Redis에 쿠폰 정보 저장
- 발급 가능한 쿠폰 큐 초기화

### 2. 쿠폰 발급
- 사용자 중복 발급 체크
- 원자적 쿠폰 할당
- 발급 결과 반환

### 3. 발급 현황 조회
- 총 발급량 조회
- 남은 쿠폰 수량 확인
- 사용자별 발급 이력 조회

## 💻 기술 스택

- **언어**: Java 
- **데이터베이스**: Redis
- **프레임워크**: Spring Boot 

## 📝 API 명세

### POST /coupon/issue
쿠폰 발급 요청
```json
{
  "userId": "user123"
}
```

**응답**
```json
{
  "success": true,
  "couponId": "COUPON_001",
  "message": "쿠폰이 성공적으로 발급되었습니다."
}
```

### GET /coupon/status
발급 현황 조회
```json
{
  "totalCoupons": 100,
  "issuedCount": 45,
  "remainingCount": 55
}
```

### GET /coupon/user/{userId}
사용자 쿠폰 조회
```json
{
  "userId": "user123",
  "couponId": "COUPON_001",
  "issuedAt": "2025-06-09T10:30:00Z"
}
```

## 🔧 설치 및 실행

### 1. Redis 설치
```bash
# Docker를 이용한 Redis 실행
docker run -d -p 6379:6379 redis:latest
```

### 2. 프로젝트 실행
```bash
# 의존성 설치
npm install / pip install -r requirements.txt / mvn install

# 애플리케이션 실행
npm start / python app.py / mvn spring-boot:run
```

### 3. 쿠폰 초기화
```bash
# 초기 쿠폰 데이터 생성
curl -X POST http://localhost:8080/admin/init-coupons
```

## 🧪 테스트

### 부하 테스트
동시 요청 처리 능력 검증을 위한 테스트 시나리오:
- 동시 사용자 1000명
- 각각 쿠폰 발급 요청
- 정확히 100개만 발급되는지 확인
- 중복 발급 없음 검증

### 단위 테스트
- 쿠폰 발급 로직 테스트
- 중복 발급 방지 테스트
- 수량 제한 테스트

## 🎯 성능 최적화 포인트

1. **Redis Pipeline**: 여러 Redis 명령을 일괄 처리
2. **Connection Pool**: Redis 연결 풀 관리
3. **Lua Script**: 복잡한 로직을 원자적으로 실행
4. **메모리 최적화**: 적절한 데이터 구조 선택

## 🔍 모니터링

- 쿠폰 발급 속도 추적
- Redis 메모리 사용량 모니터링
- 에러율 및 응답시간 측정

## 🚨 예외 상황 처리

- Redis 연결 실패 시 fallback 전략
- 쿠폰 소진 시 적절한 메시지 반환
- 중복 요청에 대한 idempotent 처리

## 트러블 슈팅

### redis config, 코틀린 기본 생성자 없음 이슈

✅ 현재 설정 (둘 다 있음)
```
kotlinconfigure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
activateDefaultTyping(...)
```
결과: @class 포함 저장 → 타입 정보로 정확한 객체 복원 → 알려지지 않은 필드 무시

❌ FAIL_ON_UNKNOWN_PROPERTIES = false만 있는 경우
```
kotlinconfigure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
// activateDefaultTyping 없음
```
결과: 타입 정보 없이 저장 → LinkedHashMap으로 역직렬화 → ClassCastException

❌ activateDefaultTyping만 있는 경우
```
kotlin// FAIL_ON_UNKNOWN_PROPERTIES = true (기본값)
activateDefaultTyping(...)
```
결과: @class 포함 저장 → @class 필드 인식 못함 → UnrecognizedPropertyException
