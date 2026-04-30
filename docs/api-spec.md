# API 명세서

이 문서는 현재 Spring Boot 프로젝트의 컨트롤러, DTO, 보안 설정을 기준으로 작성합니다.

## 공통 규칙

### Base URL

- 로컬 실행 기준: `http://localhost:8080`
- 별도 표기가 없으면 모든 API 응답은 JSON입니다.

### 인증

- 인증 방식: JWT Bearer 토큰
- 로그인 성공 후 응답의 `data.accessToken` 값을 다음 요청의 헤더에 포함합니다.

```http
Authorization: Bearer {accessToken}
```

- 인증 없이 호출 가능: `POST /admins/signup`, `POST /admins/login`
- 그 외 API는 인증이 필요합니다.

### 공통 응답

모든 성공 및 실패 응답은 `CommonApiResponse<T>` 구조를 따릅니다.
응답 바디의 `status`는 공통 응답 정책에 따라 `200`으로 고정합니다.
`data`가 `null`이면 JSON 응답에서 생략될 수 있습니다.

```json
{
  "status": 200,
  "message": "응답 메시지",
  "data": {}
}
```

에러 응답도 같은 wrapper를 사용하며 `code`가 추가됩니다. `data`는 필드 검증 실패처럼 상세 정보가 있을 때만 포함됩니다.

```json
{
  "status": 200,
  "code": "VALIDATION_FAILED",
  "message": "입력값이 올바르지 않습니다.",
  "data": [
    "이름은 필수입니다."
  ]
}
```

### 권한 정책

아래 권한 정책은 `SecurityConfig`의 `authorizeHttpRequests` 설정과 동일합니다. 위에서부터 먼저 매칭되는 규칙이 적용됩니다.

| Method | URL 패턴                                                                               | 접근 권한                                         |
|--------|--------------------------------------------------------------------------------------|-----------------------------------------------|
| ALL    | `/admins/signup`, `/admins/login`                                                    | 인증 불필요                                        |
| ALL    | `/admins/me/**`                                                                      | 로그인한 관리자                                      |
| POST   | `/admins/logout`                                                                     | 로그인한 관리자                                      |
| GET    | `/admin/dashboard/**`                                                                | `SUPER_ADMIN`, `OPERATIONS_ADMIN`, `CS_ADMIN` |
| ALL    | `/admins/**`                                                                         | `SUPER_ADMIN`                                 |
| DELETE | `/admin/customers/**`                                                                | `SUPER_ADMIN`                                 |
| PUT    | `/admin/customers/**`                                                                | `SUPER_ADMIN`, `OPERATIONS_ADMIN`             |
| PATCH  | `/admin/customers/**`                                                                | `SUPER_ADMIN`, `OPERATIONS_ADMIN`             |
| DELETE | `/admin/products/*/reviews/**`                                                       | `SUPER_ADMIN`, `OPERATIONS_ADMIN`             |
| POST   | `/admin/products/**`                                                                 | `SUPER_ADMIN`, `OPERATIONS_ADMIN`             |
| PUT    | `/admin/products/**`                                                                 | `SUPER_ADMIN`, `OPERATIONS_ADMIN`             |
| PATCH  | `/admin/products/**`                                                                 | `SUPER_ADMIN`, `OPERATIONS_ADMIN`             |
| DELETE | `/admin/products/**`                                                                 | `SUPER_ADMIN`, `OPERATIONS_ADMIN`             |
| PATCH  | `/admin/orders/*/cancel`                                                             | `SUPER_ADMIN`, `OPERATIONS_ADMIN`, `CS_ADMIN` |
| PATCH  | `/admin/orders/**`                                                                   | `SUPER_ADMIN`, `OPERATIONS_ADMIN`             |
| PUT    | `/admin/orders/**`                                                                   | `SUPER_ADMIN`, `OPERATIONS_ADMIN`             |
| DELETE | `/admin/reviews/**`                                                                  | `SUPER_ADMIN`, `OPERATIONS_ADMIN`             |
| GET    | `/admin/customers/**`, `/admin/products/**`, `/admin/orders/**`, `/admin/reviews/**` | `SUPER_ADMIN`, `OPERATIONS_ADMIN`, `CS_ADMIN` |
| ALL    | 그 외 명시되지 않은 API                                                                      | 로그인한 관리자                                      |

### 공통 Enum

- `AdminRole`: `SUPER_ADMIN`, `OPERATIONS_ADMIN`, `CS_ADMIN`
- `AdminStatus`: `ACTIVE`, `INACTIVE`, `SUSPENDED`, `PENDING_APPROVAL`, `REJECTED`
- `CustomerStatus`: `ACTIVE`, `INACTIVE`, `SUSPENDED`
- `ProductStatus`: `ON_SALE`, `OUT_OF_STOCK`, `DISCONTINUED`
- `OrderStatus`: `PREPARING`, `SHIPPING`, `DELIVERED`, `CANCELLED`
- `DeletionStatus`: `NOT_DELETED`, `DELETED`

### 주요 에러 코드

