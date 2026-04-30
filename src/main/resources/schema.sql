DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS history;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS admins;

DROP VIEW IF EXISTS dashboard_summary_view;
DROP VIEW IF EXISTS dashboard_widget_view;
DROP VIEW IF EXISTS review_rating_distribution_view;
DROP VIEW IF EXISTS customer_status_distribution_view;
DROP VIEW IF EXISTS product_category_distribution_view;

CREATE TABLE admins
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(30)  NOT NULL,
    email       VARCHAR(50)  NOT NULL,
    password    VARCHAR(255) NOT NULL,
    tele        VARCHAR(20)  NOT NULL,
    status      VARCHAR(20)  NOT NULL,
    role        VARCHAR(30)  NOT NULL,
    deletion_status VARCHAR(20) NOT NULL DEFAULT 'NOT_DELETED',
    created_at  DATETIME     NOT NULL,
    approved_at DATETIME,
    updated_at  DATETIME     NOT NULL
);

CREATE TABLE history
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    admin_id      BIGINT      NOT NULL,
    status        VARCHAR(30) NOT NULL,
    reject_reason VARCHAR(100),
    rejected_at   DATETIME,
    created_at    DATETIME    NOT NULL,
    updated_at    DATETIME    NOT NULL,
    CONSTRAINT fk_history_admin
        FOREIGN KEY (admin_id) REFERENCES admins (id)
);

CREATE TABLE customers
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(30) NOT NULL,
    email      VARCHAR(50) NOT NULL,
    tele       VARCHAR(20) NOT NULL,
    status     VARCHAR(20) NOT NULL,
    deletion_status VARCHAR(20) NOT NULL DEFAULT 'NOT_DELETED',
    created_at DATETIME    NOT NULL,
    updated_at DATETIME    NOT NULL
);

CREATE TABLE products
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    admin_id        BIGINT      NOT NULL,
    name            VARCHAR(30) NOT NULL,
    category        VARCHAR(30) NOT NULL,
    price           INT         NOT NULL,
    stock           INT         NOT NULL,
    status          VARCHAR(20) NOT NULL,
    deletion_status VARCHAR(20) NOT NULL DEFAULT 'NOT_DELETED', -- ⬅ 추가 (P-6)
    created_at      DATETIME    NOT NULL,
    updated_at      DATETIME    NOT NULL,
    CONSTRAINT fk_products_admin
        FOREIGN KEY (admin_id) REFERENCES admins (id)
);

CREATE TABLE orders
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    admin_id      BIGINT,
    customer_id   BIGINT      NOT NULL,
    product_id    BIGINT      NOT NULL,
    quantity      INT         NOT NULL,
    status        VARCHAR(20) NOT NULL,
    created_at    DATETIME    NOT NULL,
    updated_at    DATETIME    NOT NULL,
    order_number  BIGINT      NOT NULL,
    unit_price    INT         NOT NULL,
    total_price   INT         NOT NULL,
    cancel_reason VARCHAR(255),
    CONSTRAINT uk_orders_order_number
        UNIQUE (order_number),
    CONSTRAINT fk_orders_admin
        FOREIGN KEY (admin_id) REFERENCES admins (id),
    CONSTRAINT fk_orders_customer
        FOREIGN KEY (customer_id) REFERENCES customers (id),
    CONSTRAINT fk_orders_product
        FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE TABLE reviews
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id     BIGINT       NOT NULL,
    product_id      BIGINT       NOT NULL,
    order_id        BIGINT       NOT NULL,                       -- ⬅ 추가 (Review entity 정합)
    rating          INT          NOT NULL,
    content         VARCHAR(500) NOT NULL,                       -- ⬅ 변경 255 → 500 (entity 정합)
    deletion_status VARCHAR(20)  NOT NULL DEFAULT 'NOT_DELETED', -- ⬅ 추가 (R-3)
    created_at      DATETIME     NOT NULL,
    updated_at      DATETIME     NOT NULL,
    CONSTRAINT fk_reviews_customer
        FOREIGN KEY (customer_id) REFERENCES customers (id),
    CONSTRAINT fk_reviews_product
        FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT fk_reviews_order
        FOREIGN KEY (order_id) REFERENCES orders (id)            -- ⬅ 추가
);

