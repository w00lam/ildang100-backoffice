INSERT INTO admins
(id, name, email, password, tele, status, role, created_at, approved_at, updated_at)
VALUES
    (1, 'admin', 'admin@sparta.com', '$2a$10$TSPNz8qpjtfwiUbVZxI2LOWBMEGZ03OeXaQzvF19j8tkMOOT9wTfe', '010-0000-0000',
     'ACTIVE', 'SUPER_ADMIN', NOW(), NOW(), NOW()),
    (2, '김운영', 'operation@sparta.com', '$2a$10$MNpaDSOm4to665V0JnEB/.Z8EGqUZqa9URNXbuUCNMx.cGPdgYKNW', '010-1111-1111',
     'ACTIVE', 'OPERATIONS_ADMIN', NOW(), NOW(), NOW()),
    (3, '김철수', 'kim@sparta.com', '$2a$10$MNpaDSOm4to665V0JnEB/.Z8EGqUZqa9URNXbuUCNMx.cGPdgYKNW', '010-6666-6666',
     'ACTIVE', 'OPERATIONS_ADMIN', NOW(), NOW(), NOW()),
    (4, '박민수', 'park@sparta.com', '$2a$10$MNpaDSOm4to665V0JnEB/.Z8EGqUZqa9URNXbuUCNMx.cGPdgYKNW', '010-8888-8888',
     'ACTIVE', 'OPERATIONS_ADMIN', NOW(), NOW(), NOW()),
    (5, '최동욱', 'choi@sparta.com', '$2a$10$MNpaDSOm4to665V0JnEB/.Z8EGqUZqa9URNXbuUCNMx.cGPdgYKNW', '010-1010-1010',
     'ACTIVE', 'OPERATIONS_ADMIN', NOW(), NOW(), NOW()),
    (6, '최거부', 'rejected@sparta.com', '$2a$10$MNpaDSOm4to665V0JnEB/.Z8EGqUZqa9URNXbuUCNMx.cGPdgYKNW', '010-4444-4444',
     'REJECTED', 'OPERATIONS_ADMIN', NOW(), NOW(), NOW()),
    (7, '이고객', 'cs@sparta.com', '$2a$10$MNpaDSOm4to665V0JnEB/.Z8EGqUZqa9URNXbuUCNMx.cGPdgYKNW', '010-2222-2222',
     'ACTIVE', 'CS_ADMIN', NOW(), NOW(), NOW()),
    (8, '이영희', 'lee@sparta.com', '$2a$10$MNpaDSOm4to665V0JnEB/.Z8EGqUZqa9URNXbuUCNMx.cGPdgYKNW', '010-7777-7777',
     'ACTIVE', 'CS_ADMIN', NOW(), NOW(), NOW()),
    (9, '박대기', 'pending@sparta.com', '$2a$10$MNpaDSOm4to665V0JnEB/.Z8EGqUZqa9URNXbuUCNMx.cGPdgYKNW', '010-3333-3333',
     'PENDING_APPROVAL', 'CS_ADMIN', NOW(), NOW(), NOW()),
    (10, '정정지', 'suspended@sparta.com', '$2a$10$MNpaDSOm4to665V0JnEB/.Z8EGqUZqa9URNXbuUCNMx.cGPdgYKNW', '010-5555-5555',
     'SUSPENDED', 'CS_ADMIN', NOW(), NOW(), NOW()),
    (11, '정수연', 'jung@sparta.com', '$2a$10$MNpaDSOm4to665V0JnEB/.Z8EGqUZqa9URNXbuUCNMx.cGPdgYKNW', '010-9999-9999',
     'INACTIVE', 'CS_ADMIN', NOW(), NOW(), NOW());

INSERT INTO customers
(id, name, email, tele, status, created_at, updated_at)
VALUES
    (1, '김민수', 'customer1@example.com', '010-1000-0001', 'ACTIVE', NOW(), NOW()),
    (2, '이서연', 'customer2@example.com', '010-1000-0002', 'ACTIVE', NOW(), NOW()),
    (3, '박지훈', 'customer3@example.com', '010-1000-0003', 'ACTIVE', NOW(), NOW()),
    (4, '최하은', 'customer4@example.com', '010-1000-0004', 'ACTIVE', NOW(), NOW()),
    (5, '정도윤', 'customer5@example.com', '010-1000-0005', 'ACTIVE', NOW(), NOW()),
    (6, '강서준', 'customer6@example.com', '010-1000-0006', 'ACTIVE', NOW(), NOW()),
    (7, '조아린', 'customer7@example.com', '010-1000-0007', 'ACTIVE', NOW(), NOW()),
    (8, '오지우', 'customer8@example.com', '010-1000-0008', 'ACTIVE', NOW(), NOW()),
    (9, '윤유준', 'customer9@example.com', '010-1000-0009', 'ACTIVE', NOW(), NOW()),
    (10, '한하늘', 'customer10@example.com', '010-1000-0010', 'ACTIVE', NOW(), NOW());

