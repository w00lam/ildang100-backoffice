INSERT INTO admins
(id, name, email, password, tele, status, role, created_at, approved_at, updated_at)
VALUES
    (1, 'admin', 'admin@sparta.com', '$2a$04$0slsLbTBojZGxBjb/sQ5he4U28QNqFn0bzwSae8LcqTDKwtlqFCC2', '010-0000-0000',
     'ACTIVE', 'SUPER_ADMIN', NOW(), NOW(), NOW()),
    (2, '김운영', 'operation@sparta.com', '$2a$04$3OrPMqoEYFxaZ0Sw5o6qPuso4gsLPHcosPPyJYfZ/qbZElSirWuim', '010-1111-1111',
     'ACTIVE', 'OPERATIONS_ADMIN', NOW(), NOW(), NOW()),
    (3, '김철수', 'kim@sparta.com', '$2a$04$3OrPMqoEYFxaZ0Sw5o6qPuso4gsLPHcosPPyJYfZ/qbZElSirWuim', '010-6666-6666',
     'ACTIVE', 'OPERATIONS_ADMIN', NOW(), NOW(), NOW()),
    (4, '박민수', 'park@sparta.com', '$2a$04$3OrPMqoEYFxaZ0Sw5o6qPuso4gsLPHcosPPyJYfZ/qbZElSirWuim', '010-8888-8888',
     'ACTIVE', 'OPERATIONS_ADMIN', NOW(), NOW(), NOW()),
    (5, '최동욱', 'choi@sparta.com', '$2a$04$3OrPMqoEYFxaZ0Sw5o6qPuso4gsLPHcosPPyJYfZ/qbZElSirWuim', '010-1010-1010',
     'ACTIVE', 'OPERATIONS_ADMIN', NOW(), NOW(), NOW()),
    (6, '최거부', 'rejected@sparta.com', '$2a$04$3OrPMqoEYFxaZ0Sw5o6qPuso4gsLPHcosPPyJYfZ/qbZElSirWuim', '010-4444-4444',
     'REJECTED', 'OPERATIONS_ADMIN', NOW(), NOW(), NOW()),
    (7, '이고객', 'cs@sparta.com', '$2a$04$3OrPMqoEYFxaZ0Sw5o6qPuso4gsLPHcosPPyJYfZ/qbZElSirWuim', '010-2222-2222',
     'ACTIVE', 'CS_ADMIN', NOW(), NOW(), NOW()),
    (8, '이영희', 'lee@sparta.com', '$2a$04$3OrPMqoEYFxaZ0Sw5o6qPuso4gsLPHcosPPyJYfZ/qbZElSirWuim', '010-7777-7777',
     'ACTIVE', 'CS_ADMIN', NOW(), NOW(), NOW()),
    (9, '박대기', 'pending@sparta.com', '$2a$04$3OrPMqoEYFxaZ0Sw5o6qPuso4gsLPHcosPPyJYfZ/qbZElSirWuim', '010-3333-3333',
     'PENDING_APPROVAL', 'CS_ADMIN', NOW(), NOW(), NOW()),
    (10, '정정지', 'suspended@sparta.com', '$2a$04$3OrPMqoEYFxaZ0Sw5o6qPuso4gsLPHcosPPyJYfZ/qbZElSirWuim', '010-5555-5555',
     'SUSPENDED', 'CS_ADMIN', NOW(), NOW(), NOW()),
    (11, '정수연', 'jung@sparta.com', '$2a$04$3OrPMqoEYFxaZ0Sw5o6qPuso4gsLPHcosPPyJYfZ/qbZElSirWuim', '010-9999-9999',
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
(id, admin_id, name, category, price, stock, status, created_at, updated_at)
VALUES
    (1, 1, '무선 키보드', '전자기기', 59000, 100, 'ON_SALE', NOW(), NOW()),
    (2, 1, '블루투스 마우스', '전자기기', 39000, 80, 'ON_SALE', NOW(), NOW()),
    (3, 1, '텀블러', '생활용품', 18000, 50, 'ON_SALE', NOW(), NOW());

INSERT INTO reviews
(id, customer_id, product_id, rating, content, created_at, updated_at)
VALUES
    (1, 1, 1, 5, '아주 만족합니다.', NOW(), NOW()),
    (2, 2, 1, 4, '배송도 빠르고 쓸만해요.', NOW(), NOW()),
    (3, 3, 2, 5, '사용하기 편하고 디자인이 좋습니다.', NOW(), NOW()),
    (4, 4, 2, 3, '무난한 상품입니다.', NOW(), NOW()),
    (5, 5, 3, 4, '가격 대비 괜찮습니다.', NOW(), NOW()),
    (6, 6, 3, 5, '재구매 의사 있습니다.', NOW(), NOW()),
    (7, 7, 1, 4, '전반적으로 만족합니다.', NOW(), NOW()),
    (8, 8, 2, 2, '생각보다 아쉬웠습니다.', NOW(), NOW()),
    (9, 9, 3, 5, '선물용으로 좋습니다.', NOW(), NOW()),
    (10, 10, 1, 5, '추천합니다.', NOW(), NOW());