| 코드                                | HTTP | 설명                          |
|-----------------------------------|-----:|-----------------------------|
| `VALIDATION_FAILED`               |  400 | 요청 본문, 쿼리 파라미터, 경로 변수 검증 실패 |
| `INVALID_ROLE`                    |  400 | 잘못된 관리자 역할                  |
| `INVALID_ADMIN_STATUS`            |  400 | 잘못된 관리자 상태                  |
| `INVALID_CUSTOMER_STATUS`         |  400 | 잘못된 고객 상태                   |
| `INVALID_PRODUCT_STATUS`          |  400 | 잘못된 상품 상태                   |
| `INVALID_ORDER_STATUS`            |  400 | 잘못된 주문 상태                   |
| `INVALID_APPROVAL_DECISION`       |  400 | 잘못된 승인 처리 요청                |
| `REJECT_REASON_REQUIRED`          |  400 | 거절 사유 누락                    |
| `PASSWORD_CONFIRM_MISMATCH`       |  400 | 현재 비밀번호 불일치                 |
| `PASSWORD_NEW_CONFIRM_MISMATCH`   |  400 | 새 비밀번호 확인 불일치               |
| `INVALID_STOCK_VALUE`             |  400 | 잘못된 재고 값                    |
| `INVALID_QUANTITY`                |  400 | 잘못된 주문 수량                   |
| `INVALID_RATING_VALUE`            |  400 | 잘못된 리뷰 평점                   |
| `TOKEN_REQUIRED`                  |  401 | 인증 토큰 누락                    |
| `TOKEN_EXPIRED`                   |  401 | 만료된 토큰                      |
| `INVALID_TOKEN_SIGNATURE`         |  401 | 잘못된 토큰 서명                   |
| `INVALID_TOKEN`                   |  401 | 유효하지 않은 토큰                  |
| `INVALID_TOKEN_FORMAT`            |  401 | 잘못된 토큰 형식                   |
| `UNAUTHORIZED`                    |  401 | 인증 정보 없음                    |
| `FORBIDDEN`                       |  403 | 권한 부족                       |
| `ADMIN_PENDING_APPROVAL`          |  403 | 승인 대기 계정 로그인 시도             |
| `ADMIN_REJECTED`                  |  403 | 승인 거절 계정 로그인 시도             |
| `ADMIN_SUSPENDED`                 |  403 | 정지 계정 로그인 시도                |
| `ADMIN_INACTIVE`                  |  403 | 비활성 계정 로그인 시도               |
| `INVALID_PASSWORD`                |  403 | 비밀번호 검증 실패                  |
| `INVALID_CREDENTIALS`             |  404 | 로그인 이메일 또는 비밀번호 불일치         |
| `ADMIN_NOT_FOUND`                 |  404 | 관리자 없음                      |
| `CUSTOMER_NOT_FOUND`              |  404 | 고객 없음                       |
| `PRODUCT_NOT_FOUND`               |  404 | 상품 없음                       |
| `PRODUCT_DELETE_NOT_ALLOWED`      |  409 | 삭제할 수 없는 상품                 |
| `ORDER_NOT_FOUND`                 |  404 | 주문 없음                       |
| `REVIEW_NOT_FOUND`                |  404 | 리뷰 없음                       |
| `EMAIL_DUPLICATE`                 |  409 | 이메일 중복                      |
| `INSUFFICIENT_STOCK`              |  409 | 재고 부족                       |
| `INVALID_ORDER_STATUS_TRANSITION` |  409 | 허용되지 않는 주문 상태 변경            |
| `ORDER_CANCEL_NOT_ALLOWED`        |  409 | 취소할 수 없는 주문                 |
| `CANNOT_DELETE_SUPER_ADMIN`       |  409 | 슈퍼 관리자 삭제 불가                |
| `CANNOT_DELETE_ACTIVE_ADMIN`      |  409 | 활성 관리자 삭제 불가                |
| `CANNOT_DELETE_PENDING_ADMIN`     |  409 | 승인 대기 관리자 삭제 불가             |
| `CANNOT_DELETE_REJECTED_ADMIN`    |  409 | 거절 관리자 삭제 불가                |
| `ADMIN_ALREADY_DELETED`           |  409 | 이미 삭제된 관리자                  |
| `CUSTOMER_DELETE_NOT_ALLOWED`     |  409 | 삭제할 수 없는 고객                 |
| `CUSTOMER_ALREADY_DELETED`        |  409 | 이미 삭제된 고객                   |
| `PRODUCT_ALREADY_DELETED`         |  409 | 이미 삭제된 상품                   |
| `REVIEW_ALREADY_DELETED`          |  409 | 이미 삭제된 리뷰                   |
| `ALREADY_PROCESSED_ADMIN`         |  409 | 이미 승인 또는 거절 처리된 관리자         |
| `PRODUCT_DISCONTINUED`            |  409 | 단종 상품 주문 시도                 |
| `INTERNAL_SERVER_ERROR`           |  500 | 처리되지 않은 서버 오류               |

## 인증 API

### 관리자 회원가입

기능 설명

새 관리자 계정을 생성합니다. 생성된 계정은 기본적으로 `PENDING_APPROVAL` 상태이며, 이후 슈퍼 관리자의 승인 절차를 거쳐 사용할 수 있습니다.

`POST /admins/signup`

- 인증: 불필요
- 응답 바디의 `status`: `200`

Request Body

```json
{
  "name": "홍길동",
  "email": "admin@example.com",
  "password": "P@ssw0rd!",
  "tele": "010-1234-5678",
  "role": "OPERATIONS_ADMIN"
}
```

검증 조건

- `name`: 필수, 최대 30자
- `email`: 필수, 이메일 형식, 최대 50자
- `password`: 필수, 8자 이상 255자 이하
- `tele`: 필수, `010-XXXX-XXXX` 형식, 최대 20자
- `role`: 필수, `AdminRole`