INSERT INTO products
(id, admin_id, name, category, price, stock, status, deletion_status, created_at, updated_at)
VALUES
    (1, 1, '무선 키보드',     '전자기기', 59000, 100, 'ON_SALE', 'NOT_DELETED', NOW(), NOW()),
    (2, 1, '블루투스 마우스', '전자기기', 39000,  80, 'ON_SALE', 'NOT_DELETED', NOW(), NOW()),
    (3, 1, '텀블러',         '생활용품', 18000,  50, 'ON_SALE', 'NOT_DELETED', NOW(), NOW());

--- 재고 부족
INSERT INTO products
(id, admin_id, name, category, price, stock, status, created_at, updated_at)
VALUES
    (4, 1, 'USB-C 케이블', '전자기기', 9000, 5, 'ON_SALE', NOW(), NOW()),
    (5, 1, '노트북 거치대', '전자기기', 29000, 3, 'ON_SALE', NOW(), NOW());

--- 품절
INSERT INTO products
(id, admin_id, name, category, price, stock, status, created_at, updated_at)
VALUES
    (6, 1, '게이밍 마우스패드', '전자기기', 15000, 0, 'OUT_OF_STOCK', NOW(), NOW()),
    (7, 1, '스마트폰 거치대', '전자기기', 12000, 0, 'OUT_OF_STOCK', NOW(), NOW());

-- 준비중 (오늘)
INSERT INTO orders (
    admin_id, customer_id, product_id,
    quantity, status, created_at, updated_at,
    order_number, unit_price, total_price
) VALUES
    (1, 1, 1, 2, 'PREPARING', NOW(), NOW(), 100001, 10000, 20000);

-- 배송중 (오늘)
INSERT INTO orders (
    admin_id, customer_id, product_id,
    quantity, status, created_at, updated_at,
    order_number, unit_price, total_price
) VALUES
    (1, 1, 1, 1, 'SHIPPING', NOW(), NOW(), 100002, 15000, 15000);

-- 배송완료 (오늘)
INSERT INTO orders (
    admin_id, customer_id, product_id,
    quantity, status, created_at, updated_at,
    order_number, unit_price, total_price
) VALUES
    (1, 1, 1, 3, 'DELIVERED', NOW(), NOW(), 100003, 8000, 24000);

-- 배송완료 (어제)
INSERT INTO orders (
    admin_id, customer_id, product_id,
    quantity, status, created_at, updated_at,
    order_number, unit_price, total_price
) VALUES
    (1, 1, 1, 2, 'DELIVERED',
     NOW() - INTERVAL 1 DAY,
     NOW() - INTERVAL 1 DAY,
     100004, 12000, 24000);

-- 취소 (오늘, 통계 제외 확인용)
INSERT INTO orders (
    admin_id, customer_id, product_id,
    quantity, status, created_at, updated_at,
    order_number, unit_price, total_price,
    cancel_reason
) VALUES
    (1, 1, 1, 1, 'CANCELLED',
     NOW(), NOW(),
     100005, 10000, 10000, '테스트 취소');

INSERT INTO reviews
(id, customer_id, product_id, order_id, rating, content, deletion_status, created_at, updated_at)
VALUES
    ( 1,  1, 1,  1, 5, '아주 만족합니다.',                'NOT_DELETED', NOW(), NOW()),
    ( 2,  2, 1,  2, 4, '배송도 빠르고 쓸만해요.',         'NOT_DELETED', NOW(), NOW()),
    ( 3,  3, 2,  3, 5, '사용하기 편하고 디자인이 좋습니다.', 'NOT_DELETED', NOW(), NOW()),
    ( 4,  4, 2,  4, 3, '무난한 상품입니다.',              'NOT_DELETED', NOW(), NOW()),
    ( 5,  5, 3,  5, 4, '가격 대비 괜찮습니다.',            'NOT_DELETED', NOW(), NOW());