CREATE VIEW dashboard_summary_view AS
SELECT 1                                                    AS id,

       COALESCE((SELECT COUNT(*)
                 FROM admins a
                 WHERE a.deletion_status != 'DELETED'
             ), 0)          AS total_admins,

       COALESCE((SELECT COUNT(*)
                 FROM admins a
                 WHERE a.deletion_status != 'DELETED'
                   AND a.status = 'ACTIVE'), 0)             AS active_admins,

       COALESCE((SELECT COUNT(*)
                 FROM customers c
                 WHERE c.deletion_status != 'DELETED'
             ), 0)          AS total_customers,

       COALESCE((SELECT COUNT(*)
                 FROM customers c
                 WHERE c.deletion_status != 'DELETED'
                   AND c.status = 'ACTIVE'), 0)             AS active_customers,

       COALESCE((SELECT COUNT(*)
                 FROM products p
                 WHERE p.deletion_status != 'DELETED'
             ), 0) AS total_products,

       COALESCE((SELECT COUNT(*)
                 FROM products p
                 WHERE p.deletion_status != 'DELETED'
                   AND p.stock <= 5
                   AND p.stock > 0
             ), 0) AS low_stock_products,

       COALESCE((SELECT COUNT(*)
                 FROM orders o
                 WHERE o.status != 'CANCELLED'
             ), 0)          AS total_orders,

       COALESCE((SELECT COUNT(*)
                 FROM orders o
                 WHERE DATE (o.created_at) = CURRENT_DATE
                    AND o.status != 'CANCELLED'
        ), 0)              AS today_orders,

       COALESCE((SELECT COUNT(*)
                 FROM reviews r
                 WHERE r.deletion_status != 'DELETED'
             ), 0) AS total_reviews,

       COALESCE((SELECT AVG(r.rating)
                 FROM reviews r
                 WHERE r.deletion_status != 'DELETED'
             ), 0) AS average_rating;

CREATE VIEW dashboard_widget_view AS
SELECT 1                                           AS id,

       COALESCE((SELECT SUM(o.total_price)
                 FROM orders o
                 WHERE o.status != 'CANCELLED'
             ), 0) AS total_sales,

       COALESCE((SELECT SUM(o.total_price)
                 FROM orders o
                 WHERE DATE (o.created_at) = CURRENT_DATE
                    AND o.status != 'CANCELLED'
        ), 0)     AS today_sales,

       (SELECT COUNT(*)
        FROM orders o
        WHERE o.status = 'PREPARING')              AS preparing_orders,

       (SELECT COUNT(*)
        FROM orders o
        WHERE o.status = 'SHIPPING')               AS shipping_orders,

       (SELECT COUNT(*)
        FROM orders o
        WHERE o.status = 'DELIVERED')              AS delivered_orders,

       (SELECT COUNT(*)
        FROM products p
        WHERE p.stock <= 5
          AND p.stock > 0)                         AS low_stock_products,

       (SELECT COUNT(*)
        FROM products p
        WHERE p.stock = 0
           OR p.status = 'OUT_OF_STOCK')           AS out_of_stock_products;

CREATE VIEW review_rating_distribution_view AS
SELECT rating_options.rating AS rating,
       COALESCE(COUNT(r.id), 0) AS count
FROM (
         SELECT 1 AS rating
         UNION ALL SELECT 2
         UNION ALL SELECT 3
         UNION ALL SELECT 4
         UNION ALL SELECT 5
     ) rating_options
LEFT JOIN reviews r
       ON r.rating = rating_options.rating
      AND r.deletion_status != 'DELETED'
GROUP BY rating_options.rating;

CREATE VIEW customer_status_distribution_view AS
SELECT status_options.status AS status,
       COALESCE(COUNT(c.id), 0) AS count
FROM (
         SELECT 'ACTIVE' AS status
         UNION ALL SELECT 'INACTIVE'
         UNION ALL SELECT 'SUSPENDED'
     ) status_options
LEFT JOIN customers c
       ON c.status = status_options.status
      AND c.deletion_status != 'DELETED'
GROUP BY status_options.status;

CREATE VIEW product_category_distribution_view AS
SELECT p.category AS category,
       COUNT(*)   AS count
FROM products p
WHERE p.deletion_status != 'DELETED'
GROUP BY p.category;
