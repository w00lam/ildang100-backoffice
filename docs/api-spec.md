# API 명세서

### **API 명세서 공통 가이드**

#### 공통 Enum 정의

- 관리자 역할(`AdminRole`)
    - `SUPER_ADMIN`
    - `OPERATION_ADMIN`
    - `CS_ADMIN`
- 관리자 상태(`AdminStatus`)
    - `ACTIVE`
    - `INACTIVE`
    - `SUSPENDED`
    - `PENDING_APPROVAL`
    - `REJECTED`
- 고객 상태(`CustomerStatus`)
    - `ACTIVE`
    - `INACTIVE`
    - `SUSPENDED`
- 주문 상태(`OrderStatus`)
    - `PREPARING`
    - `SHIPPING`
    - `DELIVERED`
    - `CANCELLED`
- 상품 상태(`ProductStatus`)
    - `ON_SALE`
    - `OUT_OF_STOCK`
    - `DISCONTINUED`

#### 공통 응답 구조

모든 성공 및 실패 응답은 아래 `CommonApiResponse<T>` 구조를 따릅니다.

```json
{
  "status": 200,
  "message": "응답 메시지",
  "data": null
}
```

#### 전역 예외 처리

## 전역 예외 처리

비즈니스 로직 수행 중 발생하는 예외는 `ServiceException` 을 통해 관리되며,

`GlobalExceptionHandler` 에 의해 일관된 JSON 형태로 변환되어 클라이언트에게 전달됩니다.

### 공통 에러 응답 형식

```json

{
  "status": 200,
  "code": "EMAIL_DUPLICATE",
  "message": "이미 사용 중인 이메일입니다."
}
```

### 비즈니스 에러 코드 정의 (ErrorCode)

| 코드                                | HTTP 상태 | 메시지                           | 설명                               |
|-----------------------------------|---------|-------------------------------|----------------------------------|
| `VALIDATION_FAILED`               | 400     | 입력값이 올바르지 않습니다.               | 요청 본문, 파라미터, Path Variable 검증 실패 |
| `INVALID_ROLE`                    | 400     | 유효하지 않은 관리자 역할입니다.            | 관리자 역할 enum 이 아닌 값 요청            |
| `INVALID_ADMIN_STATUS`            | 400     | 유효하지 않은 관리자 상태입니다.            | 관리자 상태 enum 이 아닌 값 요청            |
| `INVALID_CUSTOMER_STATUS`         | 400     | 유효하지 않은 고객 상태입니다.             | 고객 상태 enum 이 아닌 값 요청             |
| `INVALID_PRODUCT_STATUS`          | 400     | 유효하지 않은 상품 상태입니다.             | 상품 상태 enum 이 아닌 값 요청             |
| `INVALID_ORDER_STATUS`            | 400     | 유효하지 않은 주문 상태입니다.             | 주문 상태 enum 이 아닌 값 요청             |
| `INVALID_APPROVAL_DECISION`       | 400     | 유효하지 않은 승인 처리 요청입니다.          | 관리자 승인/거부 요청값이 올바르지 않음           |
| `PASSWORD_CONFIRM_MISMATCH`       | 400     | 비밀번호 확인이 일치하지 않습니다.           | 새 비밀번호와 비밀번호 확인 불일치              |
| `INVALID_STOCK_VALUE`             | 400     | 재고값이 올바르지 않습니다.               | 상품 재고가 0 미만이거나 허용 범위를 벗어남        |
| `INVALID_QUANTITY`                | 400     | 주문 수량이 올바르지 않습니다.             | 주문 수량이 1 미만이거나 잘못된 값             |
| `UNAUTHORIZED`                    | 401     | 로그인이 필요합니다.                   | 인증되지 않은 사용자의 요청                  |
| `FORBIDDEN`                       | 403     | 접근 권한이 없습니다.                  | 권한이 없는 관리자의 요청                   |
| `ADMIN_NOT_APPROVED`              | 403     | 승인되지 않았거나 비활성 상태의 관리자입니다.     | 승인대기, 거부, 비활성 상태 관리자 로그인/접근      |
| `INVALID_PASSWORD`                | 403     | 현재 비밀번호가 올바르지 않습니다.           | 비밀번호 변경 시 현재 비밀번호 불일치            |
| `INVALID_CREDENTIALS`             | 404     | 이메일 또는 비밀번호가 올바르지 않습니다.       | 로그인 시 이메일/비밀번호 불일치               |
| `ADMIN_NOT_FOUND`                 | 404     | 관리자가 존재하지 않습니다.               | 존재하지 않는 관리자 조회/수정/삭제 시           |
| `CUSTOMER_NOT_FOUND`              | 404     | 고객이 존재하지 않습니다.                | 존재하지 않는 고객 조회/수정/삭제 시            |
| `PRODUCT_NOT_FOUND`               | 404     | 상품이 존재하지 않습니다.                | 존재하지 않는 상품 조회/수정/삭제 시            |
| `ORDER_NOT_FOUND`                 | 404     | 주문이 존재하지 않습니다.                | 존재하지 않는 주문 조회/수정/취소 시            |
| `REVIEW_NOT_FOUND`                | 404     | 리뷰가 존재하지 않습니다.                | 존재하지 않는 리뷰 조회/삭제 시               |
| `EMAIL_DUPLICATE`                 | 409     | 이미 사용 중인 이메일입니다.              | 관리자/고객 이메일 중복                    |
| `ALREADY_APPROVED_ADMIN`          | 409     | 이미 승인 처리된 관리자입니다.             | 이미 승인 또는 거부된 가입 요청 재처리           |
| `INSUFFICIENT_STOCK`              | 409     | 재고가 부족합니다.                    | 주문 생성 시 재고 부족                    |
| `INVALID_ORDER_STATUS_TRANSITION` | 409     | 허용되지 않는 주문 상태 변경입니다.          | 주문 상태 변경 규칙 위반                   |
| `ORDER_CANCEL_NOT_ALLOWED`        | 409     | 이미 취소되었거나 취소할 수 없는 주문입니다.     | 취소 불가 상태 주문 취소 요청                |
| `ADMIN_DELETE_NOT_ALLOWED`        | 409     | 삭제할 수 없는 관리자 상태입니다.           | 삭제 제한된 관리자 삭제 시                  |
| `CUSTOMER_DELETE_NOT_ALLOWED`     | 409     | 삭제할 수 없는 고객 상태입니다.            | 삭제 제한된 고객 삭제 시                   |
| `PRODUCT_DELETE_NOT_ALLOWED`      | 409     | 주문 또는 리뷰가 연결된 상품은 삭제할 수 없습니다. | 참조 중인 상품 삭제 시                    |