Response Body

```json
{
  "status": 200,
  "message": "관리자 회원가입이 완료되었습니다."
}
```

### 관리자 로그인

기능 설명

관리자 이메일과 비밀번호를 검증한 뒤 JWT Access Token을 발급합니다. 계정 상태가 `ACTIVE`가 아니면 상태별 에러로 로그인을 거부합니다.

`POST /admins/login`

- 인증: 불필요
- 응답: `200 OK`

Request Body

```json
{
  "email": "admin@example.com",
  "password": "P@ssw0rd!"
}
```

Response Body

```json
{
  "status": 200,
  "message": "로그인에 성공했습니다.",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer"
  }
}
```

### 관리자 로그아웃

기능 설명

현재 인증 흐름은 Stateless JWT 기반이므로 서버 세션을 무효화하지 않습니다. 클라이언트가 보관 중인 Access Token을 폐기하는 방식으로 로그아웃을 처리합니다.

`POST /admins/logout`

- 인증: 필요
- 응답: `200 OK`
- 현재 JWT 방식에서는 서버 세션을 제거하지 않으며, 클라이언트가 보유한 토큰을 폐기하는 방식입니다.

Response Body

```json
{
  "status": 200,
  "message": "로그아웃이 완료되었습니다."
}
```

## 관리자 API

보안 설정상 `/admins/me/**`와 `POST /admins/logout`은 로그인한 관리자라면 접근할 수 있고, 그 외 `/admins/**` 관리 기능은 `SUPER_ADMIN` 권한이 필요합니다.

### 관리자 목록 조회

기능 설명

관리자 계정 목록을 페이지 단위로 조회합니다. 검색어, 역할, 상태 조건으로 필터링할 수 있으며 슈퍼 관리자 전용 관리 화면에서 사용합니다.

`GET /admins`

Query Parameters

| 이름          | 필수 | 기본값       | 설명                      |
|-------------|----|-----------|-------------------------|
| `keyword`   | N  | -         | 관리자 이름 또는 이메일 검색어       |
| `role`      | N  | -         | `AdminRole`             |
| `status`    | N  | -         | `AdminStatus`           |
| `page`      | N  | `1`       | 1부터 시작                  |
| `size`      | N  | `10`      | 페이지 크기                  |
| `sortBy`    | N  | -         | 정렬 필드명                  |
| `sortOrder` | N  | `desc` 동작 | `asc`면 오름차순, 그 외에는 내림차순 |

Response Body