### 기타 서버 오류

| 코드                      | HTTP 상태 | 메시지               | 설명            |
|-------------------------|---------|-------------------|---------------|
| `INTERNAL_SERVER_ERROR` | 500     | 서버 내부 오류가 발생했습니다. | 처리되지 않은 예외 발생 |

### 대시 보드 API 명세서

#### 관리자 대시보드 종합 통계 조회

### 기능

관리자 대시보드에 필요한 주요 통계 정보를 조회합니다.

### Method / URL

`GET /admin/dashboard`

### 인증

필요

### Request Body

없음

### 요청 조건

- 없음

### Response Body

```json
{
  "status": 200,
  "message": "대시 보드 조회 성공",
  "data": {
    "adminCount": 5,
    "pendingAdminApprovalCount": 2,
    "customerCount": 128,
    "pendingCustomerApprovalCount": 100,
    "productCount": 54,
    "activeProductCount": 49,
    "orderCount": 321,
    "reviewCount": 210,
    "orderStatusSummary": {
      "PREPARE": 20,
      "SHIPPING": 15,
      "DELIVERED": 265,
      "CANCELED": 21
    }
  }
}
```

### 상태 코드

- `200 OK`

### 예외

- `401 Unauthorized: 로그인 필요`
- `403 Forbidden: 권한 부족`

### 리뷰 관리 API 명세서

#### 리뷰 리스트 조회

### 기능

리뷰 목록을 조회합니다.

### Method / URL

`GET /admin/products/{productId}/reviews`

### 인증

필요

### Query Parameter

- `keyword` (문자열, 선택): 검색 키워드 (customerName, productName)
- `page` (정수, 선택): 페이지 번호 (기본값: 1)
- `size` (정수, 선택): 페이지당 개수 (기본값: 10)
- `sortBy` (문자열, 선택): 정렬 기준 (rating, createdAt)
- `sortOrder` (문자열, 선택): 정렬 순서 (asc, desc)
- `rating` (정수, 선택): 평점 필터 (1~5)

### Request Body

없음

### 요청 조건

- `page`: 선택, 0 이상
- `size`: 선택, 1 이상

### Response Body