```json
{
  "status": 200,
  "message": "관리자 리스트 조회 성공",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "관리자",
        "email": "admin@example.com",
        "tele": "010-1234-5678",
        "role": "SUPER_ADMIN",
        "status": "ACTIVE",
        "createdAt": "2026-04-23T15:00:00",
        "approvedAt": "2026-04-23T15:10:00",
        "updatedAt": "2026-04-23T15:10:00"
      }
    ],
    "page": 1,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### 관리자 상세 조회

기능 설명

특정 관리자 계정의 기본 정보, 역할, 계정 상태, 생성/승인/수정 일시를 조회합니다.

`GET /admins/{adminId}`

Response Body

```json
{
  "status": 200,
  "message": "관리자 상세 조회 성공",
  "data": {
    "id": 1,
    "name": "관리자",
    "email": "admin@example.com",
    "tele": "010-1234-5678",
    "role": "SUPER_ADMIN",
    "status": "ACTIVE",
    "createdAt": "2026-04-23T15:00:00",
    "approvedAt": "2026-04-23T15:10:00",
    "updatedAt": "2026-04-23T15:10:00"
  }
}
```

### 관리자 정보 수정

기능 설명

특정 관리자 계정의 이름, 이메일, 전화번호를 부분 수정합니다. 이메일을 변경하는 경우 중복 여부를 검증합니다.

`PUT /admins/{adminId}`

Request Body

```json
{
  "name": "수정관리자",
  "email": "updated@example.com",
  "tele": "010-1111-2222"
}
```

- 모든 필드는 선택입니다.
- `name`: 최대 30자
- `email`: 이메일 형식, 최대 50자
- `tele`: `010-XXXX-XXXX` 형식, 최대 20자

Response Body: `AdminResponse`

```json
{
  "status": 200,
  "message": "관리자 정보 수정 성공",
  "data": {
    "id": 1,
    "name": "수정관리자",
    "email": "updated@example.com",
    "tele": "010-1111-2222",
    "role": "OPERATIONS_ADMIN",
    "status": "ACTIVE",
    "createdAt": "2026-04-23T15:00:00",
    "approvedAt": "2026-04-23T15:10:00",
    "updatedAt": "2026-04-23T15:20:00"
  }
}
```

### 관리자 역할 변경

기능 설명

특정 관리자 계정의 시스템 역할을 변경합니다. `SUPER_ADMIN`, `OPERATIONS_ADMIN`, `CS_ADMIN` 중 하나로 변경할 수 있습니다.

`PUT /admins/{adminId}/role`

Request Body

```json
{
  "role": "CS_ADMIN"
}
```

Response Body: `AdminResponse`

```json
{
  "status": 200,
  "message": "관리자 역할 수정 성공",
  "data": {
    "id": 1,
    "name": "관리자",
    "email": "admin@example.com",
    "tele": "010-1234-5678",
    "role": "CS_ADMIN",
    "status": "ACTIVE",
    "createdAt": "2026-04-23T15:00:00",
    "approvedAt": "2026-04-23T15:10:00",
    "updatedAt": "2026-04-23T15:20:00"
  }
}
```

### 관리자 상태 변경

기능 설명

특정 관리자 계정의 활동 상태를 변경합니다. 비활성, 정지, 승인 대기 등 운영 정책에 따른 계정 제어에 사용합니다.

`PUT /admins/{adminId}/status`

Request Body

```json
{
  "status": "INACTIVE"
}
```

Response Body: `AdminResponse`

```json
{
  "status": 200,
  "message": "관리자 상태 수정 성공",
  "data": {
    "id": 1,
    "name": "관리자",
    "email": "admin@example.com",
    "tele": "010-1234-5678",
    "role": "OPERATIONS_ADMIN",
    "status": "INACTIVE",
    "createdAt": "2026-04-23T15:00:00",
    "approvedAt": "2026-04-23T15:10:00",
    "updatedAt": "2026-04-23T15:20:00"
  }
}
```

### 관리자 삭제

기능 설명

특정 관리자 계정을 소프트 삭제 처리합니다. 실제 데이터를 물리 삭제하지 않고 `deletionStatus`를 `DELETED`로 변경하며, 슈퍼 관리자나 삭제 제한 상태의 계정은 삭제할 수 없습니다.

`DELETE /admins/{adminId}`

Response Body

```json
{
  "status": 200,
  "message": "관리자 삭제 완료"
}
```

삭제 제한

- `SUPER_ADMIN` 삭제 불가
- `ACTIVE`, `PENDING_APPROVAL`, `REJECTED` 상태 관리자 삭제 불가
- 이미 삭제된 관리자는 `ADMIN_ALREADY_DELETED`

### 관리자 가입 승인/거부

기능 설명

승인 대기 중인 관리자 가입 요청을 승인하거나 거부합니다. 승인 시 계정 상태가 `ACTIVE`가 되고, 거부 시 `REJECTED` 상태가 되며 거부 사유가 기록됩니다.

`PATCH /admins/{adminId}/approval`

Request Body

```json
{
  "isApproved": true,
  "rejectReason": null
}
```

- `isApproved`: 필수
- `rejectReason`: 거부 시 필수, 최대 100자

승인 Response Body

```json
{
  "status": 200,
  "message": "관리자 승인 완료",
  "data": {
    "id": 2,
    "status": "ACTIVE",
    "approvedAt": "2026-04-23T15:40:00",
    "updatedAt": "2026-04-23T15:40:00"
  }
}
```

거부 Response Body

```json
{
  "status": 200,
  "message": "관리자 거절 완료",
  "data": {
    "id": 2,
    "status": "REJECTED",
    "rejectReason": "승인 기준 미충족",
    "rejectedAt": "2026-04-23T15:40:00",
    "updatedAt": "2026-04-23T15:40:00"
  }
}
```

### 내 프로필 조회

기능 설명

현재 로그인한 관리자의 프로필을 조회합니다. JWT 인증 정보에서 관리자 ID를 식별해 본인 계정 정보를 반환합니다.

`GET /admins/me`

Response Body

```json
{
  "status": 200,
  "message": "관리자 프로필 조회 성공",
  "data": {
    "id": 1,
    "name": "관리자",
    "email": "admin@example.com",
    "tele": "010-1234-5678",
    "role": "SUPER_ADMIN",
    "status": "ACTIVE",
    "createdAt": "2026-04-23T15:00:00",
    "approvedAt": "2026-04-23T15:10:00",
    "updatedAt": "2026-04-23T15:10:00"
  }
}
```

### 내 프로필 수정

기능 설명

현재 로그인한 관리자의 이름, 이메일, 전화번호를 수정합니다. 본인 이메일을 유지하는 경우 중복으로 처리하지 않고, 다른 이메일로 변경할 때만 중복 검증을 수행합니다.

`PUT /admins/me`

Request Body

```json
{
  "name": "관리자",
  "email": "admin@example.com",
  "tele": "010-7777-8888"
}
```

Response Body: `AdminResponse`

```json
{
  "status": 200,
  "message": "프로필 수정 완료",
  "data": {
    "id": 1,
    "name": "관리자",
    "email": "admin@example.com",
    "tele": "010-7777-8888",
    "role": "SUPER_ADMIN",
    "status": "ACTIVE",
    "createdAt": "2026-04-23T15:00:00",
    "approvedAt": "2026-04-23T15:10:00",
    "updatedAt": "2026-04-23T15:50:00"
  }
}
```

### 내 비밀번호 변경

기능 설명

현재 로그인한 관리자의 비밀번호를 변경합니다. 현재 비밀번호를 먼저 검증하고, 새 비밀번호와 확인 값이 일치할 때 암호화해 저장합니다.

`PATCH /admins/me/password`

Request Body

```json
{
  "currentPassword": "OldP@ssw0rd!",
  "newPassword": "NewP@ssw0rd!",
  "newPasswordConfirm": "NewP@ssw0rd!"
}
```

- `currentPassword`: 필수
- `newPassword`: 필수, 8자 이상 255자 이하
- `newPasswordConfirm`: 필수

Response Body

```json
{
  "status": 200,
  "message": "비밀번호 변경 완료"
}
```

## 대시보드 API

보안 권한: `SUPER_ADMIN`, `OPERATIONS_ADMIN`, `CS_ADMIN`

### 대시보드 조회

기능 설명

관리자 대시보드 화면에 필요한 종합 통계를 한 번에 조회합니다. 관리자/고객/상품/주문/리뷰 요약, 위젯 수치, 차트용 분포 데이터, 최근 주문 목록을 포함합니다.

`GET /admin/dashboard`

Response Body

```json
{
  "status": 200,
  "message": "대시보드 조회가 완료되었습니다.",
  "data": {
    "summary": {
      "totalAdmins": 10,
      "activeAdmins": 7,
      "totalCustomers": 120,
      "activeCustomers": 95,
      "totalProducts": 80,
      "lowStockProducts": 5,
      "totalOrders": 300,
      "todayOrders": 12,
      "totalReviews": 150,
      "averageRating": 4.3
    },
    "widgets": {
      "totalSales": 15000000,
      "todaySales": 350000,
      "preparingOrders": 4,
      "shippingOrders": 8,
      "deliveredOrders": 250,
      "lowStockProducts": 5,
      "outOfStockProducts": 2
    },
    "charts": {
      "reviewRatings": [
        {
          "rating": 5,
          "count": 70
        }
      ],
      "customerStatuses": [
        {
          "status": "ACTIVE",
          "count": 95
        }
      ],
      "productCategories": [
        {
          "category": "전자기기",
          "count": 30
        }
      ]
    },
    "recentOrders": [
      {
        "orderNumber": 20260427164000123,
        "customerName": "김고객",
        "productName": "무선 마우스",
        "amount": 89000,
        "status": "PREPARING"
      }
    ]
  }
}
```

## 고객 API

- 조회: `SUPER_ADMIN`, `OPERATIONS_ADMIN`, `CS_ADMIN`
- 수정/상태 변경: `SUPER_ADMIN`, `OPERATIONS_ADMIN`
- 삭제: `SUPER_ADMIN`

### 고객 목록 조회

기능 설명

고객 목록을 페이지 단위로 조회합니다. 이름 또는 이메일 검색, 상태 필터, 정렬 조건을 지원하며 각 고객의 주문 집계(`totalOrderCount`, `totalOrderAmount`)를 함께 제공합니다. 취소 주문은 집계에서 제외됩니다.

`GET /admin/customers`

Query Parameters

| 이름          | 필수 | 기본값         | 설명                           |
|-------------|----|-------------|------------------------------|
| `keyword`   | N  | -           | 이름 또는 이메일 검색어                |
| `page`      | N  | `1`         | 1부터 시작                       |
| `size`      | N  | `10`        | 1 이상 100 이하                  |
| `sortBy`    | N  | `createdAt` | `name`, `email`, `createdAt` |
| `sortOrder` | N  | `desc`      | `asc`, `desc`                |
| `status`    | N  | -           | `CustomerStatus`             |

Response Body

```json
{
  "status": 200,
  "message": "고객 리스트 조회 성공",
  "data": {
    "content": [
      {
        "id": 101,
        "name": "김고객",
        "email": "customer@example.com",
        "tele": "010-1234-0000",
        "status": "ACTIVE",
        "createdAt": "2026-04-10T10:00:00",
        "updatedAt": "2026-04-23T12:00:00",
        "totalOrderCount": 3,
        "totalOrderAmount": 120000
      }
    ],
    "page": 1,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### 고객 상세 조회

기능 설명

특정 고객의 기본 정보와 주문 집계 정보를 조회합니다. 삭제 처리된 고객은 조회 대상에서 제외됩니다.

`GET /admin/customers/{customerId}`

Response Body: `CustomerResponse`

```json
{
  "status": 200,
  "message": "고객 상세 조회 성공",
  "data": {
    "id": 101,
    "name": "김고객",
    "email": "customer@example.com",
    "tele": "010-1234-0000",
    "status": "ACTIVE",
    "createdAt": "2026-04-10T10:00:00",
    "updatedAt": "2026-04-23T12:00:00",
    "totalOrderCount": 3,
    "totalOrderAmount": 120000
  }
}
```

### 고객 정보 수정

기능 설명

특정 고객의 이름, 이메일, 전화번호를 부분 수정합니다. 이메일을 변경하는 경우 다른 고객과 중복되는지 검증합니다.

`PUT /admin/customers/{customerId}`

Request Body

```json
{
  "name": "김고객",
  "email": "customer@example.com",
  "tele": "010-5555-6666"
}
```

- 모든 필드는 선택입니다.
- `name`: 공백 불가, 최대 30자
- `email`: 공백 불가, 이메일 형식, 최대 50자
- `tele`: 공백 불가, 최대 30자

Response Body

```json
{
  "status": 200,
  "message": "고객 정보 수정 완료",
  "data": {
    "id": 101,
    "name": "김고객",
    "email": "customer@example.com",
    "tele": "010-5555-6666",
    "status": "ACTIVE",
    "createdAt": "2026-04-10T10:00:00",
    "updatedAt": "2026-04-23T16:00:00",
    "totalOrderCount": 3,
    "totalOrderAmount": 120000
  }
}
```

### 고객 상태 변경

기능 설명

특정 고객의 계정 상태를 변경합니다. 운영자가 고객 이용 상태를 `ACTIVE`, `INACTIVE`, `SUSPENDED` 중 하나로 조정할 때 사용합니다.

`PUT /admin/customers/{customerId}/status`

Request Body

```json
{
  "status": "INACTIVE"
}
```

Response Body

```json
{
  "status": 200,
  "message": "고객 상태 수정 완료",
  "data": {
    "id": 101,
    "name": "김고객",
    "email": "customer@example.com",
    "tele": "010-5555-6666",
    "status": "INACTIVE",
    "createdAt": "2026-04-10T10:00:00",
    "updatedAt": "2026-04-23T16:05:00",
    "totalOrderCount": 3,
    "totalOrderAmount": 120000
  }
}
```

### 고객 삭제

기능 설명

특정 고객을 소프트 삭제 처리합니다. 실제 데이터를 물리 삭제하지 않고 `deletionStatus`를 `DELETED`로 변경합니다.

`DELETE /admin/customers/{customerId}`

Response Body

```json
{
  "status": 200,
  "message": "고객 삭제 완료"
}
```

## 상품 API

- 조회: `SUPER_ADMIN`, `OPERATIONS_ADMIN`, `CS_ADMIN`
- 생성/수정/상태 변경/재고 변경/삭제: `SUPER_ADMIN`, `OPERATIONS_ADMIN`

### 상품 등록

기능 설명

로그인한 관리자를 등록자로 새 상품을 생성합니다. 상품명, 카테고리, 가격, 재고, 요청 상태를 저장하며 재고와 요청 상태 조합에 따라 실제 상품 상태가 자동 보정될 수 있습니다.

`POST /admin/products`

- 응답 바디의 `status`: `200`

Request Body

```json
{
  "name": "무선 마우스",
  "category": "전자기기",
  "price": 35000,
  "stock": 100,
  "status": "ON_SALE"
}
```

- `name`: 필수, 최대 30자
- `category`: 필수, 최대 30자
- `price`: 필수, 0 이상
- `stock`: 필수, 0 이상
- `status`: 필수, `ProductStatus`

Response Body

```json
{
  "status": 200,
  "message": "상품 생성 완료",
  "data": {
    "id": 1001,
    "adminName": "운영관리자",
    "name": "무선 마우스",
    "category": "전자기기",
    "price": 35000,
    "stock": 100,
    "status": "ON_SALE",
    "createdAt": "2026-04-23T16:10:00",
    "updatedAt": "2026-04-23T16:10:00"
  }
}
```

### 상품 목록 조회

기능 설명

상품 목록을 페이지 단위로 조회합니다. 상품명 검색, 카테고리, 상품 상태, 정렬 조건을 지원합니다.

`GET /admin/products`

Query Parameters

| 이름          | 필수 | 기본값         | 설명                            |
|-------------|----|-------------|-------------------------------|
| `keyword`   | N  | -           | 상품명 검색어                       |
| `category`  | N  | -           | 카테고리 검색 조건                    |
| `status`    | N  | -           | `ProductStatus`               |
| `page`      | N  | `1`         | 1부터 시작                        |
| `size`      | N  | `10`        | 1 이상 100 이하                   |
| `sortBy`    | N  | `createdAt` | `createdAt`, `price`, `stock` |
| `sortOrder` | N  | `desc`      | `asc`, `desc`                 |

Response Body

```json
{
  "status": 200,
  "message": "상품 목록 조회 성공",
  "data": {
    "content": [
      {
        "id": 1001,
        "adminName": "운영관리자",
        "name": "무선 마우스",
        "category": "전자기기",
        "price": 35000,
        "stock": 100,
        "status": "ON_SALE",
        "createdAt": "2026-04-23T16:10:00",
        "updatedAt": "2026-04-23T16:10:00"
      }
    ],
    "page": 1,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### 상품 상세 조회

기능 설명

특정 상품의 상세 정보와 등록 관리자 정보를 조회합니다. 상품 상세에는 평균 평점, 평점 분포, 최신 리뷰 목록을 포함한 리뷰 요약 정보가 함께 제공됩니다.

`GET /admin/products/{productId}`

Response Body

```json
{
  "status": 200,
  "message": "상품 상세 조회 성공",
  "data": {
    "id": 1001,
    "adminName": "운영관리자",
    "adminEmail": "operation@example.com",
    "name": "무선 마우스",
    "category": "전자기기",
    "price": 35000,
    "stock": 100,
    "status": "ON_SALE",
    "createdAt": "2026-04-23T16:10:00",
    "updatedAt": "2026-04-23T16:10:00",
    "reviewSummary": {
      "averageRating": 4.5,
      "totalCount": 2,
      "ratingDistribution": {
        "1": 0,
        "2": 0,
        "3": 0,
        "4": 1,
        "5": 1
      },
      "latestReviews": [
        {
          "id": 7001,
          "customerName": "김고객",
          "rating": 5,
          "content": "좋아요",
          "createdAt": "2026-04-23T13:00:00"
        }
      ]
    }
  }
}
```

### 상품 정보 수정

기능 설명

상품의 기본 정보인 상품명, 카테고리, 가격을 부분 수정합니다. 재고와 판매 상태는 별도 API에서 변경합니다.

`PUT /admin/products/{productId}`

Request Body

```json
{
  "name": "무선 마우스 PRO",
  "category": "전자기기",
  "price": 39000
}
```

- 모든 필드는 선택입니다.
- `name`: 최대 30자. `null`이면 기존 값 유지, 공백이면 실패
- `category`: 최대 30자. `null`이면 기존 값 유지, 공백이면 실패
- `price`: 0 이상. `null`이면 기존 값 유지

Response Body

```json
{
  "status": 200,
  "message": "상품 정보 수정 성공",
  "data": {
    "id": 1001,
    "adminName": "운영관리자",
    "name": "무선 마우스 PRO",
    "category": "전자기기",
    "price": 39000,
    "stock": 100,
    "status": "ON_SALE",
    "createdAt": "2026-04-23T16:10:00",
    "updatedAt": "2026-04-23T16:20:00"
  }
}
```

### 상품 재고 변경

기능 설명

상품 재고를 지정한 절대값으로 변경합니다. 단종 상품이 아니면 재고가 0이면 `OUT_OF_STOCK`, 1 이상이면 `ON_SALE`로 상태가 자동 전환됩니다.

`PUT /admin/products/{productId}/stock`

Request Body

```json
{
  "stock": 85
}
```

Response Body

```json
{
  "status": 200,
  "message": "상품 재고 변경 성공",
  "data": {
    "id": 1001,
    "adminName": "운영관리자",
    "name": "무선 마우스",
    "category": "전자기기",
    "price": 35000,
    "stock": 85,
    "status": "ON_SALE",
    "createdAt": "2026-04-23T16:10:00",
    "updatedAt": "2026-04-23T16:25:00"
  }
}
```

### 상품 상태 변경

기능 설명

상품의 판매 상태를 운영자가 명시적으로 변경합니다. `DISCONTINUED`로 변경된 상품은 이후 재고가 변경되어도 단종 상태를 유지합니다.

`PUT /admin/products/{productId}/status`

Request Body

```json
{
  "status": "DISCONTINUED"
}
```

Response Body

```json
{
  "status": 200,
  "message": "상품 상태 변경 성공",
  "data": {
    "id": 1001,
    "adminName": "운영관리자",
    "name": "무선 마우스",
    "category": "전자기기",
    "price": 35000,
    "stock": 85,
    "status": "DISCONTINUED",
    "createdAt": "2026-04-23T16:10:00",
    "updatedAt": "2026-04-23T16:30:00"
  }
}
```

### 상품 삭제

기능 설명

특정 상품을 소프트 삭제 처리합니다. 실제 데이터를 물리 삭제하지 않고 `deletionStatus`만 `DELETED`로 변경하며, 기존 주문과 리뷰 이력은 보존됩니다.

`DELETE /admin/products/{productId}`

Response Body

```json
{
  "status": 200,
  "message": "상품 삭제 완료"
}
```

## 주문 API

- 조회: `SUPER_ADMIN`, `OPERATIONS_ADMIN`, `CS_ADMIN`
- 생성: 인증된 관리자
- 상태 변경: `SUPER_ADMIN`, `OPERATIONS_ADMIN`
- 취소: `SUPER_ADMIN`, `OPERATIONS_ADMIN`, `CS_ADMIN`

### 주문 생성

기능 설명

로그인한 관리자가 고객과 상품을 지정해 CS 주문을 생성합니다. 주문번호를 자동 생성하고, 생성 시점의 상품 가격으로 단가와 총액을 저장하며, 주문 수량만큼 상품 재고를 차감합니다.

서버 처리

- 주문 상태는 `PREPARING`으로 시작합니다.
- 주문번호는 현재 시각과 난수를 조합해 생성합니다.
- `unitPrice`는 주문 당시 상품 가격입니다.
- `totalPrice`는 `unitPrice * quantity`로 계산합니다.
- 삭제 또는 단종 상품은 주문할 수 없습니다.
- 재고가 부족하면 주문 생성에 실패합니다.

`POST /admin/orders`

- 응답 바디의 `status`: `200`

Request Body

```json
{
  "customerId": 101,
  "productId": 1001,
  "quantity": 2
}
```

- `customerId`: 필수, 1 이상
- `productId`: 필수, 1 이상
- `quantity`: 필수, 1 이상

Response Body

```json
{
  "status": 200,
  "message": "주문 생성 성공",
  "data": {
    "id": 5001,
    "orderNumber": 20260427164000123,
    "customerId": 101,
    "productId": 1001,
    "quantity": 2,
    "unitPrice": 35000,
    "totalPrice": 70000,
    "status": "PREPARING",
    "adminName": "운영관리자",
    "createdAt": "2026-04-23T16:40:00",
    "updatedAt": "2026-04-23T16:40:00"
  }
}
```

### 주문 목록 조회

기능 설명

주문 목록을 페이지 단위로 조회합니다. 고객명 또는 숫자 주문번호로 검색할 수 있고, 주문 상태와 정렬 조건을 적용할 수 있습니다.

`GET /admin/orders`

Query Parameters

| 이름          | 필수 | 기본값         | 설명                                    |
|-------------|----|-------------|---------------------------------------|
| `keyword`   | N  | -           | 고객명 또는 숫자 주문번호 검색어                    |
| `page`      | N  | `1`         | 1부터 시작                                |
| `size`      | N  | `10`        | 1 이상 100 이하                           |
| `sortBy`    | N  | `createdAt` | `quantity`, `totalPrice`, `createdAt` |
| `sortOrder` | N  | `desc`      | `asc`, `desc`                         |
| `status`    | N  | -           | `OrderStatus`                         |

Response Body

```json
{
  "status": 200,
  "message": "주문 리스트 조회 성공",
  "data": {
    "content": [
      {
        "id": 5001,
        "orderNumber": 20260427164000123,
        "customerName": "김고객",
        "productName": "무선 마우스",
        "quantity": 2,
        "totalPrice": 70000,
        "createdAt": "2026-04-23T16:40:00",
        "status": "PREPARING",
        "adminName": "운영관리자"
      }
    ],
    "page": 1,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### 주문 상세 조회

기능 설명

특정 주문의 상세 정보를 조회합니다. 주문 고객, 상품, 주문 상태, 담당 관리자 정보를 함께 제공합니다.

`GET /admin/orders/{orderId}`

Response Body

```json
{
  "status": 200,
  "message": "주문 상세 조회 성공",
  "data": {
    "id": 5001,
    "orderNumber": 20260427164000123,
    "customerName": "김고객",
    "customerEmail": "customer@example.com",
    "productName": "무선 마우스",
    "quantity": 2,
    "totalPrice": 70000,
    "createdAt": "2026-04-23T16:40:00",
    "status": "PREPARING",
    "adminName": "운영관리자",
    "adminEmail": "operation@example.com",
    "adminRole": "OPERATIONS_ADMIN"
  }
}
```

`adminName`, `adminEmail`, `adminRole`은 주문 담당 관리자가 없으면 `null`입니다.

### 주문 상태 변경

기능 설명

주문 배송 처리 상태를 변경합니다. 현재 구현은 `PREPARING -> SHIPPING`, `SHIPPING -> DELIVERED` 전이만 허용합니다.

`PUT /admin/orders/{orderId}/status`

Request Body

```json
{
  "status": "SHIPPING"
}
```

허용 전이

- `PREPARING` -> `SHIPPING`
- `SHIPPING` -> `DELIVERED`
- `CANCELLED` 변경은 주문 취소 API를 사용합니다.

Response Body

```json
{
  "status": 200,
  "message": "주문 상태 수정 완료",
  "data": {
    "id": 5001,
    "status": "SHIPPING",
    "updatedAt": "2026-04-23T17:00:00"
  }
}
```

### 주문 취소

기능 설명

특정 주문을 취소합니다. `PREPARING` 상태의 주문만 취소할 수 있고, 취소 사유를 저장한 뒤 주문 수량만큼 상품 재고를 복구합니다.

`PATCH /admin/orders/{orderId}/cancel`

Request Body

```json
{
  "cancelReason": "고객 변심"
}
```

- `cancelReason`: 필수, 공백 불가, 최대 255자
- `PREPARING` 상태 주문만 취소할 수 있습니다.
- 취소 시 주문 상품 재고가 복구됩니다.

Response Body

```json
{
  "status": 200,
  "message": "주문 취소 성공",
  "data": {
    "id": 5001,
    "status": "CANCELLED",
    "cancelReason": "고객 변심",
    "updatedAt": "2026-04-23T17:10:00"
  }
}
```

## 리뷰 API

- 조회: `SUPER_ADMIN`, `OPERATIONS_ADMIN`, `CS_ADMIN`
- 삭제: `SUPER_ADMIN`, `OPERATIONS_ADMIN`

### 리뷰 목록 조회

기능 설명

특정 상품에 작성된 리뷰 목록을 페이지 단위로 조회합니다. 고객명 또는 상품명 검색, 평점 필터, 정렬 조건을 지원합니다.

`GET /admin/products/{productId}/reviews`

Query Parameters

| 이름          | 필수 | 기본값         | 설명                    |
|-------------|----|-------------|-----------------------|
| `keyword`   | N  | -           | 고객명 또는 상품명 검색어        |
| `rating`    | N  | -           | 1 이상 5 이하             |
| `page`      | N  | `1`         | 1부터 시작                |
| `size`      | N  | `10`        | 1 이상 20 이하            |
| `sortBy`    | N  | `createdAt` | `createdAt`, `rating` |
| `sortOrder` | N  | `desc`      | `asc`, `desc`         |

Response Body

```json
{
  "status": 200,
  "message": "리뷰 리스트 조회 성공",
  "data": {
    "content": [
      {
        "id": 7001,
        "orderNumber": 20260427164000123,
        "customerName": "김고객",
        "productName": "무선 마우스",
        "rating": 5,
        "content": "배송이 빨라요",
        "createdAt": "2026-04-23T13:00:00",
        "updatedAt": "2026-04-23T13:00:00"
      }
    ],
    "page": 1,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### 리뷰 상세 조회

기능 설명

특정 상품에 속한 리뷰의 상세 정보를 조회합니다. 리뷰 작성 고객의 이름과 이메일, 상품명, 평점, 내용, 작성/수정 일시를 제공합니다.

`GET /admin/products/{productId}/reviews/{reviewId}`

Response Body

```json
{
  "status": 200,
  "message": "리뷰 상세 조회 성공",
  "data": {
    "id": 7001,
    "productName": "무선 마우스",
    "customerName": "김고객",
    "customerEmail": "customer@example.com",
    "rating": 5,
    "content": "배송이 빨라요",
    "createdAt": "2026-04-23T13:00:00",
    "updatedAt": "2026-04-23T13:00:00"
  }
}
```

### 리뷰 삭제

기능 설명

특정 상품에 속한 리뷰를 소프트 삭제 처리합니다. 실제 데이터를 물리 삭제하지 않고 삭제 상태로 전환하며, 이미 삭제된 리뷰는 중복 삭제할 수 없습니다.

`DELETE /admin/products/{productId}/reviews/{reviewId}`

Response Body

```json
{
  "status": 200,
  "message": "리뷰 삭제 완료"
}
```