```json
{
  "status": 200,
  "message": "리뷰 리스트 조회 성공",
  "data": {
    "content": [
      {
        "id": 7001,
        "orderNumber": 1,
        "customerName": "김고객",
        "productName": "무선 마우스",
        "rating": 5,
        "content": "배송이 빨라요",
        "createdAt": "2026-04-23T13:00:00",
        "updatedAt": "2026-04-23T13:00:00"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### 상태 코드

- `200 OK`

### 예외

- `401 Unauthorized: 로그인 필요`

#### 리뷰 상세 조회

### 기능

특정 리뷰의 상세 정보를 조회합니다.

### Method / URL

`GET /admin/products/{productId}/reviews/{reviewId}`

### 인증

필요

### Path Variable

- `reviewId`: 리뷰 ID

### Request Body

없음

### 요청 조건

- `reviewId`: 필수, 양의 정수

### Response Body

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

### 상태 코드

- `200 OK`

### 예외

- `401 Unauthorized: 로그인 필요`
- `404 Not Found: 리뷰 없음`

#### 리뷰 삭제

### 기능

부적절한 리뷰를 관리자가 삭제합니다.

### Method / URL

`DELETE /admin/products/{productId}/reviews/{reviewId}`

### 인증

필요

### Path Variable

- `reviewId`: 리뷰 ID

### Request Body

없음

### 요청 조건

- `reviewId`: 필수, 양의 정수

### Response Body

```json
{
  "status": 200,
  "message": "리뷰 삭제 완료",
  "data": null
}
```

### 상태 코드

- `200 OK`

### 예외

- `401 Unauthorized: 로그인 필요`
- `404 Not Found: 리뷰 없음`

### 주문 관리 API 명세서

#### 주문 생성

### 기능

고객과 상품을 지정해 주문을 생성합니다.

### Method / URL

`POST /admin/orders`

### 인증

필요

### Request Body

```json
{
  "customerId": 101,
  "productId": 1001,
  "quantity": 2
}
```

### 요청 조건

- `customerId`: 필수, 양의 정수
- `productId`: 필수, 양의 정수
- `quantity`: 필수, 1 이상 정수

### Response Body

```json
{
  "status": 201,
  "message": "주문 생성 성공",
  "data": {
    "id": 5001,
    "customerId": 101,
    "productId": 1001,
    "quantity": 2,
    "status": "PREPARING",
    "createdAt": "2026-04-23T16:40:00",
    "updatedAt": "2026-04-23T16:40:00"
  }
}
```

### 상태 코드

- `201 Created`

### 예외

- `400 Bad Request: 요청값 검증 실패`
- `401 Unauthorized: 로그인 필요`
- `404 Not Found: 고객 또는 상품 없음`
- `409 Conflict: 재고 부족`

#### 주문 리스트 조회

### 기능

주문 목록을 조회합니다.

### Method / URL

`GET /admin/orders`

### 인증

필요

### Query Parameter

- `keyword` (문자열, 선택): 검색 키워드 (orderId, customerName)
- `page`: 선택, 0부터 시작
- `size`: 선택, 페이지 크기
- `sortBy` (문자열, 선택): 정렬 기준 (quantity, totalAmount, orderDate)
- `sortOrder` (문자열, 선택): 정렬 순서 (asc, desc)
- `status`: 선택, `OrderStatus`(`PREPERING`, `SHIPPING`, `DELIVERED`, `CANCELLED`)

### Request Body

없음

### 요청 조건

- `page`: 선택, 0 이상
- `size`: 선택, 1 이상
- `status`: 선택, `OrderStatus`(`PREPERING`, `SHIPPING`, `DELIVERED`, `CANCELLED`)

### Response Body

```json
{
  "status": 200,
  "message": "주문 리스트 조회 성공",
  "data": {
    "content": [
      {
        "id": 5001,
        "orderNumber": 1,
        "customerName": "김고객",
        "productName": "무선 마우스",
        "quantity": 2,
        "totalAmount": 1000000,
        "status": "PREPARING",
        "adminName": "운영관리자",
        "createdAt": "2026-04-23T16:40:00",
        "updatedAt": "2026-04-23T16:40:00"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### 상태 코드

- `200 OK`

### 예외

- `401 Unauthorized: 로그인 필요`

#### 주문 상세 조회

### 기능

특정 주문의 상세 정보를 조회합니다.

### Method / URL

`GET /admin/orders/{orderId}`

### 인증

필요

### Path Variable

- `orderId`: 주문 ID

### Request Body

없음

### 요청 조건

- `orderId`: 필수, 양의 정수

### Response Body

```json
{
  "status": 200,
  "message": "주문 상세 조회 성공",
  "data": {
    "id": 5001,
    "customerId": 101,
    "productId": 1001,
    "quantity": 2,
    "totalAmount": 1000000,
    "status": "PREPARING",
    "createdAt": "2026-04-23T16:40:00",
    "updatedAt": "2026-04-23T16:40:00"
  }
}
```

- **Response (성공 시 - 200 OK):**

**- 주문 상세 정보
반환: `orderNo`, `customerName`, `customerEmail`, `productName`, `quantity`, `totalAmount`, `orderDate`, `status`**

**- **CS 주문인 경우:** `adminName`, `adminEmail`, `adminRole` 포함 (고객 직접 주문이면 해당 필드는 null 처리)**

- **Response (실패 시 - 404 Not Found):** 존재하지 않는 ID 요청 시 에러 반환

### 상태 코드

- `200 OK`

### 예외

- `401 Unauthorized: 로그인 필요`
- `404 Not Found: 주문 없음`

#### 주문 상태 수정

### 기능

주문 상태를 변경합니다.

### Method / URL

`PUT /admin/orders/{orderId}/status`

### 인증

필요

### Path Variable

- `orderId`: 주문 ID

### Request Body

```json
{
  "status": "DELIVERED"
}
```

### 요청 조건

- `orderId`: 필수, 양의 정수
- `status`: 필수, 문자열, 최대 20자, `OrderStatus(PREPERING, SHIPPING, DELIVERED, CANCELLED)`

### Response Body

```json
{
  "status": 200,
  "message": "주문 상태 수정 완료",
  "data": {
    "id": 5001,
    "status": "DELIVERED",
    "updatedAt": "2026-04-23T17:00:00"
  }
}
```

### 상태 코드

- `200 OK`

### 예외

- `400 Bad Request: 유효하지 않은 상태값`
- `401 Unauthorized: 로그인 필요`
- `404 Not Found: 주문 없음`
- `409 Conflict: 허용되지 않는 주문 상태 전이`

#### 주문 취소

### 기능

특정 주문을 취소합니다.

### Method / URL

`PATCH /orders/{orderId}/cancel`

### 인증

필요

### Path Variable

- `orderId`: 주문 ID

### Request Body

없음

### 요청 조건

- `orderId`: 필수, 양의 정수

### Response Body

```json
{
  "status": 200,
  "message": "주문 취소 성공",
  "data": {
    "id": 5001,
    "status": "CANCELED",
    "updatedAt": "2026-04-23T17:10:00"
  }
}
```

### 상태 코드

- `200 OK`

### 예외

- `401 Unauthorized: 로그인 필요`
- `404 Not Found: 주문 없음`
- `409 Conflict: 이미 취소되었거나 취소 불가 상태`

### 관리자 관리 API 명세서

#### 관리자 리스트 조회

### 기능

관리자 목록을 조회합니다.

### Method / URL

`/admins`

### 인증

필요 (슈퍼 관리자 권한 권장)

### Query Parameter

- `keyword` (문자열, 선택): 검색 키워드 (adminName, adminEmail)
- `page`: 선택, 0부터 시작
- `size`: 선택, 페이지 크기
- `sortBy` (문자열, 선택): 정렬 기준 (adminName, adminEmail, createdAt)
- `sortOrder` (문자열, 선택): 정렬 순서 (asc, desc)
- `role`: 선택, `AdminRole`(`SUPER_ADMIN`, `OPERATION_ADMIN`, `CS_ADMIN`)
- `status`: 선택, `AdminStatus`(`ACTIVE`, `INACTIVE`, `SUSPENDED`, `PENDING_APPROVAL`, `REJECTED`)

### Request Body

없음

### 요청 조건

- `page`: 선택, 0 이상
- `size`: 선택, 1 이상
- `role`: 선택, `AdminRole`(`SUPER_ADMIN`, `OPERATION_ADMIN`, `CS_ADMIN`)
- `status`: 선택, `AdminStatus`(`ACTIVE`, `INACTIVE`, `SUSPENDED`, `PENDING_APPROVAL`, `REJECTED`)

### Response Body

```json
{
  "status": 200,
  "message": "관리자 리스트 조회 성공",
  "data": {
    "content": [
      {
        "id": 2,
        "name": "운영관리자",
        "email": "ops@example.com",
        "tele": "010-2222-3333",
        "role": "OPERATION_ADMIN",
        "status": "ACTIVE",
        "createdAt": "2026-04-21T10:00:00",
        "approvedAt": "2026-04-21T10:30:00",
        "updatedAt": "2026-04-21T11:00:00"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### 상태 코드

- `200 OK`

### 예외

- `401 Unauthorized: 로그인 필요`
- `403 Forbidden: 권한 부족`

#### 관리자 상세 조회

### 기능

특정 관리자의 상세 정보를 조회합니다.

### Method / URL

`GET /admins/{adminId}`

### 인증

필요 (슈퍼 관리자 권한 권장)

### Path Variable

- `adminId`: 관리자 ID

### Request Body

없음

### 요청 조건

- `adminId`: 필수, 양의 정수

### Response Body

```json
{
  "status": 200,
  "message": "관리자 상세 조회 성공",
  "data": {
    "id": 2,
    "name": "운영관리자",
    "email": "ops@example.com",
    "tele": "010-2222-3333",
    "role": "OPERATION_ADMIN",
    "status": "ACTIVE",
    "createdAt": "2026-04-21T10:00:00",
    "approvedAt": "2026-04-21T10:30:00",
    "updatedAt": "2026-04-21T11:00:00"
  }
}
```

### 상태 코드

- `200 OK`

### 예외

- `401 Unauthorized: 로그인 필요`
- `403 Forbidden: 권한 부족`
- `404 Not Found: 관리자 없음`

#### 관리자 정보 수정

### 기능

특정 관리자의 기본 정보를 수정합니다.

### Method / URL

`PUT /admins/{adminId}`

### 인증

필요 (슈퍼 관리자 전용)

### Path Variable

- `adminId`: 관리자 ID

### Request Body

```json
{
  "name": "운영관리자",
  "email": "ops@example.com",
  "tele": "010-9999-8888"
}
```

### 요청 조건

- `adminId`: 필수, 양의 정수
- `name`: 필수, 최대 30자
- `email`: 필수, 이메일 형식, 최대 50자
- `tele`: 필수, 최대 20자

### Response Body

```json
{
  "status": 200,
  "message": "관리자 정보 수정 성공",
  "data": {
    "id": 2,
    "name": "운영관리자",
    "email": "ops@example.com",
    "tele": "010-9999-8888",
    "role": "OPERATION_ADMIN",
    "status": "ACTIVE",
    "createdAt": "2026-04-21T10:00:00",
    "approvedAt": "2026-04-21T10:30:00",
    "updatedAt": "2026-04-23T15:20:00"
  }
}
```

### 상태 코드

- `200 OK`

### 예외

- `400 Bad Request: 요청값 검증 실패`
- `401 Unauthorized: 로그인 필요`
- `403 Forbidden: 권한 부족`
- `404 Not Found: 관리자 없음`
- `409 Conflict: 이미 사용 중인 이메일`

#### 관리자 역할 변경

### 기능

특정 관리자의 권한 역할을 변경합니다.

### Method / URL

`PUT /admins/{adminId}/role`

### 인증

필요 (슈퍼 관리자 전용)

### Path Variable

- `adminId`: 관리자 ID

### Request Body

```json
{
  "role": "CS_ADMIN"
}
```

### 요청 조건

- `adminId`: 필수, 양의 정수
- `role`: 필수, 문자열, 최대 30자, `AdminRole`(`SUPER_ADMIN`, `OPERATIONS_ADMIN`, `CS_ADMIN` )

### Response Body

```json
{
  "status": 200,
  "message": "관리자 역할 수정 성공",
  "data": {
    "id": 2,
    "name": "운영관리자",
    "email": "ops@example.com",
    "tele": "010-9999-8888",
    "role": "CS_ADMIN",
    "status": "ACTIVE",
    "createdAt": "2026-04-21T10:00:00",
    "approvedAt": "2026-04-21T10:30:00",
    "updatedAt": "2026-04-23T15:20:00"
  }
}
```

### 상태 코드

- `200 OK`

### 예외

- `400 Bad Request: 유효하지 않은 역할값`
- `401 Unauthorized: 로그인 필요`
- `403 Forbidden: 권한 부족`
- `404 Not Found: 관리자 없음`

#### 관리자 상태 변경

### 기능

특정 관리자의 상태를 변경합니다.

### Method / URL

`PUT /admins/{adminId}/status`

### 인증

필요 (슈퍼 관리자 전용)

### Path Variable

- `adminId`: 관리자 ID

### Request Body

```json
{
  "status": "INACTIVE"
}
```

### 요청 조건

- `adminId`: 필수, 양의 정수
- `status`: 필수, 문자열, 최대 20자, `AdminStatus`(`ACTIVE`, `INACTIVE`, `SUSPENDED`, `PENDING_APPROVAL`, `REJECTED`)

### Response Body

```json
{
  "status": 200,
  "message": "관리자 역할 수정 성공",
  "data": {
    "id": 2,
    "name": "운영관리자",
    "email": "ops@example.com",
    "tele": "010-9999-8888",
    "role": "CS_ADMIN",
    "status": "INACTIVE",
    "createdAt": "2026-04-21T10:00:00",
    "approvedAt": "2026-04-21T10:30:00",
    "updatedAt": "2026-04-23T15:20:00"
  }
}
```

### 상태 코드

- `200 OK`

### 예외

- `400 Bad Request: 유효하지 않은 상태값`
- `401 Unauthorized: 로그인 필요`
- `403 Forbidden: 권한 부족`
- `404 Not Found: 관리자 없음`

#### 관리자 삭제

### 기능

특정 관리자를 삭제합니다.

### Method / URL

`DELETE /admins/{adminId}`

### 인증

필요 (슈퍼 관리자 전용)

### Path Variable

- `adminId`: 관리자 ID

### Request Body

없음

### 요청 조건

- `adminId`: 필수, 양의 정수

### Response Body

```json
{
  "status": 200,
  "message": "관리자 삭제 완료",
  "data": null
}
```

### 상태 코드

- `200 OK`

### 예외

- `401 Unauthorized: 로그인 필요`
- `403 Forbidden: 권한 부족`
- `404 Not Found: 관리자 없음`
- `409 Conflict: 삭제할 수 없는 관리자 상태`

#### 관리자 가입 승인/거부

### 기능

관리자 가입 요청을 승인합니다.

### Method / URL

`PATCH /admins/{adminId}/approval`

### 인증

필요 (슈퍼 관리자 전용)

### Path Variable

- `adminId`: 관리자 ID
- `rejectReason` : 거부 사유

### Request Body

```json
{
  "isApproved": true,
  "rejectReason": null
}
```

### 요청 조건

- `isApproved`: 필수, `booelan`
- `rejectReason` : 선택, 최대 100자

### Response Body(승인)

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

### Response Body(거부)

```json
{
  "status": 200,
  "message": "관리자 거부 완료",
  "data": {
    "id": 2,
    "status": "REJECTED",
    "rejectReason": "그냥 해주기 싫음",
    "approvedAt": "2026-04-23T15:40:00",
    "updatedAt": "2026-04-23T15:40:00"
  }
}
```

### 상태 코드

- `200 OK`

### 예외

- `400 Bad Request: 유효하지 않은 승인 처리 요청`
- `401 Unauthorized: 로그인 필요`
- `403 Forbidden: 권한 부족`
- `404 Not Found: 관리자 없음`
- `409 Conflict: 이미 승인 처리된 관리자`

### 인증 API 명세서

#### 관리자 회원 가입

### 기능

새 관리자를 등록합니다. 가입 직후에는 승인 대기 상태로 생성합니다.

### Method / URL

`POST /admins/signup`

### 인증

불필요

### Request Body

```json
{
  "name": "홍길동",
  "email": "admin@example.com",
  "password": "P@ssw0rd!",
  "tele": "010-1234-5678",
  "role": "OPERATIONS_ADMIN"
}
```

### 요청 조건

- `name`: 필수, 공백 불가, 최대 30자
- `email`: 필수, 이메일 형식, 최대 50자
- `password`: 필수, 공백 불가, 최대 255자
- `tele`: 필수, 공백 불가, 최대 20자
- `role` : 필수, 공백 불가, 최대 30자, `AdminRole`(`SUPER_ADMIN`, `OPERATIONS_ADMIN`, `CS_ADMIN` )
- `approvedAt`: 서버 관리 값, 가입 시 `null` 허용

### Response Body

```json
{
  "status": 201,
  "message": "회원 가입 성공",
  "data": {
    "id": 1,
    "name": "홍길동",
    "email": "admin@example.com",
    "tele": "010-1234-5678",
    "status": "PENDING_APPROVAL",
    "role": "OPERATIONS_ADMIN",
    "createdAt": "2026-04-23T15:00:00",
    "approvedAt": null,
    "updatedAt": "2026-04-23T15:00:00"
  }
}
```

### 상태 코드

- `201 Created`

### 예외

- `400 Bad Request: 요청값 검증 실패`
- `409 Conflict: 이미 사용 중인 이메일`

#### 관리자 로그인

### 기능

관리자 계정으로 로그인합니다.

### Method / URL

`POST /admins/login`

### 인증

불필요

### Request Body

```json
{
  "email": "admin@example.com",
  "password": "P@ssw0rd!"
}
```

### 요청 조건

- `email`: 필수, 이메일 형식, 최대 50자
- `password`: 필수, 공백 불가, 최대 255자

> 세션 기반 인증입니다. 로그인 성공 시 응답 헤더의 `Set-Cookie` 에 세션 쿠키가 포함됩니다.
>

### Response Body

```json
{
  "status": 200,
  "message": "로그인 성공",
  "data": {
    "id": 1,
    "name": "홍길동",
    "email": "admin@example.com",
    "role": "SUPER_ADMIN",
    "status": "ACTIVE"
  }
}
```

### 응답 헤더

- `Set-Cookie: JSESSIONID=...; Path=/; HttpOnly`

### 상태 코드

- `200 OK`

### 예외

- `400 Bad Request: 요청값 검증 실패`
- `403 Forbidden: 승인되지 않았거나 비활성 상태의 관리자`
- `404 Unauthorized: 이메일 또는 비밀번호 불일치`

#### 관리자 로그아웃

### 기능

현재 로그인한 관리자 세션을 무효화합니다.

### Method / URL

`POST /admins/logout`

### 인증

필요

### Request Body

없음

### 요청 조건

- 없음

### Response Body

```json
{
  "status": 200,
  "message": "로그아웃 성공",
  "data": null
}
```

### 상태 코드

- `200 OK`

### 예외

- `401 Unauthorized: 로그인 필요`

#### 관리자 내 프로필 조회

### 기능

현재 로그인한 관리자의 프로필을 조회합니다.

### Method / URL

`GET /admins/me`

### 인증

필요

### Request Body

없음

### 요청 조건

- 없음

### Response Body

```json
{
  "id": 1,
  "name": "슈퍼관리자",
  "email": "superadmin@example.com",
  "tele": "010-1111-2222",
  "status": "ACTIVE",
  "role": "SUPER_ADMIN",
  "createdAt": "2026-04-20T09:00:00",
  "approvedAt": "2026-04-20T09:10:00",
  "updatedAt": "2026-04-20T09:10:00"
}
```

### 상태 코드

- `200 OK`

### 예외

- `401 Unauthorized: 로그인 필요`

#### 관리자 내 프로필 수정

### 기능

현재 로그인한 관리자의 프로필을 수정합니다.

### Method / URL

`PUT /admins/me`

### 인증

필요

### Request Body

```json
{
  "name": "슈퍼관리자",
  "email": "superadmin@example.com",
  "tele": "010-7777-8888"
}
```

### 요청 조건

- `name`: 선택, 최대 30자
- `email`: 선택, 이메일 형식, 최대 50자
- `tele`: 선택, 최대 20자

### Response Body

```json
{
  "id": 1,
  "name": "슈퍼관리자",
  "email": "superadmin@example.com",
  "tele": "010-7777-8888",
  "status": "ACTIVE",
  "role": "SUPER_ADMIN",
  "updatedAt": "2026-04-23T15:50:00"
}
```

### 상태 코드

- `200 OK`

### 예외

- `400 Bad Request: 요청값 검증 실패`
- `401 Unauthorized: 로그인 필요`
- `409 Conflict: 이미 사용 중인 이메일`

#### 관리자 비밀번호 변경

### 기능

현재 로그인한 관리자의 비밀번호를 변경합니다.

### Method / URL

`PATCH /admins/me/password`

### 인증

필요

### Request Body

```json
{
  "currentPassword": "OldP@ssw0rd!",
  "newPassword": "NewP@ssw0rd!",
  "newPasswordConfirm": "NewP@ssw0rd!"
}
```

### 요청 조건

- `currentPassword`: 필수
- `newPassword`: 필수, 최대 255자
- `newPasswordConfirm`: 필수, `newPassword` 와 일치해야 함

### Response Body

```json
{
  "message": "비밀번호 변경 완료"
}
```

### 상태 코드

- `200 OK`

### 예외

- `400 Bad Request: 비밀번호 확인 불일치`
- `401 Unauthorized: 로그인 필요`
- `403 Forbidden: 현재 비밀번호 불일치`

### 고객 관리 API 명세서

#### 고객 리스트 조회

### 기능

고객 목록을 조회합니다.

### Method / URL

`GET /admin/customers`

### 인증

필요

### Query Parameter

- `keyword`: 선택, 검색 키워드 (이름, 이메일)
- `page`: 선택, 1부터 시작 (기본값: 1)
- `size`: 선택, 페이지 크기 (기본값: 10)
- `sortBy`: 선택, 정렬 기준 (`name`, `email`, `createdAt`, 기본값: `createdAt`)
- `sortOrder`: 선택, 정렬 순서 (`asc`, `desc`, 기본값: `desc`)
- `status`: 선택, `CustomerStatus`(`ACTIVE`, `INACTIVE`, `SUSPENDED`)

### Request Body

없음

### 요청 조건

- `keyword`: 선택, 문자열
- `page`: 선택, 1 이상
- `size`: 선택, 1 이상
- `sortBy`: 선택, `name`, `email`, `createdAt` 중 하나
- `sortOrder`: 선택, `asc`, `desc` 중 하나
- `status`: 선택, `CustomerStatus`(`ACTIVE`, `INACTIVE`, `SUSPENDED`)

### Response Body

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
        "updatedAt": "2026-04-23T12:00:00"
      }
    ],
    "page": 1,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### 상태 코드

- `200 OK`

### 예외

- `400 Bad Request: 입력값이 올바르지 않음`
- `401 Unauthorized: 로그인 필요`

#### 고객 상세 조회

### 기능

특정 고객의 상세 정보를 조회합니다.

### Method / URL

`GET /admin/customers/{customerId}`

### 인증

필요

### Path Variable

- `customerId`: 고객 ID

### Request Body

없음

### 요청 조건

- `customerId`: 필수, 양의 정수

### Response Body

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
    "updatedAt": "2026-04-23T12:00:00"
  }
}
```

### 상태 코드

- `200 OK`

### 예외

- `400 Bad Request: 입력값이 올바르지 않음`
- `401 Unauthorized: 로그인 필요`
- `404 Not Found: 고객 없음`

#### 고객 정보 수정

### 기능

특정 고객의 기본 정보를 수정합니다.

### Method / URL

`PUT /admin/customers/{customerId}`

### 인증

필요

### Path Variable

- `customerId`: 고객 ID

### Request Body

```json
{
  "name": "김고객",
  "email": "customer@example.com",
  "tele": "010-5555-6666"
}
```

### 요청 조건

- `customerId`: 필수, 양의 정수
- `name`: 선택, 최대 30자
- `email`: 선택, 이메일 형식, 최대 30자
- `tele`: 선택, 최대 30자

### Response Body

```json
{
  "id": 101,
  "name": "김고객",
  "email": "customer@example.com",
  "tele": "010-5555-6666",
  "status": "ACTIVE",
  "updatedAt": "2026-04-23T16:00:00"
}
```

### 상태 코드

- `200 OK`

### 예외

- `400 Bad Request: 요청값 검증 실패`
- `401 Unauthorized: 로그인 필요`
- `404 Not Found: 고객 없음`
- `409 Conflict: 이미 사용 중인 이메일`

#### 고객 상태 변경

### 기능

특정 고객의 상태를 변경합니다.

### Method / URL

`PUT /admin/customers/{customerId}/status`

### 인증

필요

### Path Variable

- `customerId`: 고객 ID

### Request Body

```json
{
  "status": "INACTIVE"
}
```

### 요청 조건

- `customerId`: 필수, 양의 정수
- `status`: 필수, 문자열, 최대 30자, `CustomerStatus`(`ACTIVE`, `INACTIVE`, `SUSPENDED`)

### Response Body

```json
{
  "id": 101,
  "status": "INACTIVE",
  "updatedAt": "2026-04-23T16:05:00"
}
```

### 상태 코드

- `200 OK`

### 예외

- `400 Bad Request: 유효하지 않은 상태값`
- `401 Unauthorized: 로그인 필요`
- `404 Not Found: 고객 없음`

#### 고객 삭제

### 기능

특정 고객을 삭제합니다.

### Method / URL

`DELETE /admin/customers/{customerId}`

### 인증

필요

### Path Variable

- `customerId`: 고객 ID

### Request Body

없음

### 요청 조건

- `customerId`: 필수, 양의 정수

### Response Body

```json
{
  "message": "고객 삭제 완료"
}
```

### 상태 코드

- `200 OK`

### 예외

- `401 Unauthorized: 로그인 필요`
- `404 Not Found: 고객 없음`
- `409 Conflict: 삭제할 수 없는 고객 상태`

### 상품 관리 API 명세서

#### 상품 등록

### Method / URL

`POST /admin/products`

### 인증

필요

### Request Body

```json
{
  "name": "무선 마우스",
  "category": "ON_SALE",
  "price": 35000,
  "stock": 100,
  "status": "ON_SALE"
}
```

### 요청 조건

- `name`: 필수, 공백 불가, 최대 30자
- `category`: 필수, 공백 불가, 최대 30자
- `price`: 필수, 0 이상 정수
- `stock`: 필수, 0 이상 정수
- `status`: 필수, 문자열, 최대 20자, `ProductStatus`(`ON_SALE`, `OUT_OF_STOCK`, `DISCONTINUED`)

### Response Body

```json
{
  "status": 200,
  "message": "상품 생성 완료",
  "data": {
    "id": 1001,
    "adminId": 1,
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

### 상태 코드

- `201 Created`

### 예외

- `400 Bad Request: 요청값 검증 실패`
- `401 Unauthorized: 로그인 필요`

새 상품을 등록합니다. 등록 관리자 ID는 인증 정보에서 식별되는 흐름을 기준으로 작성했습니다.

#### 상품 리스트 조회

### 기능

상품 목록을 조회합니다.

### Method / URL

`GET /admin/products`

### 인증

필요

### Query Parameter

- `keyword` (문자열, 선택): 검색 키워드 (name)
- `page`: 선택, 0부터 시작
- `size`: 선택, 페이지 크기
- `sortBy` (문자열, 선택): 정렬 기준 (price, stock, createdAt)
- `sortOrder` (문자열, 선택): 정렬 순서 (asc, desc)
- `status`: 선택, `ProductStatus`(`ON_SALE`, `OUT_OF_STOCK`, `DISCONTINUED`)

### Request Body

없음

### 요청 조건

- `page`: 선택, 0 이상
- `size`: 선택, 1 이상
- `status`: 선택, `ProductStatus`(`ON_SALE`, `OUT_OF_STOCK`, `DISCONTINUED`)

### Response Body

```json
{
  "status": 200,
  "message": "상품 리스트 조회 성공",
  "data": {
    "content": [
      {
        "id": 1001,
        "adminName": "슈퍼관리자",
        "name": "무선 마우스",
        "category": "전자기기",
        "price": 35000,
        "stock": 100,
        "status": "ON_SALE",
        "createdAt": "2026-04-23T16:10:00",
        "updatedAt": "2026-04-23T16:10:00"
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

### 상태 코드

- `200 OK`

### 예외

- `401 Unauthorized: 로그인 필요`

#### 상품 상세 조회

### 기능

특정 상품의 상세 정보를 조회합니다.

### Method / URL

`GET /admin/products/{productId}`

### 인증

필요

### Path Variable

- `productId`: 상품 ID

### Request Body

없음

### 요청 조건

- `productId`: 필수, 양의 정수

### Response Body

```json
{
  "status": 200,
  "message": "상품 상세 조회 성공",
  "data": {
    "id": 1001,
    "name": "무선 마우스",
    "category": "전자기기",
    "price": 35000,
    "stock": 100,
    "status": "ON_SALE",
    "adminName": "슈퍼관리자",
    "adminEmail": "amdin@example.com",
    "createdAt": "2026-04-23T16:10:00",
    "updatedAt": "2026-04-23T16:10:00"
  }
}
```

### 상태 코드

- `200 OK`

### 예외

- `401 Unauthorized: 로그인 필요`
- `404 Not Found: 상품 없음`

#### 상품 정보 수정

### 기능

상품의 기본 정보를 수정합니다.

### Method / URL

`PUT /admin/products/{productId}`

### 인증

필요

### Path Variable

- `productId`: 상품 ID

### Request Body

```json
{
  "name": "무선 마우스 PRO",
  "category": "전자기기",
  "price": 39000
}
```

### 요청 조건

- `productId`: 필수, 양의 정수
- `name`: 선택, 최대 30자
- `category`: 선택, 최대 30자
- `price`: 선택, 0 이상 정수

### Response Body

```json
{
  "status": 200,
  "message": "상품 정보 수정 성공",
  "data": {
    "id": 1001,
    "name": "무선 마우스 PRO",
    "category": "전자기기",
    "price": 39000,
    "stock": 100,
    "status": "ON_SALE",
    "updatedAt": "2026-04-23T16:20:00"
  }
}
```

### 상태 코드

- `200 OK`

### 예외

- `400 Bad Request: 요청값 검증 실패`
- `401 Unauthorized: 로그인 필요`
- `404 Not Found: 상품 없음`

#### 상품 재고 변경

### 기능

상품 재고 수량을 변경합니다.

### Method / URL

`PUT /admin/products/{productId}/stock`

### 인증

필요

### Path Variable

- `productId`: 상품 ID

### Request Body

```json
{
  "stock": 85
}
```

### 요청 조건

- `productId`: 필수, 양의 정수
- `stock`: 필수, 0 이상 정수

### Response Body

```json
{
  "status": 200,
  "message": "상품 재고 수정 성공",
  "data": {
    "id": 1001,
    "stock": 85,
    "updatedAt": "2026-04-23T16:25:00"
  }
}
```

### 상태 코드

- `200 OK`

### 예외

- `400 Bad Request: 유효하지 않은 재고값`
- `401 Unauthorized: 로그인 필요`
- `404 Not Found: 상품 없음`

#### 상품 상태 변경

### 기능

상품 판매 상태를 변경합니다.

### Method / URL

`PUT /admin/products/{productId}/status`

### 인증

필요

### Path Variable

- `productId`: 상품 ID

### Request Body

```json
{
  "status": "ON_SALE"
}
```

### 요청 조건

- `productId`: 필수, 양의 정수
- `status`: 필수, 문자열, 최대 20자, `ProductStatus`(`ON_SALE`, `OUT_OF_STOCK`, `DISCONTINUED`)

### Response Body

```json
{
  "status": 200,
  "message": "상품 재고 변경 성공",
  "data": {
    "id": 1001,
    "status": "ON_SALE",
    "updatedAt": "2026-04-23T16:30:00"
  }
}
```

### 상태 코드

- `200 OK`

### 예외

- `400 Bad Request: 유효하지 않은 상태값`
- `401 Unauthorized: 로그인 필요`
- `404 Not Found: 상품 없음`

#### 상품 삭제

### 기능

특정 상품을 삭제합니다.

### Method / URL

`DELETE /admin/products/{productId}`

### 인증

필요

### Path Variable

- `productId`: 상품 ID

### Request Body

없음

### 요청 조건

- `productId`: 필수, 양의 정수

### Response Body

```json
{
  "status": 200,
  "message": "상품 삭제 완료",
  "data": null
}
```

### 상태 코드

- `200 OK`

### 예외

- `401 Unauthorized: 로그인 필요`
- `404 Not Found: 상품 없음`
- `409 Conflict: 주문 또는 리뷰가 연결된 상품`
